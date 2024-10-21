package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
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

    protected static final String NOTFOUNDEXCEPTIONMESSAGE = "Order not found.";
    protected static final String BADPAYLOADEXCEPTIONMESSAGE = "Wrong order payload.";
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final MerchantOrderService merchantOrderService;
    private final ShippingOrderService shippingOrderService;
    private final ItemService itemService;
    private final PaymentService paymentService;
    private final AddressService addressService;
    private final ItemQuantityService itemQuantityService;

    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orders = new ArrayList<>();

        this.orderRepository.findAll().forEach(order -> orders.add(new OrderDTO(order)));
        return orders;
    }

    public List<OrderDTO> getUserOrders(String authorizationToken) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        List<OrderDTO> orders = new ArrayList<>();

        this.orderRepository.findByUser(user).forEach(order -> orders.add(new OrderDTO(order)));
        return orders;
    }

    public OrderDTO createOrder(String authorizationToken, OrderCreateDTO orderDTO) throws NotFoundException, InvalidQuantityException, BadPayloadException {
        User user = this.userService.getUserByToken(authorizationToken);

        isSameMerchantForAllItems(orderDTO.getOrderItems(), orderDTO.getMerchantId());

        Address shippingAddress = this.addressService.createAddress(orderDTO.getAddress(), user);

        Payment payment = this.paymentService.createPayment(orderDTO.getPayment());

        List<ItemQuantity> itemQuantities = new ArrayList<>();
        ItemQuantity itemQuantity;
        int itemStock;

        for (ItemQuantityDTO itemQuantityDTO : orderDTO.getOrderItems()) {
            itemQuantity = this.itemQuantityService.createItemQuantity(itemQuantityDTO);
            itemStock = itemQuantity.getItem().getQuantityInStock().getQuantity();
            itemQuantities.add(itemQuantity);
            this.itemService.removeItemStock(authorizationToken, itemQuantityDTO.getItemId(), new ItemUpdateDTO(itemQuantityDTO.getItemId(), itemQuantityDTO.getItemSku(), itemQuantityDTO.getPrice(), itemStock - itemQuantityDTO.getQty()));
        }

        Order order = orderDTO.dtoToEntity(user, payment, itemQuantities);

        order = this.orderRepository.save(order);

        MerchantOrder merchantOrder = this.merchantOrderService.createMerchantOrder(user, order, orderDTO.getMerchantId());

        this.shippingOrderService.createShippingOrder(user, order, merchantOrder, shippingAddress);

        return new OrderDTO(order);
    }

    private void isSameMerchantForAllItems(List<ItemQuantityDTO> itemQuantityDTOS, int merchantId) throws NotFoundException, BadPayloadException {
        ItemDTO itemDTO;
        for (ItemQuantityDTO itemQuantityDTO : itemQuantityDTOS) {
            itemDTO = this.itemService.getItemDTO(itemQuantityDTO.getItemId());
            if (itemDTO.getMerchant().getId() != merchantId) {
                throw new BadPayloadException("Only can order items form same merchant");
            }
        }
    }

    public OrderDTO getUserOrder(String authorizationToken, int id) throws NotFoundException {
        Order order = getUserOrderById(authorizationToken, id);
        return new OrderDTO(order);
    }

    public OrderDTO deleteOrder(int userId, int orderId) throws NotFoundException {
        Order order = this.orderRepository.findById(orderId).filter(o -> o.getUser().getId() == userId).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

        this.shippingOrderService.deleteShippingOrderByOrderId(order.getId());
        this.merchantOrderService.deleteMerchantOrderByOrderId(order.getId());
        this.orderRepository.delete(order);

        return new OrderDTO(order);
    }

    public OrderUpdateDTO fullCancelOrder(String authorizationToken, int id, OrderUpdateDTO orderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (isIdNotEqualToOrderId(id, orderUpdateDTO) || !OrderStatusEnum.CANCELLED.equals(orderUpdateDTO.getOrderStatus())) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        Order order = changeOrderStatus(authorizationToken, orderUpdateDTO.getId(), OrderStatusEnum.CANCELLED);

        this.merchantOrderService.fullCancelMerchantOrderByOrder(authorizationToken, order);
        this.shippingOrderService.fullCancelShippingOrderByOrder(authorizationToken, order);

        addItemStock(authorizationToken, order);

        return new OrderUpdateDTO(order);
    }

    public OrderUpdateDTO fullCancelOrderByOrderId(String authorizationToken, int orderId) throws NotFoundException, WrongFlowException {
        return new OrderUpdateDTO(changeOrderStatus(authorizationToken, orderId, OrderStatusEnum.CANCELLED));
    }

    public OrderUpdateDTO rejectOrder(String authorizationToken, int id, OrderUpdateDTO orderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (isIdNotEqualToOrderId(id, orderUpdateDTO) || !OrderStatusEnum.REJECTED.equals(orderUpdateDTO.getOrderStatus())) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        Order order = changeOrderStatus(authorizationToken, orderUpdateDTO.getId(), OrderStatusEnum.REJECTED);

        this.merchantOrderService.rejectMerchantOrderByOrder(authorizationToken, order);
        this.shippingOrderService.rejectShippingOrderByOrder(authorizationToken, order);

        addItemStock(authorizationToken, order);

        return new OrderUpdateDTO(order);
    }

    public OrderUpdateDTO rejectOrderByOrderId(String authorizationToken, int orderId) throws NotFoundException, WrongFlowException {
        return new OrderUpdateDTO(changeOrderStatus(authorizationToken, orderId, OrderStatusEnum.REJECTED));
    }

    public OrderUpdateDTO approveOrder(String authorizationToken, int id, OrderUpdateDTO orderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, orderUpdateDTO) || !OrderStatusEnum.APPROVED.equals(orderUpdateDTO.getOrderStatus())) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        Order order = changeOrderStatus(authorizationToken, orderUpdateDTO.getId(), OrderStatusEnum.APPROVED);

        return new OrderUpdateDTO(order);
    }

    private Order getUserOrderById(String authorizationToken, int id) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        if (user.getAccount().getRole().equals(RoleEnum.ADMIN) || user.getAccount().getRole().equals(RoleEnum.MERCHANT)) {
            return this.orderRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        } else {
            return this.orderRepository.findById(id).filter(o -> o.getUser() == user).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        }


    }

    private Order changeOrderStatus(String authorizationToken, int id, OrderStatusEnum status) throws NotFoundException, WrongFlowException {
        Order order = getUserOrderById(authorizationToken, id);

        if (isOrderFlowValid(authorizationToken, order, status)) {
            order.setStatus(status);
            order = this.orderRepository.save(order);
            return order;
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
                return order.isPending() && merchantOrderDTOIsPendingOrApproved(merchantOrderDTO) && shippingOrderDTO.isPendingOrApproved();
            }
            case REJECTED -> {
                return order.isPendingOrApproved() && merchantOrderDTOIsPendingOrApprovedOrRejected(merchantOrderDTO) && shippingOrderDTO.isPendingOrApprovedOrRejected();
            }
            case CANCELLED -> {
                return order.isPendingOrApproved() && merchantOrderDTOIsPendingOrApprovedOrCancelled(merchantOrderDTO) && shippingOrderDTO.isPendingOrApprovedOrCancelled();
            }
            case SHIPPED -> {
                return order.isApproved() && merchantOrderDTOIsApprovedOrShipped(merchantOrderDTO) && shippingOrderDTO.isShipped();
            }
            case DELIVERED -> {
                return order.isShipped() && merchantOrderDTOIsShippedOrDelivered(merchantOrderDTO) && shippingOrderDTO.isDelivered();
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isIdNotEqualToOrderId(int id, OrderUpdateDTO orderUpdateDTO) {
        return id != orderUpdateDTO.getId();
    }

    private void addItemStock(String authorizationToken, Order order) throws InvalidQuantityException, BadPayloadException, NotFoundException {
        ItemUpdateDTO itemUpdateDTO;
        for (ItemQuantity itemQuantity : order.getItemQuantities()) {
            itemUpdateDTO = new ItemUpdateDTO(itemQuantity.getItem());
            itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + itemQuantity.getQuantityOrdered().getQuantity());
            this.itemService.addItemStock(authorizationToken, itemQuantity.getItem().getId(), itemUpdateDTO);
        }
    }

    private boolean merchantOrderDTOIsPendingOrApproved(MerchantOrderDTO merchantOrderDTO) {
        return merchantOrderDTO.isPending() || merchantOrderDTO.isApproved();
    }

    private boolean merchantOrderDTOIsPendingOrApprovedOrRejected(MerchantOrderDTO merchantOrderDTO) {
        return merchantOrderDTOIsPendingOrApproved(merchantOrderDTO) || merchantOrderDTO.isRejected();
    }

    private boolean merchantOrderDTOIsPendingOrApprovedOrCancelled(MerchantOrderDTO merchantOrderDTO) {
        return merchantOrderDTOIsPendingOrApproved(merchantOrderDTO) || merchantOrderDTO.isCancelled();
    }

    private boolean merchantOrderDTOIsApprovedOrShipped(MerchantOrderDTO merchantOrderDTO) {
        return merchantOrderDTO.isApproved() || merchantOrderDTO.isShipped();
    }

    private boolean merchantOrderDTOIsShippedOrDelivered(MerchantOrderDTO merchantOrderDTO) {
        return merchantOrderDTO.isShipped() || merchantOrderDTO.isDelivered();
    }
}
