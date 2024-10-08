package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
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

    protected static final String BADPAYLOADEXCEPTIONMESSAGE = "Wrong shipping order payload.";
    protected static final String NOTFOUNDEXCEPTIONMESSAGE = "Shipping Order not found.";
    private final ShippingOrderRepository shippingOrderRepository;
    private final UserService userService;
    private final MerchantOrderService merchantOrderService;
    private final OrderService orderService;
    private final ItemService itemService;

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
        if (isIdNotEqualToOrderId(id, shippingOrderUpdateDTO)) {
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
        if (isIdNotEqualToOrderId(id, shippingOrderUpdateDTO)) {
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

        if (!shippingOrder.isRejected()) {
            shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrder.getId(), ShippingOrderStatusEnum.REJECTED);
        }

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO rejectShippingOrderByMerchantOrder(String authorizationToken, MerchantOrder merchantOrder) throws NotFoundException, WrongFlowException {
        return rejectShippingOrderByOrder(authorizationToken, merchantOrder.getOrder());
    }

    public ShippingOrderUpdateDTO approveShippingOrder(String authorizationToken, int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.APPROVED);

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO shippedShippingOrder(String authorizationToken, int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.SHIPPED);

        this.merchantOrderService.shipMerchantOrder(authorizationToken, shippingOrder.getMerchantOrder().getId());
        this.orderService.shipOrder(authorizationToken, shippingOrder.getOrder().getId());

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO deliveredShippingOrder(String authorizationToken, int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(authorizationToken, shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.DELIVERED);

        this.merchantOrderService.deliverMerchantOrder(authorizationToken, shippingOrder.getMerchantOrder().getId());
        this.orderService.deliverOrder(authorizationToken, shippingOrder.getOrder().getId());

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    private ShippingOrder getUserShippingOrderById(String authorizationToken, int id) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        switch (user.getAccount().getRole()) {
            case ADMIN -> {
                return this.shippingOrderRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            case MERCHANT -> {
                return this.shippingOrderRepository.findById(id)
                        .filter(o -> o.getMerchantOrder().getMerchant().getEmail().compareTo(user.getAccount().getEmail()) == 0)
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            default -> {
                return this.shippingOrderRepository.findById(id)
                        .filter(o -> o.getUser() == user && o.getUser().getAccount().getRole().equals(RoleEnum.USER))
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
        }
    }

    private ShippingOrder getUserShippingOrderByOrder(String authorizationToken, Order order) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        if (user.getAccount().getRole().equals(RoleEnum.ADMIN)) {
            return this.shippingOrderRepository.findByOrder(order)
                    .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        } else {
            return this.shippingOrderRepository.findByOrder(order)
                    .filter(shippingOrder -> shippingOrder.getUser() == user && shippingOrder.getUser().getAccount().getRole().equals(RoleEnum.USER))
                    .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        }
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
            case APPROVED -> {
                return shippingOrder.isPending() && (orderDTO.isPending() || orderDTO.isApproved()) && (merchantOrderDTO.isPending() || merchantOrderDTO.isApproved());
            }
            case REJECTED -> {
                return shippingOrder.isPendingOrApproved() && (orderDTO.isPending() || orderDTO.isApproved() || orderDTO.isRejected()) && (merchantOrderDTO.isPending() || merchantOrderDTO.isApproved() || merchantOrderDTO.isRejected());
            }
            case CANCELLED -> {
                return shippingOrder.isPendingOrApproved() && (orderDTO.isPending() || orderDTO.isApproved() || orderDTO.isCancelled()) && (merchantOrderDTO.isPending() || merchantOrderDTO.isApproved() || merchantOrderDTO.isCancelled());
            }
            case SHIPPED -> {
                return shippingOrder.isApproved() && orderDTO.isApproved() && merchantOrderDTO.isApproved();
            }
            case DELIVERED -> {
                return shippingOrder.isShipped() && orderDTO.isShipped() && merchantOrderDTO.isShipped();
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isIdNotEqualToOrderId(int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        return id != shippingOrderUpdateDTO.getId();
    }

    private void addItemStock(String authorizationToken, ShippingOrder shippingOrder) throws InvalidQuantityException, BadPayloadException, NotFoundException {
        ItemUpdateDTO itemUpdateDTO;

        for (ItemQuantity itemQuantity : shippingOrder.getOrder().getItemQuantities()) {
            itemUpdateDTO = new ItemUpdateDTO(itemQuantity.getItem());
            itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + itemQuantity.getQuantityOrdered().getQuantity());
            this.itemService.addItemStock(authorizationToken, itemQuantity.getItem().getId(), itemUpdateDTO);
        }
    }

    protected void deleteShippingOrderByOrderId(int orderId) {
        this.shippingOrderRepository.deleteByOrderId(orderId);
    }
}
