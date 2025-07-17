package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker.OrderPublisher;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import edu.ipp.isep.dei.dimei.retailproject.events.OrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
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
    private final ItemService itemService;
    private final PaymentService paymentService;
    private final ItemQuantityService itemQuantityService;
    private final OrderPublisher orderPublisher;

    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orders = new ArrayList<>();

        this.orderRepository.findAll().forEach(order -> orders.add(new OrderDTO(order)));
        return orders;
    }

    public List<OrderDTO> getUserOrders(UserDTO userDTO) {
        List<OrderDTO> orders = new ArrayList<>();

        this.orderRepository.findByUserId(userDTO.getUserId()).forEach(order -> orders.add(new OrderDTO(order)));

        return orders;
    }

    public OrderDTO createOrder(OrderCreateDTO orderDTO, boolean isEvent) throws NotFoundException, InvalidQuantityException, BadPayloadException {
        isSameMerchantForAllItems(orderDTO.getOrderItems(), orderDTO.getMerchantId());

        Payment payment = this.paymentService.createPayment(orderDTO.getPayment());

        List<ItemQuantity> itemQuantities = new ArrayList<>();
        ItemQuantity itemQuantity;
        int itemStock;

        for (ItemQuantityDTO itemQuantityDTO : orderDTO.getOrderItems()) {
            itemQuantity = this.itemQuantityService.createItemQuantity(itemQuantityDTO);
            itemStock = itemQuantity.getItem().getQuantityInStock().getQuantity();
            itemQuantities.add(itemQuantity);
            this.itemService.removeItemStock(itemQuantityDTO.getItemId(), new ItemUpdateDTO(itemQuantityDTO.getItemId(), itemQuantityDTO.getItemSku(), itemQuantityDTO.getPrice(), itemStock - itemQuantityDTO.getQty(), orderDTO.getUserDTO()));
        }

        Order order = orderDTO.dtoToEntity(payment, itemQuantities);

        order = this.orderRepository.save(order);
        OrderDTO orderDTO1 = new OrderDTO(order);

        if (!isEvent) {
            orderPublisher.publishEvent(new OrderEvent(orderDTO1, EventTypeEnum.CREATE));
        }

        return orderDTO1;
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

    public OrderDTO getUserOrder(UserDTO userDTO, int id) throws NotFoundException {
        Order order = getUserOrderById(userDTO, id, false);
        return new OrderDTO(order);
    }

    public OrderDTO deleteOrder(int id, int orderId, boolean isEvent) throws NotFoundException {

        Order order = this.orderRepository.findById(orderId)
                .filter(o -> o.getUserId() == id || isEvent)
                .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

        this.orderRepository.delete(order);
        OrderDTO orderDTO = new OrderDTO(order);
        orderPublisher.publishEvent(new OrderEvent(orderDTO, EventTypeEnum.DELETE));

        return orderDTO;
    }

    public OrderUpdateDTO fullCancelOrder(int id, OrderUpdateDTO orderUpdateDTO, boolean isEvent) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (isIdNotEqualToOrderId(id, orderUpdateDTO) || !OrderStatusEnum.CANCELLED.equals(orderUpdateDTO.getOrderStatus())) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        Order order = changeOrderStatus(orderUpdateDTO.getUserDTO(), orderUpdateDTO.getId(), OrderStatusEnum.CANCELLED, isEvent);

        addItemStock(orderUpdateDTO.getUserDTO(), order);

        return new OrderUpdateDTO(order, orderUpdateDTO.getUserDTO().getEmail());
    }

    public OrderUpdateDTO fullCancelOrderByOrderId(UserDTO userDTO, int orderId, boolean isEvent) throws NotFoundException, WrongFlowException {
        return new OrderUpdateDTO(changeOrderStatus(userDTO, orderId, OrderStatusEnum.CANCELLED, isEvent), userDTO.getEmail());
    }

    public OrderUpdateDTO rejectOrder(int id, OrderUpdateDTO orderUpdateDTO, boolean isEvent) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (isIdNotEqualToOrderId(id, orderUpdateDTO) || !OrderStatusEnum.REJECTED.equals(orderUpdateDTO.getOrderStatus())) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        Order order = changeOrderStatus(orderUpdateDTO.getUserDTO(), orderUpdateDTO.getId(), OrderStatusEnum.REJECTED, isEvent);

        addItemStock(orderUpdateDTO.getUserDTO(), order);

        return new OrderUpdateDTO(order, orderUpdateDTO.getUserDTO().getEmail());
    }

    public OrderUpdateDTO rejectOrderByOrderId(UserDTO userDTO, int orderId, boolean isEvent) throws NotFoundException, WrongFlowException {
        return new OrderUpdateDTO(changeOrderStatus(userDTO, orderId, OrderStatusEnum.REJECTED, isEvent), userDTO.getEmail());
    }

    public OrderUpdateDTO approveOrder(int id, OrderUpdateDTO orderUpdateDTO, boolean isEvent) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, orderUpdateDTO) || !OrderStatusEnum.APPROVED.equals(orderUpdateDTO.getOrderStatus())) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        Order order = changeOrderStatus(orderUpdateDTO.getUserDTO(), orderUpdateDTO.getId(), OrderStatusEnum.APPROVED, isEvent);

        return new OrderUpdateDTO(order, orderUpdateDTO.getUserDTO().getEmail());
    }

    private Order getUserOrderById(UserDTO userDTO, int id, boolean isEvent) throws NotFoundException {
        if (isEvent) {
            return this.orderRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        } else if (userDTO.getRole().equals(RoleEnum.ADMIN) || userDTO.getRole().equals(RoleEnum.MERCHANT)) {
            return this.orderRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        } else {
            return this.orderRepository.findById(id).filter(o -> o.getUserId() == userDTO.getUserId()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        }


    }

    private Order changeOrderStatus(UserDTO userDTO, int id, OrderStatusEnum status, boolean isEvent) throws NotFoundException, WrongFlowException {
        Order order = getUserOrderById(userDTO, id, isEvent);

        if (isOrderFlowValid(order, status)) {
            order.setStatus(status);
            this.orderRepository.save(order);

            if (!isEvent) {
                OrderDTO orderDTO = new OrderDTO(order);
                OrderEvent orderEvent = new OrderEvent(orderDTO, EventTypeEnum.UPDATE);
                this.orderPublisher.publishEvent(orderEvent);
            }
            return order;
        } else {
            throw new WrongFlowException("It is not possible to change Order status");
        }
    }

    public void shipOrder(UserDTO userDTO, int id, boolean isEvent) throws NotFoundException, WrongFlowException {
        changeOrderStatus(userDTO, id, OrderStatusEnum.SHIPPED, isEvent);
    }

    public void deliverOrder(UserDTO userDTO, int id, boolean isEvent) throws NotFoundException, WrongFlowException {
        changeOrderStatus(userDTO, id, OrderStatusEnum.DELIVERED, isEvent);
    }

    private boolean isOrderFlowValid(Order order, OrderStatusEnum newStatus) {

        switch (newStatus) {
            case PENDING -> {
                return true;
            }
            case APPROVED -> {
                return order.isPending();
            }
            case REJECTED, CANCELLED -> {
                return order.isPendingOrApproved();
            }
            case SHIPPED -> {
                return order.isApproved();
            }
            case DELIVERED -> {
                return order.isShipped();
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isIdNotEqualToOrderId(int id, OrderUpdateDTO orderUpdateDTO) {
        return id != orderUpdateDTO.getId();
    }

    private void addItemStock(UserDTO userDTO, Order order) throws InvalidQuantityException, BadPayloadException, NotFoundException {
        ItemUpdateDTO itemUpdateDTO;
        for (ItemQuantity itemQuantity : order.getItemQuantities()) {
            itemUpdateDTO = new ItemUpdateDTO(itemQuantity.getItem());
            itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + itemQuantity.getQuantityOrdered().getQuantity());
            itemUpdateDTO.setUserDTO(userDTO);
            this.itemService.addItemStock(itemQuantity.getItem().getId(), itemUpdateDTO);
        }
    }
}
