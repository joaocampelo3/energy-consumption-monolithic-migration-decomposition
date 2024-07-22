package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
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

import static edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum.ADMIN;
import static edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum.MERCHANT;

@Transactional
@Service
public class ShippingOrderService {

    protected static final String BADPAYLOADEXCEPTIONMESSAGE = "Wrong shipping order payload.";
    protected static final String NOTFOUNDEXCEPTIONMESSAGE = "Shipping Order not found.";
    private final ShippingOrderRepository shippingOrderRepository;
    private final MerchantOrderService merchantOrderService;
    private final OrderService orderService;
    private final ItemService itemService;

    public ShippingOrderService(ShippingOrderRepository shippingOrderRepository, @Lazy MerchantOrderService merchantOrderService, @Lazy OrderService orderService, ItemService itemService) {
        this.shippingOrderRepository = shippingOrderRepository;
        this.merchantOrderService = merchantOrderService;
        this.orderService = orderService;
        this.itemService = itemService;
    }

    public List<ShippingOrderDTO> getAllShippingOrders(UserDTO userDTO) {
        List<ShippingOrderDTO> shippingOrders = new ArrayList<>();

        this.shippingOrderRepository.findAll()
                .forEach(shippingOrder -> shippingOrders.add(new ShippingOrderDTO(shippingOrder, userDTO.getEmail())));

        return shippingOrders;
    }

    public List<ShippingOrderDTO> getUserShippingOrders(UserDTO userDTO) {
        List<ShippingOrderDTO> shippingOrders = new ArrayList<>();

        this.shippingOrderRepository.findByUserId(userDTO.getUserId()).forEach(shippingOrder -> shippingOrders.add(new ShippingOrderDTO(shippingOrder, userDTO.getEmail())));
        return shippingOrders;
    }

    public ShippingOrderDTO getUserShippingOrder(UserDTO userDTO, int id) throws NotFoundException {
        ShippingOrder shippingOrder = getUserShippingOrderById(id, userDTO);
        return new ShippingOrderDTO(shippingOrder, userDTO.getEmail());
    }

    public void createShippingOrder(UserDTO userDTO, Order order, MerchantOrder merchantOrder, int shippingAddressId) {
        ShippingOrder shippingOrder = new ShippingOrder(userDTO.getUserId(), order, merchantOrder, shippingAddressId);

        this.shippingOrderRepository.save(shippingOrder);
    }

