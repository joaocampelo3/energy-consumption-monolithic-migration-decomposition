package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
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

    private static final String BADPAYLOADEXCEPTIONMESSAGE = "Wrong merchant order payload.";
    private static final String NOTFOUNDEXCEPTIONMESSAGE = "Merchant Order not found.";
    private final MerchantOrderRepository merchantOrderRepository;
    private final MerchantService merchantService;
    private final OrderService orderService;
    private final ShippingOrderService shippingOrderService;

    public MerchantOrderService(MerchantOrderRepository merchantOrderRepository, MerchantService merchantService, @Lazy OrderService orderService, ShippingOrderService shippingOrderService) {
        this.merchantOrderRepository = merchantOrderRepository;
        this.merchantService = merchantService;
        this.orderService = orderService;
        this.shippingOrderService = shippingOrderService;
    }

    public List<MerchantOrderDTO> getAllMerchantOrders(UserDTO userDTO) {
        List<MerchantOrderDTO> merchantOrders = new ArrayList<>();

        this.merchantOrderRepository.findAll()
                .forEach(merchantOrder -> merchantOrders.add(new MerchantOrderDTO(merchantOrder, userDTO.getEmail())));

        return merchantOrders;
    }

    public List<MerchantOrderDTO> getUserMerchantOrders(UserDTO userDTO) {
        List<MerchantOrderDTO> merchantOrders = new ArrayList<>();

        this.merchantOrderRepository.findByMerchantEmail(userDTO.getEmail())
                .forEach(merchantOrder -> merchantOrders.add(new MerchantOrderDTO(merchantOrder, userDTO.getEmail())));
        return merchantOrders;
    }

    public MerchantOrderDTO getUserMerchantOrder(UserDTO userDTO, int id) throws NotFoundException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(userDTO, id);
        return new MerchantOrderDTO(merchantOrder, userDTO.getEmail());
    }

    public MerchantOrder createMerchantOrder(UserDTO userDTO, Order order, int merchantId) throws NotFoundException {
        Merchant merchant = this.merchantService.getMerchant(merchantId).dtoToEntity();

        MerchantOrder merchantOrder = new MerchantOrder(userDTO.getUserId(), order, merchant);

        merchantOrder = this.merchantOrderRepository.save(merchantOrder);

        return merchantOrder;
    }

    public MerchantOrderUpdateDTO fullCancelMerchantOrder(int id, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (isIdNotEqualToOrderId(id, merchantOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        MerchantOrder merchantOrder = getUserMerchantOrderById(merchantOrderUpdateDTO.getUserDTO(), merchantOrderUpdateDTO.getId());


        merchantOrder = changeMerchantOrderStatus(merchantOrderUpdateDTO.getUserDTO(), merchantOrder.getId(), MerchantOrderStatusEnum.CANCELLED);

        OrderUpdateDTO orderUpdateDTO = this.orderService.fullCancelOrderByOrderId(merchantOrderUpdateDTO.getUserDTO(), merchantOrder.getOrder().getId(), false);
        merchantOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
        this.shippingOrderService.fullCancelShippingOrderByMerchantOrder(merchantOrderUpdateDTO.getUserDTO(), merchantOrder);

        merchantOrder = this.merchantOrderRepository.findById(merchantOrder.getId()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

        return new MerchantOrderUpdateDTO(merchantOrder, merchantOrderUpdateDTO.getUserDTO().getEmail());
    }

    public MerchantOrderUpdateDTO fullCancelMerchantOrderByOrder(UserDTO userDTO, Order order) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderByOrder(userDTO, order);

        if (!merchantOrder.isCancelled()) {
            merchantOrder = changeMerchantOrderStatus(userDTO, merchantOrder.getId(), MerchantOrderStatusEnum.CANCELLED);
        }

        return new MerchantOrderUpdateDTO(merchantOrder, userDTO.getEmail());
    }

    public MerchantOrderUpdateDTO fullCancelMerchantOrderByShippingOrder(UserDTO userDTO, ShippingOrder shippingOrder) throws NotFoundException, WrongFlowException {
        return fullCancelMerchantOrderByOrder(userDTO, shippingOrder.getOrder());
    }

    public MerchantOrderUpdateDTO rejectMerchantOrder(int id, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException, InvalidQuantityException {
        if (isIdNotEqualToOrderId(id, merchantOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        MerchantOrder merchantOrder = getUserMerchantOrderById(merchantOrderUpdateDTO.getUserDTO(), merchantOrderUpdateDTO.getId());

        merchantOrder = changeMerchantOrderStatus(merchantOrderUpdateDTO.getUserDTO(), merchantOrder.getId(), MerchantOrderStatusEnum.REJECTED);

        OrderUpdateDTO orderUpdateDTO = this.orderService.rejectOrderByOrderId(merchantOrderUpdateDTO.getUserDTO(), merchantOrder.getOrder().getId());
        merchantOrder.getOrder().setStatus(orderUpdateDTO.getOrderStatus());
        this.shippingOrderService.rejectShippingOrderByMerchantOrder(merchantOrderUpdateDTO.getUserDTO(), merchantOrder);

        merchantOrder = this.merchantOrderRepository.findById(merchantOrder.getId()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));

        return new MerchantOrderUpdateDTO(merchantOrder, merchantOrderUpdateDTO.getUserDTO().getEmail());
    }

    public MerchantOrderUpdateDTO rejectMerchantOrderByOrder(UserDTO userDTO, Order order) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderByOrder(userDTO, order);

        if (!MerchantOrderStatusEnum.REJECTED.equals(merchantOrder.getStatus())) {
            merchantOrder = changeMerchantOrderStatus(userDTO, merchantOrder.getId(), MerchantOrderStatusEnum.REJECTED);
        }

        return new MerchantOrderUpdateDTO(merchantOrder, userDTO.getEmail());
    }

    public MerchantOrderUpdateDTO rejectMerchantOrderByShippingOrder(UserDTO userDTO, ShippingOrder shippingOrder) throws NotFoundException, WrongFlowException {
        return rejectMerchantOrderByOrder(userDTO, shippingOrder.getOrder());
    }

    public MerchantOrderUpdateDTO approveMerchantOrder(int id, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws NotFoundException, WrongFlowException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, merchantOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        MerchantOrder merchantOrder = getUserMerchantOrderById(merchantOrderUpdateDTO.getUserDTO(), merchantOrderUpdateDTO.getId());

        if (!MerchantOrderStatusEnum.APPROVED.equals(merchantOrder.getStatus())) {
            merchantOrder = changeMerchantOrderStatus(merchantOrderUpdateDTO.getUserDTO(), merchantOrder.getId(), MerchantOrderStatusEnum.APPROVED);
        }
        return new MerchantOrderUpdateDTO(merchantOrder, merchantOrderUpdateDTO.getUserDTO().getEmail());
    }

    public MerchantOrder shipMerchantOrder(UserDTO userDTO, int id) throws NotFoundException, WrongFlowException {
        return changeMerchantOrderStatus(userDTO, id, MerchantOrderStatusEnum.SHIPPED);
    }

    public MerchantOrder deliverMerchantOrder(UserDTO userDTO, int id) throws NotFoundException, WrongFlowException {
        return changeMerchantOrderStatus(userDTO, id, MerchantOrderStatusEnum.DELIVERED);
    }

    MerchantOrder getUserMerchantOrderById(UserDTO userDTO, int id) throws NotFoundException {
        switch (userDTO.getRole()) {
            case MERCHANT -> {
                return this.merchantOrderRepository.findById(id)
                        .filter(o -> o.getMerchant().getEmail().compareTo(userDTO.getEmail()) == 0)
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            case ADMIN -> {
                return this.merchantOrderRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            case USER -> {
                return this.merchantOrderRepository.findById(id)
                        .filter(o -> o.getUserId() == userDTO.getUserId())
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            default -> throw new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE);
        }
    }

    MerchantOrder getUserMerchantOrderByOrder(UserDTO userDTO, Order order) throws NotFoundException {
        switch (userDTO.getRole()) {
            case MERCHANT -> {
                return this.merchantOrderRepository.findByOrder(order)
                        .filter(o -> o.getMerchant().getEmail().compareTo(userDTO.getEmail()) == 0)
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            case ADMIN -> {
                return this.merchantOrderRepository.findByOrder(order)
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            case USER -> {
                return this.merchantOrderRepository.findByOrder(order)
                        .filter(o -> o.getUserId() == userDTO.getUserId())
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            default -> throw new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE);
        }
    }

    private MerchantOrder changeMerchantOrderStatus(UserDTO userDTO, int id, MerchantOrderStatusEnum status) throws NotFoundException, WrongFlowException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(userDTO, id);

        if (isMerchantOrderFlowValid(userDTO, merchantOrder, status)) {
            merchantOrder.setStatus(status);

            merchantOrder = this.merchantOrderRepository.save(merchantOrder);
            return merchantOrder;
        } else {
            throw new WrongFlowException("It is not possible to change Merchant Order status");
        }
    }

    private boolean isMerchantOrderFlowValid(UserDTO userDTO, MerchantOrder merchantOrder, MerchantOrderStatusEnum newStatus) throws NotFoundException {
        OrderDTO orderDTO = this.orderService.getUserOrder(userDTO, merchantOrder.getOrder().getId());
        ShippingOrderDTO shippingOrderDTO = this.shippingOrderService.getUserShippingOrder(userDTO, merchantOrder.getOrder().getId());

        switch (newStatus) {
            case PENDING -> {
                return true;
            }
            case APPROVED -> {
                return merchantOrder.isPending() && orderDTOIsPendingOrApproved(orderDTO) && shippingOrderDTO.isPendingOrApproved();
            }
            case REJECTED -> {
                return (merchantOrder.isPending() || merchantOrder.isApproved()) && orderDTOIsPendingOrApprovedOrRejected(orderDTO) && shippingOrderDTO.isPendingOrApprovedOrRejected();
            }
            case CANCELLED -> {
                return (merchantOrder.isPending() || merchantOrder.isApproved()) && orderDTOIsPendingOrApprovedOrCancelled(orderDTO) && shippingOrderDTO.isPendingOrApprovedOrCancelled();
            }
            case SHIPPED -> {
                return merchantOrder.isApproved() && orderDTOIsApprovedOrShipped(orderDTO) && shippingOrderDTO.isShipped();
            }
            case DELIVERED -> {
                return merchantOrder.isShipped() && orderDTOIsShippedOrDelivered(orderDTO) && shippingOrderDTO.isDelivered();
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isIdNotEqualToOrderId(int id, MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        return id != merchantOrderUpdateDTO.getId();
    }

    protected void deleteMerchantOrderByOrderId(int orderId) {
        this.merchantOrderRepository.deleteByOrderId(orderId);
    }

    private boolean orderDTOIsPendingOrApproved(OrderDTO orderDTO) {
        return orderDTO.isPending() || orderDTO.isApproved();
    }

    private boolean orderDTOIsPendingOrApprovedOrRejected(OrderDTO orderDTO) {
        return orderDTOIsPendingOrApproved(orderDTO) || orderDTO.isRejected();
    }

    private boolean orderDTOIsPendingOrApprovedOrCancelled(OrderDTO orderDTO) {
        return orderDTOIsPendingOrApproved(orderDTO) || orderDTO.isCancelled();
    }

    private boolean orderDTOIsApprovedOrShipped(OrderDTO orderDTO) {
        return orderDTO.isApproved() || orderDTO.isShipped();
    }

    private boolean orderDTOIsShippedOrDelivered(OrderDTO orderDTO) {
        return orderDTO.isShipped() || orderDTO.isDelivered();
    }

}
