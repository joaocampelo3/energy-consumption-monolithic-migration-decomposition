package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
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
    private final MerchantOrderService merchantOrderService;
    private final ShippingOrderService shippingOrderService;
    private final ItemService itemService;
    private final PaymentService paymentService;
    private final ItemQuantityService itemQuantityService;

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

    public OrderDTO createOrder(OrderCreateDTO orderDTO) throws NotFoundException, InvalidQuantityException, BadPayloadException {
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

        MerchantOrder merchantOrder = this.merchantOrderService.createMerchantOrder(orderDTO.getUserDTO(), order, orderDTO.getMerchantId());

        this.shippingOrderService.createShippingOrder(orderDTO.getUserDTO(), order, merchantOrder, orderDTO.getAddress().getId());

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

    public OrderDTO getUserOrder(UserDTO userDTO, int id) throws NotFoundException {
        Order order = getUserOrderById(userDTO, id);
        return new OrderDTO(order);
    }

    public OrderDTO deleteOrder(int id, UserDTO userDTO, int orderId) throws NotFoundException {

        Order order = this.orderRepository.findById(orderId)
                .filter(o -> o.getUserId() == userDTO.getUserId() && userDTO.getUserId() == id)
                .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

        this.shippingOrderService.deleteShippingOrderByOrderId(order.getId());
        this.merchantOrderService.deleteMerchantOrderByOrderId(order.getId());
        this.orderRepository.delete(order);

        return new OrderDTO(order);
    }

    public OrderUpdateDTO fullCancelOrder(int id, OrderUpdateDTO orderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (isIdNotEqualToOrderId(id, orderUpdateDTO) || OrderStatusEnum.CANCELLED.compareTo(orderUpdateDTO.getOrderStatus()) == 0) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        Order order = changeOrderStatus(orderUpdateDTO.getUserDTO(), orderUpdateDTO.getId(), OrderStatusEnum.CANCELLED);

        this.merchantOrderService.fullCancelMerchantOrderByOrder(orderUpdateDTO.getUserDTO(), order);
        this.shippingOrderService.fullCancelShippingOrderByOrder(orderUpdateDTO.getUserDTO(), order);

        addItemStock(orderUpdateDTO.getUserDTO(), order);

        return new OrderUpdateDTO(order, orderUpdateDTO.getUserDTO().getEmail());
    }

    public OrderUpdateDTO fullCancelOrderByOrderId(UserDTO userDTO, int orderId) throws NotFoundException, WrongFlowException {
        return new OrderUpdateDTO(changeOrderStatus(userDTO, orderId, OrderStatusEnum.CANCELLED), userDTO.getEmail());
    }

    public OrderUpdateDTO rejectOrder(int id, OrderUpdateDTO orderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (isIdNotEqualToOrderId(id, orderUpdateDTO) || OrderStatusEnum.REJECTED.compareTo(orderUpdateDTO.getOrderStatus()) == 0) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        Order order = changeOrderStatus(orderUpdateDTO.getUserDTO(), orderUpdateDTO.getId(), OrderStatusEnum.REJECTED);

        this.merchantOrderService.rejectMerchantOrderByOrder(orderUpdateDTO.getUserDTO(), order);
        this.shippingOrderService.rejectShippingOrderByOrder(orderUpdateDTO.getUserDTO(), order);

        addItemStock(orderUpdateDTO.getUserDTO(), order);

        return new OrderUpdateDTO(order, orderUpdateDTO.getUserDTO().getEmail());
    }

    public OrderUpdateDTO rejectOrderByOrderId(UserDTO userDTO, int orderId) throws NotFoundException, WrongFlowException {
        return new OrderUpdateDTO(changeOrderStatus(userDTO, orderId, OrderStatusEnum.REJECTED), userDTO.getEmail());
    }

    public OrderUpdateDTO approveOrder(int id, OrderUpdateDTO orderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, orderUpdateDTO) || OrderStatusEnum.APPROVED.compareTo(orderUpdateDTO.getOrderStatus()) == 0) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        Order order = changeOrderStatus(orderUpdateDTO.getUserDTO(), orderUpdateDTO.getId(), OrderStatusEnum.APPROVED);

        return new OrderUpdateDTO(order, orderUpdateDTO.getUserDTO().getEmail());
    }

    private Order getUserOrderById(UserDTO userDTO, int id) throws NotFoundException {
        if (userDTO.getRole().equals(RoleEnum.ADMIN) || userDTO.getRole().equals(RoleEnum.MERCHANT)) {
            return this.orderRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        } else {
            return this.orderRepository.findById(id).filter(o -> o.getUserId() == userDTO.getUserId()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        }


    }

    private Order changeOrderStatus(UserDTO userDTO, int id, OrderStatusEnum status) throws NotFoundException, WrongFlowException {
        Order order = getUserOrderById(userDTO, id);

        if (isOrderFlowValid(userDTO, order, status)) {
            order.setStatus(status);
            order = this.orderRepository.save(order);
            return order;
        } else {
            throw new WrongFlowException("It is not possible to change Order status");
        }
    }

    public void shipOrder(UserDTO userDTO, int id) throws NotFoundException, WrongFlowException {
        changeOrderStatus(userDTO, id, OrderStatusEnum.SHIPPED);
    }

    public void deliverOrder(UserDTO userDTO, int id) throws NotFoundException, WrongFlowException {
        changeOrderStatus(userDTO, id, OrderStatusEnum.DELIVERED);
    }

    private boolean isOrderFlowValid(UserDTO userDTO, Order order, OrderStatusEnum newStatus) throws NotFoundException {
        MerchantOrderDTO merchantOrderDTO = this.merchantOrderService.getUserMerchantOrder(userDTO, order.getId());
        ShippingOrderDTO shippingOrderDTO = this.shippingOrderService.getUserShippingOrder(userDTO, order.getId());

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

    private void addItemStock(UserDTO userDTO, Order order) throws InvalidQuantityException, BadPayloadException, NotFoundException {
        ItemUpdateDTO itemUpdateDTO;
        for (ItemQuantity itemQuantity : order.getItemQuantities()) {
            itemUpdateDTO = new ItemUpdateDTO(itemQuantity.getItem());
            itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + itemQuantity.getQuantityOrdered().getQuantity());
            itemUpdateDTO.setUserDTO(userDTO);
            this.itemService.addItemStock(itemQuantity.getItem().getId(), itemUpdateDTO);
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
