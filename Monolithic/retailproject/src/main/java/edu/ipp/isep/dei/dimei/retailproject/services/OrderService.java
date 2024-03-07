package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final MerchantOrderService merchantOrderService;
    private final ShippingOrderService shippingOrderService;
    private final ItemService itemService;

    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orders = new ArrayList<>();

        this.orderRepository.findAll().forEach(order -> orders.add(new OrderDTO(order)));
        return orders;
    }

    public List<OrderDTO> getUserOrders(String authorizationToken) {
        User user = this.userService.getUserByToken(authorizationToken);

        List<OrderDTO> orders = new ArrayList<>();

        this.orderRepository.findByUser(user).forEach(order -> orders.add(new OrderDTO(order)));
        return orders;
    }

    public Order createOrder(String authorizationToken, OrderCreateDTO orderDTO) throws NotFoundException, InvalidQuantityException, BadPayloadException {
        User user = this.userService.getUserByToken(authorizationToken);

        isSameMerchantForAllItems(authorizationToken, orderDTO.getOrderItems(), orderDTO.getMerchantId());

        Address address = orderDTO.getAddressDTO().dtoToEntity();

        Order order = this.orderRepository.save(orderDTO.dtoToEntity(user));

        MerchantOrder merchantOrder = this.merchantOrderService.createMerchantOrder(user, order, orderDTO.getMerchantId());
        this.shippingOrderService.createShippingOrder(user, order, merchantOrder, address);

        for (ItemQuantity itemQuantity : order.getItemQuantities()) {
            this.itemService.removeItemStock(authorizationToken, itemQuantity.getItem().getId(), new ItemUpdateDTO(itemQuantity.getItem()));
        }

        return order;
    }

    private void isSameMerchantForAllItems(String authorizationToken, List<ItemQuantityDTO> itemQuantityDTOS, int merchantId) throws NotFoundException, BadPayloadException {
        ItemDTO itemDTO;
        for (ItemQuantityDTO itemQuantityDTO : itemQuantityDTOS) {
            itemDTO = this.itemService.getUserItem(authorizationToken, itemQuantityDTO.getItemId());
            if (itemDTO.getMerchant().getId() != merchantId)
                throw new BadPayloadException("Only can order items form same merchant");
        }
    }

    public OrderDTO getUserOrder(String authorizationToken, int id) throws NotFoundException {
        Order order = getUserOrderById(authorizationToken, id);
        return new OrderDTO(order);
    }

    public OrderDTO deleteOrder(int userId, int orderId) throws NotFoundException {
        Order order = this.orderRepository.findById(orderId).filter(o -> o.getUser().getId() == userId).orElseThrow(() -> new NotFoundException("Order not found"));

        this.orderRepository.delete(order);

        return new OrderDTO(order);
    }

    public OrderUpdateDTO fullCancelOrder(String authorizationToken, int id, OrderUpdateDTO orderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (!isIdEqualToOrderId(id, orderUpdateDTO)) {
            throw new BadPayloadException("Wrong order payload.");
        }

        Order order = changeOrderStatus(authorizationToken, orderUpdateDTO.getId(), OrderStatusEnum.CANCELLED);

        this.merchantOrderService.fullCancelOrderByOrder(authorizationToken, order);
        this.shippingOrderService.fullCancelOrderByOrder(authorizationToken, order);

        for (ItemQuantity itemQuantity : order.getItemQuantities()) {
            this.itemService.addItemStock(authorizationToken, itemQuantity.getItem().getId(), new ItemUpdateDTO(itemQuantity.getItem()));
        }

        return new OrderUpdateDTO(order);
    }

    public OrderUpdateDTO fullCancelOrderByOrderId(String authorizationToken, int orderId) throws NotFoundException, WrongFlowException {
        return new OrderUpdateDTO(changeOrderStatus(authorizationToken, orderId, OrderStatusEnum.CANCELLED));
    }

    public OrderUpdateDTO rejectOrder(String authorizationToken, int id, OrderUpdateDTO orderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (!isIdEqualToOrderId(id, orderUpdateDTO)) {
            throw new BadPayloadException("Wrong order payload.");
        }

        Order order = changeOrderStatus(authorizationToken, orderUpdateDTO.getId(), OrderStatusEnum.REJECTED);

        this.merchantOrderService.rejectMerchantOrderByOrder(authorizationToken, order);
        this.shippingOrderService.rejectShippingOrderByOrder(authorizationToken, order);

        for (ItemQuantity itemQuantity : order.getItemQuantities()) {
            this.itemService.addItemStock(authorizationToken, itemQuantity.getItem().getId(), new ItemUpdateDTO(itemQuantity.getItem()));
        }

        return new OrderUpdateDTO(order);
    }

    public OrderUpdateDTO rejectOrderByOrderId(String authorizationToken, int orderId) throws NotFoundException, WrongFlowException {
        return new OrderUpdateDTO(changeOrderStatus(authorizationToken, orderId, OrderStatusEnum.REJECTED));
    }

    public OrderUpdateDTO approveOrder(String authorizationToken, int id, OrderUpdateDTO orderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (!isIdEqualToOrderId(id, orderUpdateDTO)) {
            throw new BadPayloadException("Wrong order payload.");
        }

        Order order = changeOrderStatus(authorizationToken, orderUpdateDTO.getId(), OrderStatusEnum.APPROVED);

        return new OrderUpdateDTO(order);
    }

    private Order getUserOrderById(String authorizationToken, int id) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        return this.orderRepository.findById(id).filter(o -> o.getUser() == user).orElseThrow(() -> new NotFoundException("Order not found"));
    }

    private Order changeOrderStatus(String authorizationToken, int id, OrderStatusEnum status) throws NotFoundException, WrongFlowException {
        Order order = getUserOrderById(authorizationToken, id);

        if (isOrderFlowValid(authorizationToken, order, status)) {
            order.setStatus(status);
            return this.orderRepository.save(order);
        } else {
            throw new WrongFlowException("It is not possible to change Order status");
        }
    }

    public void shipOrder(String authorizationToken, int id) throws NotFoundException, WrongFlowException {
        changeOrderStatus(authorizationToken, id, OrderStatusEnum.SHIPPED);
    }

    public void deliverOrder(String authorizationToken, int id) throws NotFoundException, WrongFlowException {
        changeOrderStatus(authorizationToken, id, OrderStatusEnum.DELIVERED);
    }

    private boolean isOrderFlowValid(String authorizationToken, Order order, OrderStatusEnum newStatus) throws NotFoundException {
        MerchantOrderDTO merchantOrderDTO = this.merchantOrderService.getUserMerchantOrder(authorizationToken, order.getId());
        ShippingOrderDTO shippingOrderDTO = this.shippingOrderService.getUserShippingOrder(authorizationToken, order.getId());

        switch (newStatus) {
            case PENDING -> {
                return true;
            }
            case APPROVED -> {
                return order.getStatus().compareTo(OrderStatusEnum.PENDING) == 0 &&
                        (merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.PENDING) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0)
                        && (shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.PENDING) == 0 ||
                        shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.APPROVED) == 0);
            }
            case REJECTED -> {
                return (order.getStatus().compareTo(OrderStatusEnum.PENDING) == 0 ||
                        order.getStatus().compareTo(OrderStatusEnum.APPROVED) == 0) &&
                        (merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.PENDING) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.REJECTED) == 0) &&
                        (shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.PENDING) == 0 ||
                                shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.APPROVED) == 0 ||
                                shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.REJECTED) == 0);
            }
            case CANCELLED -> {
                return (order.getStatus().compareTo(OrderStatusEnum.PENDING) == 0 ||
                        order.getStatus().compareTo(OrderStatusEnum.APPROVED) == 0) &&
                        (merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.PENDING) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.CANCELLED) == 0) &&
                        (shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.PENDING) == 0 ||
                                shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.APPROVED) == 0 ||
                                shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.CANCELLED) == 0);
            }
            case SHIPPED -> {
                return order.getStatus().compareTo(OrderStatusEnum.APPROVED) == 0 &&
                        (merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.SHIPPED) == 0) &&
                        shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.SHIPPED) == 0;
            }
            case DELIVERED -> {
                return order.getStatus().compareTo(OrderStatusEnum.SHIPPED) == 0 &&
                        (merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.SHIPPED) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.DELIVERED) == 0) &&
                        shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.DELIVERED) == 0;
            }
        }
        return false;
    }

    private boolean isIdEqualToOrderId(int id, OrderUpdateDTO orderUpdateDTO) {
        return id == orderUpdateDTO.getId();
    }
}
