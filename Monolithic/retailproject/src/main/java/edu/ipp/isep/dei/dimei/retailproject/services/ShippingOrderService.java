package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ShippingOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ShippingOrderService {

    private final ShippingOrderRepository shippingOrderRepository;
    private final UserService userService;
    private final MerchantOrderService merchantOrderService;
    private final OrderService orderService;

    public List<ShippingOrderDTO> getAllShippingOrders() {
        List<ShippingOrderDTO> shippingOrders = new ArrayList<>();

        this.shippingOrderRepository.findAll()
                .forEach(shippingOrder -> shippingOrders.add(new ShippingOrderDTO(shippingOrder)));

        return shippingOrders;
    }

    public List<ShippingOrderDTO> getUserShippingOrders(String authorizationToken) {
        User user = this.userService.getUserByToken(authorizationToken);

        List<ShippingOrderDTO> shippingOrders = new ArrayList<>();

        this.shippingOrderRepository.findByUser(user).forEach(shippingOrder -> shippingOrders.add(new ShippingOrderDTO(shippingOrder)));
        return shippingOrders;
    }

    public ShippingOrderDTO getUserShippingOrder(String authorizationToken, int id) throws NotFoundException {
        ShippingOrder shippingOrder = getUserShippingOrderById(authorizationToken, id);
        return new ShippingOrderDTO(shippingOrder);
    }

    public void createShippingOrder(User user, Order order, MerchantOrder merchantOrder, Address address) {
        ShippingOrder shippingOrder = new ShippingOrder(user, order, merchantOrder, address);

        this.shippingOrderRepository.save(shippingOrder);
    }

    public ShippingOrderUpdateDTO fullCancelShippingOrder(String authorizationToken, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.CANCELLED);

        OrderUpdateDTO orderUpdateDTO = this.orderService.rejectOrderByOrderId(authorizationToken, shippingOrder.getOrder().getId());
        shippingOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
        this.merchantOrderService.fullCancelOrderByShippingOrder(authorizationToken, shippingOrder);

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO fullCancelOrderByOrder(String authorizationToken, Order order) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = getUserShippingOrderByOrder(authorizationToken, order);

        shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrder.getId(), ShippingOrderStatusEnum.CANCELLED);

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO fullCancelOrderByMerchantOrder(String authorizationToken, MerchantOrder merchantOrder) throws NotFoundException, WrongFlowException {
        return fullCancelOrderByOrder(authorizationToken, merchantOrder.getOrder());
    }

    public ShippingOrderUpdateDTO rejectShippingOrder(String authorizationToken, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.REJECTED);

        OrderUpdateDTO orderUpdateDTO = this.orderService.rejectOrderByOrderId(authorizationToken, shippingOrder.getOrder().getId());
        shippingOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
        this.merchantOrderService.rejectMerchantOrderByShippingOrder(authorizationToken, shippingOrder);

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO rejectShippingOrderByOrder(String authorizationToken, Order order) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = getUserShippingOrderByOrder(authorizationToken, order);

        if (!ShippingOrderStatusEnum.REJECTED.equals(shippingOrder.getStatus())) {
            shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrder.getId(), ShippingOrderStatusEnum.REJECTED);
        }

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public void rejectShippingOrderByMerchantOrder(String authorizationToken, MerchantOrder merchantOrder) throws NotFoundException, WrongFlowException {
        rejectShippingOrderByOrder(authorizationToken, merchantOrder.getOrder());
    }

    public ShippingOrderUpdateDTO approveShippingOrder(String authorizationToken, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.APPROVED);

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO shippedShippingOrder(String authorizationToken, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.SHIPPED);

        this.merchantOrderService.shipMerchantOrder(authorizationToken, shippingOrder.getMerchantOrder().getId());
        this.orderService.shipOrder(authorizationToken, shippingOrder.getOrder().getId());

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO deliveredShippingOrder(String authorizationToken, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.DELIVERED);

        this.merchantOrderService.deliverMerchantOrder(authorizationToken, shippingOrder.getMerchantOrder().getId());
        this.orderService.deliverOrder(authorizationToken, shippingOrder.getOrder().getId());

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    private ShippingOrder getUserShippingOrderById(String authorizationToken, int id) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        return this.shippingOrderRepository.findById(id).filter(o -> o.getUser() == user)
                .orElseThrow(() -> new NotFoundException("Shipping Order not found"));
    }

    private ShippingOrder getUserShippingOrderByOrder(String authorizationToken, Order order) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        return this.shippingOrderRepository.findByOrder(order).filter(o -> o.getUser() == user)
                .orElseThrow(() -> new NotFoundException("Shipping Order not found"));
    }

    private ShippingOrder changeShippingOrderStatus(String authorizationToken, int id, ShippingOrderStatusEnum status) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = getUserShippingOrderById(authorizationToken, id);

        if (isShippingOrderFlowValid(authorizationToken, shippingOrder, status)) {
            shippingOrder.setStatus(status);

            return this.shippingOrderRepository.save(shippingOrder);
        } else {
            throw new WrongFlowException("It is not possible to change Shipping Order status");
        }
    }

    private boolean isShippingOrderFlowValid(String authorizationToken, ShippingOrder shippingOrder, ShippingOrderStatusEnum newStatus) throws NotFoundException {
        OrderDTO orderDTO = this.orderService.getUserOrder(authorizationToken, shippingOrder.getOrder().getId());
        MerchantOrderDTO merchantOrderDTO = this.merchantOrderService.getUserMerchantOrder(authorizationToken, shippingOrder.getOrder().getId());

        switch (newStatus) {
            case PENDING -> {
                return true;
            }
            case APPROVED -> {
                return shippingOrder.getStatus().compareTo(ShippingOrderStatusEnum.PENDING) == 0 &&
                        (orderDTO.getOrderStatus().compareTo(OrderStatusEnum.PENDING) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.APPROVED) == 0) &&
                        (merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.PENDING) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0);
            }
            case REJECTED -> {
                return (shippingOrder.getStatus().compareTo(ShippingOrderStatusEnum.PENDING) == 0 ||
                        shippingOrder.getStatus().compareTo(ShippingOrderStatusEnum.APPROVED) == 0) &&
                        (orderDTO.getOrderStatus().compareTo(OrderStatusEnum.PENDING) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.APPROVED) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.REJECTED) == 0) &&
                        (merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.PENDING) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.REJECTED) == 0);
            }
            case CANCELLED -> {
                return (shippingOrder.getStatus().compareTo(ShippingOrderStatusEnum.PENDING) == 0 ||
                        shippingOrder.getStatus().compareTo(ShippingOrderStatusEnum.APPROVED) == 0) &&
                        (orderDTO.getOrderStatus().compareTo(OrderStatusEnum.PENDING) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.APPROVED) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.CANCELLED) == 0) &&
                        (merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.PENDING) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0 ||
                                merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.CANCELLED) == 0);
            }
            case SHIPPED -> {
                return shippingOrder.getStatus().compareTo(ShippingOrderStatusEnum.APPROVED) == 0 &&
                        orderDTO.getOrderStatus().compareTo(OrderStatusEnum.APPROVED) == 0 &&
                        merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0;
            }
            case DELIVERED -> {
                return shippingOrder.getStatus().compareTo(ShippingOrderStatusEnum.SHIPPED) == 0 &&
                        orderDTO.getOrderStatus().compareTo(OrderStatusEnum.SHIPPED) == 0 &&
                        merchantOrderDTO.getMerchantOrderStatus().compareTo(MerchantOrderStatusEnum.SHIPPED) == 0;
            }
        }
        return false;
    }
}