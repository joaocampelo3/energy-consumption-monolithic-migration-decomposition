package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class MerchantOrderService {

    private final MerchantOrderRepository merchantOrderRepository;
    private final UserService userService;
    private final MerchantService merchantService;
    private final OrderService orderService;
    private final ShippingOrderService shippingOrderService;
    private final ItemService itemService;
    private static final String BADPAYLOADEXCEPTIONMESSAGE = "Wrong merchant order payload.";
    private static final String NOTFOUNDEXCEPTIONMESSAGE = "Merchant Order not found.";

    public MerchantOrderService(MerchantOrderRepository merchantOrderRepository, UserService userService, MerchantService merchantService, @Lazy OrderService orderService, ShippingOrderService shippingOrderService, ItemService itemService) {
        this.merchantOrderRepository = merchantOrderRepository;
        this.userService = userService;
        this.merchantService = merchantService;
        this.orderService = orderService;
        this.shippingOrderService = shippingOrderService;
        this.itemService = itemService;
    }

    public List<MerchantOrderDTO> getAllMerchantOrders() {
        List<MerchantOrderDTO> merchantOrders = new ArrayList<>();

        this.merchantOrderRepository.findAll()
                .forEach(merchantOrder -> merchantOrders.add(new MerchantOrderDTO(merchantOrder)));

        return merchantOrders;
    }

    public List<MerchantOrderDTO> getUserMerchantOrders(String authorizationToken) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        List<MerchantOrderDTO> merchantOrders = new ArrayList<>();

        this.merchantOrderRepository.findByMerchantEmail(user.getAccount().getEmail())
                .forEach(merchantOrder -> merchantOrders.add(new MerchantOrderDTO(merchantOrder)));
        return merchantOrders;
    }

    public MerchantOrderDTO getUserMerchantOrder(String authorizationToken, int id) throws NotFoundException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(authorizationToken, id);
        return new MerchantOrderDTO(merchantOrder);
    }

    public MerchantOrder createMerchantOrder(User user, Order order, int merchantId) throws NotFoundException {
        Merchant merchant = this.merchantService.getMerchant(merchantId).dtoToEntity();

        MerchantOrder merchantOrder = new MerchantOrder(user, order, merchant);

        merchantOrder = this.merchantOrderRepository.save(merchantOrder);

        return merchantOrder;
    }

    public MerchantOrderUpdateDTO fullCancelMerchantOrder(String authorizationToken, int id, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (!isIdEqualToOrderId(id, merchantOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        MerchantOrder merchantOrder = getUserMerchantOrderById(authorizationToken, merchantOrderUpdateDTO.getId());


        merchantOrder = changeMerchantOrderStatus(authorizationToken, merchantOrder.getId(), MerchantOrderStatusEnum.CANCELLED);

        OrderUpdateDTO orderUpdateDTO = this.orderService.fullCancelOrderByOrderId(authorizationToken, merchantOrder.getOrder().getId());
        merchantOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
        this.shippingOrderService.fullCancelShippingOrderByMerchantOrder(authorizationToken, merchantOrder);

        addItemStock(authorizationToken, merchantOrder);

        merchantOrder = this.merchantOrderRepository.findById(merchantOrder.getId()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

        return new MerchantOrderUpdateDTO(merchantOrder);
    }

    public MerchantOrderUpdateDTO fullCancelMerchantOrderByOrder(String authorizationToken, Order order) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderByOrder(authorizationToken, order);

        if (!MerchantOrderStatusEnum.CANCELLED.equals(merchantOrder.getStatus())) {
            merchantOrder = changeMerchantOrderStatus(authorizationToken, merchantOrder.getId(), MerchantOrderStatusEnum.CANCELLED);
        }

        return new MerchantOrderUpdateDTO(merchantOrder);
    }

    public MerchantOrderUpdateDTO fullCancelMerchantOrderByShippingOrder(String authorizationToken, ShippingOrder shippingOrder) throws NotFoundException, WrongFlowException {
        return fullCancelMerchantOrderByOrder(authorizationToken, shippingOrder.getOrder());
    }

    public MerchantOrderUpdateDTO rejectMerchantOrder(String authorizationToken, int id, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (!isIdEqualToOrderId(id, merchantOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        MerchantOrder merchantOrder = getUserMerchantOrderById(authorizationToken, merchantOrderUpdateDTO.getId());

        merchantOrder = changeMerchantOrderStatus(authorizationToken, merchantOrder.getId(), MerchantOrderStatusEnum.REJECTED);

        OrderUpdateDTO orderUpdateDTO = this.orderService.rejectOrderByOrderId(authorizationToken, merchantOrder.getOrder().getId());
        merchantOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
        this.shippingOrderService.rejectShippingOrderByMerchantOrder(authorizationToken, merchantOrder);

        addItemStock(authorizationToken, merchantOrder);

        merchantOrder = this.merchantOrderRepository.findById(merchantOrder.getId()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

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

    public MerchantOrderUpdateDTO approveMerchantOrder(String authorizationToken, int id, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (!isIdEqualToOrderId(id, merchantOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

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

        return this.merchantOrderRepository.findById(id)
                .filter(o -> o.getMerchant().getEmail().compareTo(user.getAccount().getEmail()) == 0)
                .orElseThrow(() -> new NotFoundException("Merchant Order not found"));
    }

    private MerchantOrder getUserMerchantOrderByOrder(String authorizationToken, Order order) throws NotFoundException {
        User user = this.userService.getUserByToken(authorizationToken);

        return this.merchantOrderRepository.findByOrder(order)
                .filter(o -> o.getMerchant().getEmail().compareTo(user.getAccount().getEmail()) == 0)
                .orElseThrow(() -> new NotFoundException("Merchant Order not found"));
    }

    private MerchantOrder changeMerchantOrderStatus(String authorizationToken, int id, MerchantOrderStatusEnum status) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(authorizationToken, id);

        if (isMerchantOrderFlowValid(authorizationToken, merchantOrder, status)) {
            merchantOrder.setStatus(status);

            merchantOrder = this.merchantOrderRepository.save(merchantOrder);
            return merchantOrder;
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

    private boolean isIdEqualToOrderId(int id, MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        return id == merchantOrderUpdateDTO.getId();
    }

    private void addItemStock(String authorizationToken, MerchantOrder merchantOrder) throws InvalidQuantityException, BadPayloadException, NotFoundException {
        for (ItemQuantity itemQuantity : merchantOrder.getOrder().getItemQuantities()) {
            this.itemService.addItemStock(authorizationToken, itemQuantity.getItem().getId(), new ItemUpdateDTO(itemQuantity.getItem()));
        }
    }
}
