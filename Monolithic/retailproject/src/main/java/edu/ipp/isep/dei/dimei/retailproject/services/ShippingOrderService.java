package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ShippingOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ShippingOrderService {

    private final ShippingOrderRepository shippingOrderRepository;
    private final UserService userService;
    private final MerchantOrderService merchantOrderService;
    private final OrderService orderService;
    private final ItemService itemService;
    private static final String BADPAYLOADEXCEPTIONMESSAGE = "Wrong shipping order payload.";
    private static final String NOTFOUNDEXCEPTIONMESSAGE = "Shipping Order not found.";

    public ShippingOrderService(ShippingOrderRepository shippingOrderRepository, UserService userService, @Lazy MerchantOrderService merchantOrderService, @Lazy OrderService orderService, ItemService itemService) {
        this.shippingOrderRepository = shippingOrderRepository;
        this.userService = userService;
        this.merchantOrderService = merchantOrderService;
        this.orderService = orderService;
        this.itemService = itemService;
    }

    public List<ShippingOrderDTO> getAllShippingOrders() {
        List<ShippingOrderDTO> shippingOrders = new ArrayList<>();

        this.shippingOrderRepository.findAll()
                .forEach(shippingOrder -> shippingOrders.add(new ShippingOrderDTO(shippingOrder)));

        return shippingOrders;
    }

    public List<ShippingOrderDTO> getUserShippingOrders(String authorizationToken) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        List<ShippingOrderDTO> shippingOrders = new ArrayList<>();

        this.shippingOrderRepository.findByUser(user).forEach(shippingOrder -> shippingOrders.add(new ShippingOrderDTO(shippingOrder)));
        return shippingOrders;
    }

    public ShippingOrderDTO getUserShippingOrder(String authorizationToken, int id) throws NotFoundException {
        ShippingOrder shippingOrder = getUserShippingOrderById(authorizationToken, id);
        return new ShippingOrderDTO(shippingOrder);
    }

    public void createShippingOrder(User user, Order order, MerchantOrder merchantOrder, Address shippingAddress) {
        ShippingOrder shippingOrder = new ShippingOrder(user, order, merchantOrder, shippingAddress);

        this.shippingOrderRepository.save(shippingOrder);
    }

    public ShippingOrderUpdateDTO fullCancelShippingOrder(String authorizationToken, int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (!isIdEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.CANCELLED);

        OrderUpdateDTO orderUpdateDTO = this.orderService.fullCancelOrderByOrderId(authorizationToken, shippingOrder.getOrder().getId());
        shippingOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
        this.merchantOrderService.fullCancelMerchantOrderByShippingOrder(authorizationToken, shippingOrder);

        addItemStock(authorizationToken, shippingOrder);

        shippingOrder = this.shippingOrderRepository.findById(shippingOrder.getId()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO fullCancelShippingOrderByOrder(String authorizationToken, Order order) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = getUserShippingOrderByOrder(authorizationToken, order);

        shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrder.getId(), ShippingOrderStatusEnum.CANCELLED);

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO fullCancelShippingOrderByMerchantOrder(String authorizationToken, MerchantOrder merchantOrder) throws NotFoundException, WrongFlowException {
        return fullCancelShippingOrderByOrder(authorizationToken, merchantOrder.getOrder());
    }

    public ShippingOrderUpdateDTO rejectShippingOrder(String authorizationToken, int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (!isIdEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.REJECTED);

        OrderUpdateDTO orderUpdateDTO = this.orderService.rejectOrderByOrderId(authorizationToken, shippingOrder.getOrder().getId());
        shippingOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
        this.merchantOrderService.rejectMerchantOrderByShippingOrder(authorizationToken, shippingOrder);

        addItemStock(authorizationToken, shippingOrder);

        shippingOrder = this.shippingOrderRepository.findById(shippingOrder.getId()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO rejectShippingOrderByOrder(String authorizationToken, Order order) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = getUserShippingOrderByOrder(authorizationToken, order);

        if (!ShippingOrderStatusEnum.REJECTED.equals(shippingOrder.getStatus())) {
            shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrder.getId(), ShippingOrderStatusEnum.REJECTED);
        }

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO rejectShippingOrderByMerchantOrder(String authorizationToken, MerchantOrder merchantOrder) throws NotFoundException, WrongFlowException {
        return rejectShippingOrderByOrder(authorizationToken, merchantOrder.getOrder());
    }

    public ShippingOrderUpdateDTO approveShippingOrder(String authorizationToken, int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (!isIdEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.APPROVED);

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO shippedShippingOrder(String authorizationToken, int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (!isIdEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.SHIPPED);

        this.merchantOrderService.shipMerchantOrder(authorizationToken, shippingOrder.getMerchantOrder().getId());
        this.orderService.shipOrder(authorizationToken, shippingOrder.getOrder().getId());

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO deliveredShippingOrder(String authorizationToken, int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (!isIdEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

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

        return this.shippingOrderRepository.findByOrder(order)
                .filter(shippingOrder -> shippingOrder.getUser() == user)
                .orElseThrow(() -> new NotFoundException("Shipping Order not found"));
    }

    private ShippingOrder changeShippingOrderStatus(String authorizationToken, int id, ShippingOrderStatusEnum status) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = getUserShippingOrderById(authorizationToken, id);

        if (isShippingOrderFlowValid(authorizationToken, shippingOrder, status)) {
            shippingOrder.setStatus(status);

            shippingOrder = this.shippingOrderRepository.save(shippingOrder);
            return shippingOrder;
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

    private boolean isIdEqualToOrderId(int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        return id == shippingOrderUpdateDTO.getId();
    }

    private void addItemStock(String authorizationToken, ShippingOrder shippingOrder) throws InvalidQuantityException, BadPayloadException, NotFoundException {
        for (ItemQuantity itemQuantity : shippingOrder.getOrder().getItemQuantities()) {
            this.itemService.addItemStock(authorizationToken, itemQuantity.getItem().getId(), new ItemUpdateDTO(itemQuantity.getItem()));
        }
    }
}
