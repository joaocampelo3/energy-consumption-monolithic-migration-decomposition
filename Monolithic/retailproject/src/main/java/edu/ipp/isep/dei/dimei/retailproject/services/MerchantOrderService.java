package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantOrderRepository;
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class MerchantOrderService {

    private final MerchantOrderRepository merchantOrderRepository;
    private final UserService userService;
    private final MerchantRepository merchantRepository;
    private final OrderService orderService;
    private final ShippingOrderService shippingOrderService;

    public List<MerchantOrderDTO> getAllMerchantOrders() {
        List<MerchantOrderDTO> merchantOrders = new ArrayList<>();

        this.merchantOrderRepository.findAll()
                .forEach(merchantOrder -> merchantOrders.add(new MerchantOrderDTO(merchantOrder)));

        return merchantOrders;
    }

    public List<MerchantOrderDTO> getUserMerchantOrders(String authorizationToken) {
        User user = this.userService.getUserByToken(authorizationToken);

        List<MerchantOrderDTO> merchantOrders = new ArrayList<>();

        this.merchantOrderRepository.findByUser(user).forEach(merchantOrder -> merchantOrders.add(new MerchantOrderDTO(merchantOrder)));
        return merchantOrders;
    }

    public MerchantOrderDTO getUserMerchantOrder(String authorizationToken, int id) throws NotFoundException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(authorizationToken, id);
        return new MerchantOrderDTO(merchantOrder);
    }

    public MerchantOrder createMerchantOrder(User user, Order order, int merchantId) throws NotFoundException {
        Merchant merchant = this.merchantRepository.findById(merchantId)
                .orElseThrow(() -> new NotFoundException("Merchant not found."));

        MerchantOrder merchantOrder = new MerchantOrder(user, order, merchant);

        return this.merchantOrderRepository.save(merchantOrder);
    }

    public MerchantOrderUpdateDTO fullCancelMerchantOrder(String authorizationToken, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(authorizationToken, merchantOrderUpdateDTO.getId());

        if (!MerchantOrderStatusEnum.CANCELLED.equals(merchantOrder.getStatus())) {
            merchantOrder = changeMerchantOrderStatus(authorizationToken, merchantOrder.getId(), MerchantOrderStatusEnum.CANCELLED);

            OrderUpdateDTO orderUpdateDTO = this.orderService.fullCancelOrderByOrderId(authorizationToken, merchantOrder.getOrder().getId());
            merchantOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
            this.shippingOrderService.fullCancelOrderByMerchantOrder(authorizationToken, merchantOrder);
        }

        return new MerchantOrderUpdateDTO(merchantOrder);
    }

    public MerchantOrderUpdateDTO fullCancelOrderByOrder(String authorizationToken, Order order) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderByOrder(authorizationToken, order);

        if (!MerchantOrderStatusEnum.CANCELLED.equals(merchantOrder.getStatus())) {
            merchantOrder = changeMerchantOrderStatus(authorizationToken, merchantOrder.getId(), MerchantOrderStatusEnum.CANCELLED);
        }

        return new MerchantOrderUpdateDTO(merchantOrder);
    }

    public MerchantOrderUpdateDTO fullCancelOrderByShippingOrder(String authorizationToken, ShippingOrder shippingOrder) throws NotFoundException, WrongFlowException {
        return fullCancelOrderByOrder(authorizationToken, shippingOrder.getOrder());
    }

    public MerchantOrderUpdateDTO rejectMerchantOrder(String authorizationToken, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(authorizationToken, merchantOrderUpdateDTO.getId());

        if (!MerchantOrderStatusEnum.REJECTED.equals(merchantOrder.getStatus())) {
            merchantOrder = changeMerchantOrderStatus(authorizationToken, merchantOrder.getId(), MerchantOrderStatusEnum.REJECTED);

            OrderUpdateDTO orderUpdateDTO = this.orderService.fullCancelOrderByOrderId(authorizationToken, merchantOrder.getOrder().getId());
            merchantOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
            this.shippingOrderService.rejectShippingOrderByMerchantOrder(authorizationToken, merchantOrder);
        }

        return new MerchantOrderUpdateDTO(merchantOrder);
    }

    public MerchantOrderUpdateDTO rejectMerchantOrderByOrder(String authorizationToken, Order order) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderByOrder(authorizationToken, order);

        if (!MerchantOrderStatusEnum.REJECTED.equals(merchantOrder.getStatus())) {
            merchantOrder = changeMerchantOrderStatus(authorizationToken, merchantOrder.getId(), MerchantOrderStatusEnum.REJECTED);
        }

        return new MerchantOrderUpdateDTO(merchantOrder);
    }

    public MerchantOrderUpdateDTO rejectMerchantOrderByShippingOrder(String authorizationToken, ShippingOrder shippingOrder) throws NotFoundException, WrongFlowException {
        return rejectMerchantOrderByOrder(authorizationToken, shippingOrder.getOrder());
    }

    public MerchantOrderUpdateDTO approveMerchantOrder(String authorizationToken, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(authorizationToken, merchantOrderUpdateDTO.getId());

        if (!MerchantOrderStatusEnum.APPROVED.equals(merchantOrder.getStatus()))
            merchantOrder = changeMerchantOrderStatus(authorizationToken, merchantOrder.getId(), MerchantOrderStatusEnum.APPROVED);

        return new MerchantOrderUpdateDTO(merchantOrder);
    }

    public MerchantOrder shipMerchantOrder(String authorizationToken, int id) throws NotFoundException, WrongFlowException {
        return changeMerchantOrderStatus(authorizationToken, id, MerchantOrderStatusEnum.SHIPPED);
    }

    public MerchantOrder deliverMerchantOrder(String authorizationToken, int id) throws NotFoundException, WrongFlowException {
        return changeMerchantOrderStatus(authorizationToken, id, MerchantOrderStatusEnum.DELIVERED);
    }

    private MerchantOrder getUserMerchantOrderById(String authorizationToken, int id) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        return this.merchantOrderRepository.findById(id).filter(o -> o.getUser() == user)
                .orElseThrow(() -> new NotFoundException("Merchant Order not found"));
    }

    private MerchantOrder getUserMerchantOrderByOrder(String authorizationToken, Order order) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        return this.merchantOrderRepository.findByOrder(order).filter(o -> o.getUser() == user)
                .orElseThrow(() -> new NotFoundException("Merchant Order not found"));
    }

    private MerchantOrder changeMerchantOrderStatus(String authorizationToken, int id, MerchantOrderStatusEnum status) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(authorizationToken, id);

        if (isMerchantOrderFlowValid(authorizationToken, merchantOrder, status)) {
            merchantOrder.setStatus(status);

            return this.merchantOrderRepository.save(merchantOrder);
        } else {
            throw new WrongFlowException("It is not possible to change Merchant Order status");
        }
    }

    private boolean isMerchantOrderFlowValid(String authorizationToken, MerchantOrder merchantOrder, MerchantOrderStatusEnum newStatus) throws NotFoundException {
        OrderDTO orderDTO = this.orderService.getUserOrder(authorizationToken, merchantOrder.getOrder().getId());
        ShippingOrderDTO shippingOrderDTO = this.shippingOrderService.getUserShippingOrder(authorizationToken, merchantOrder.getOrder().getId());

        switch (newStatus) {
            case PENDING -> {
                return true;
            }
            case APPROVED -> {
                return merchantOrder.getStatus().compareTo(MerchantOrderStatusEnum.PENDING) == 0 &&
                        (orderDTO.getOrderStatus().compareTo(OrderStatusEnum.PENDING) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.APPROVED) == 0) &&
                        (shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.PENDING) == 0 ||
                                shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.APPROVED) == 0);
            }
            case REJECTED -> {
                return (merchantOrder.getStatus().compareTo(MerchantOrderStatusEnum.PENDING) == 0 ||
                        merchantOrder.getStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0) &&
                        (orderDTO.getOrderStatus().compareTo(OrderStatusEnum.PENDING) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.APPROVED) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.REJECTED) == 0) &&
                        (shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.PENDING) == 0 ||
                                shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.APPROVED) == 0 ||
                                shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.REJECTED) == 0);
            }
            case CANCELLED -> {
                return (merchantOrder.getStatus().compareTo(MerchantOrderStatusEnum.PENDING) == 0 ||
                        merchantOrder.getStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0) &&
                        (orderDTO.getOrderStatus().compareTo(OrderStatusEnum.PENDING) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.APPROVED) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.CANCELLED) == 0) &&
                        (shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.PENDING) == 0 ||
                                shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.APPROVED) == 0 ||
                                shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.CANCELLED) == 0);
            }
            case SHIPPED -> {
                return merchantOrder.getStatus().compareTo(MerchantOrderStatusEnum.APPROVED) == 0 &&
                        (orderDTO.getOrderStatus().compareTo(OrderStatusEnum.APPROVED) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.SHIPPED) == 0) &&
                        shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.SHIPPED) == 0;
            }
            case DELIVERED -> {
                return merchantOrder.getStatus().compareTo(MerchantOrderStatusEnum.SHIPPED) == 0 &&
                        (orderDTO.getOrderStatus().compareTo(OrderStatusEnum.SHIPPED) == 0 ||
                                orderDTO.getOrderStatus().compareTo(OrderStatusEnum.DELIVERED) == 0) &&
                        shippingOrderDTO.getShippingOrderStatus().compareTo(ShippingOrderStatusEnum.DELIVERED) == 0;
            }
        }
        return false;
    }
}