    public ShippingOrderUpdateDTO fullCancelShippingOrder(int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (isIdNotEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(shippingOrderUpdateDTO.getUserDTO(), shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.CANCELLED);

        OrderUpdateDTO orderUpdateDTO = this.orderService.fullCancelOrderByOrderId(shippingOrderUpdateDTO.getUserDTO(), shippingOrder.getOrder().getId());
        shippingOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
        this.merchantOrderService.fullCancelMerchantOrderByShippingOrder(shippingOrderUpdateDTO.getUserDTO(), shippingOrder);

        addItemStock(shippingOrder);

        shippingOrder = this.shippingOrderRepository.findById(shippingOrder.getId()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO fullCancelShippingOrderByOrder(UserDTO userDTO, Order order) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = getUserShippingOrderByOrder(userDTO, order);

        shippingOrder = changeShippingOrderStatus(userDTO, shippingOrder.getId(), ShippingOrderStatusEnum.CANCELLED);

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO fullCancelShippingOrderByMerchantOrder(UserDTO userDTO, MerchantOrder merchantOrder) throws NotFoundException, WrongFlowException {
        return fullCancelShippingOrderByOrder(userDTO, merchantOrder.getOrder());
    }

    public ShippingOrderUpdateDTO rejectShippingOrder(int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (isIdNotEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(shippingOrderUpdateDTO.getUserDTO(), shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.REJECTED);

        OrderUpdateDTO orderUpdateDTO = this.orderService.rejectOrderByOrderId(shippingOrderUpdateDTO.getUserDTO(), shippingOrder.getOrder().getId());
        shippingOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
        this.merchantOrderService.rejectMerchantOrderByShippingOrder(shippingOrderUpdateDTO.getUserDTO(), shippingOrder);

        addItemStock(shippingOrder);

        shippingOrder = this.shippingOrderRepository.findById(shippingOrder.getId()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO rejectShippingOrderByOrder(UserDTO userDTO, Order order) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = getUserShippingOrderByOrder(userDTO, order);

        if (!shippingOrder.isRejected()) {
            shippingOrder = changeShippingOrderStatus(userDTO, shippingOrder.getId(), ShippingOrderStatusEnum.REJECTED);
        }

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO rejectShippingOrderByMerchantOrder(UserDTO userDTO, MerchantOrder merchantOrder) throws NotFoundException, WrongFlowException {
        return rejectShippingOrderByOrder(userDTO, merchantOrder.getOrder());
    }

    public ShippingOrderUpdateDTO approveShippingOrder(int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(shippingOrderUpdateDTO.getUserDTO(), shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.APPROVED);

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO shippedShippingOrder(int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(shippingOrderUpdateDTO.getUserDTO(), shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.SHIPPED);

        this.merchantOrderService.shipMerchantOrder(shippingOrderUpdateDTO.getUserDTO(), shippingOrder.getMerchantOrder().getId());
        this.orderService.shipOrder(shippingOrderUpdateDTO.getUserDTO(), shippingOrder.getOrder().getId());

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    public ShippingOrderUpdateDTO deliveredShippingOrder(int id, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, shippingOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        ShippingOrder shippingOrder = changeShippingOrderStatus(shippingOrderUpdateDTO.getUserDTO(), shippingOrderUpdateDTO.getId(), ShippingOrderStatusEnum.DELIVERED);

        this.merchantOrderService.deliverMerchantOrder(shippingOrderUpdateDTO.getUserDTO(), shippingOrder.getMerchantOrder().getId());
        this.orderService.deliverOrder(shippingOrderUpdateDTO.getUserDTO(), shippingOrder.getOrder().getId());

        return new ShippingOrderUpdateDTO(shippingOrder);
    }

    private ShippingOrder getUserShippingOrderById(int id, UserDTO userDTO) throws NotFoundException {
        switch (userDTO.getRole()) {
            case ADMIN -> {
                return this.shippingOrderRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            case MERCHANT -> {
                return this.shippingOrderRepository.findById(id)
                        .filter(o -> o.getMerchantOrder().getMerchant().getEmail().compareTo(userDTO.getEmail()) == 0)
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            default -> {
                return this.shippingOrderRepository.findById(id)
                        .filter(o -> o.getUserId() == userDTO.getUserId() && userDTO.getRole().equals(RoleEnum.USER))
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
        }
    }

    private ShippingOrder getUserShippingOrderByOrder(UserDTO userDTO, Order order) throws NotFoundException {
        if (userDTO.getRole().equals(ADMIN) || userDTO.getRole().equals(MERCHANT)) {
            return this.shippingOrderRepository.findByOrder(order)
                    .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        } else {
            return this.shippingOrderRepository.findByOrder(order)
                    .filter(shippingOrder -> shippingOrder.getUserId() == userDTO.getUserId() && userDTO.getRole().equals(RoleEnum.USER))
                    .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        }
    }

    private ShippingOrder changeShippingOrderStatus(UserDTO userDTO, int id, ShippingOrderStatusEnum status) throws NotFoundException, WrongFlowException {
        ShippingOrder shippingOrder = getUserShippingOrderById(id, userDTO);

        if (isShippingOrderFlowValid(userDTO, shippingOrder, status)) {
            shippingOrder.setStatus(status);

            shippingOrder = this.shippingOrderRepository.save(shippingOrder);
            return shippingOrder;
        } else {
            throw new WrongFlowException("It is not possible to change Shipping Order status");
        }
    }

    private boolean isShippingOrderFlowValid(UserDTO userDTO, ShippingOrder shippingOrder, ShippingOrderStatusEnum newStatus) throws NotFoundException {
        OrderDTO orderDTO = this.orderService.getUserOrder(userDTO, shippingOrder.getOrder().getId());
        MerchantOrderDTO merchantOrderDTO = this.merchantOrderService.getUserMerchantOrder(userDTO, shippingOrder.getOrder().getId());

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

    private void addItemStock(ShippingOrder shippingOrder) throws InvalidQuantityException, BadPayloadException, NotFoundException {
        ItemUpdateDTO itemUpdateDTO;

        for (ItemQuantity itemQuantity : shippingOrder.getOrder().getItemQuantities()) {
            itemUpdateDTO = new ItemUpdateDTO(itemQuantity.getItem());
            itemUpdateDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + itemQuantity.getQuantityOrdered().getQuantity());
            this.itemService.addItemStock(itemQuantity.getItem().getId(), itemUpdateDTO);
        }
    }

    protected void deleteShippingOrderByOrderId(int orderId) {
        this.shippingOrderRepository.deleteByOrderId(orderId);
    }
}
