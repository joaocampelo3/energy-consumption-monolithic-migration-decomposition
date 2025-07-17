package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class MerchantOrderHelperService {

    private static final String BADPAYLOADEXCEPTIONMESSAGE = "Wrong merchant order payload.";
    private static final String NOTFOUNDEXCEPTIONMESSAGE = "Merchant Order not found.";
    @Autowired
    private final MerchantOrderRepository merchantOrderRepository;

    public MerchantOrderDTO getUserMerchantOrder(UserDTO userDTO, int id) throws NotFoundException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(userDTO, id, false);
        return new MerchantOrderDTO(merchantOrder, userDTO.getEmail());
    }

    public MerchantOrder createMerchantOrder(UserDTO userDTO, Order order, int merchantId) {
        MerchantOrder merchantOrder = new MerchantOrder(userDTO.getUserId(), order.getId(), order.getOrderDate(), merchantId);

        merchantOrder = this.merchantOrderRepository.save(merchantOrder);

        return merchantOrder;
    }

    public MerchantOrderUpdateDTO fullCancelMerchantOrderByOrder(UserDTO userDTO, int orderId, boolean isEvent) throws NotFoundException {
        MerchantOrder merchantOrder = getUserMerchantOrderByOrder(userDTO, orderId);

        if (!merchantOrder.isCancelled()) {
            merchantOrder = changeMerchantOrderStatus(userDTO, merchantOrder.getId(), MerchantOrderStatusEnum.CANCELLED, isEvent);
        }

        return new MerchantOrderUpdateDTO(merchantOrder, userDTO.getEmail());
    }

    public MerchantOrderUpdateDTO fullCancelMerchantOrderByShippingOrder(UserDTO userDTO, ShippingOrder shippingOrder, boolean isEvent) throws NotFoundException {
        return fullCancelMerchantOrderByOrder(userDTO, shippingOrder.getOrderId(), isEvent);
    }

    public MerchantOrderUpdateDTO rejectMerchantOrderByOrder(UserDTO userDTO, int orderId, boolean isEvent) throws NotFoundException {
        MerchantOrder merchantOrder = getUserMerchantOrderByOrder(userDTO, orderId);

        if (!MerchantOrderStatusEnum.REJECTED.equals(merchantOrder.getStatus())) {
            merchantOrder = changeMerchantOrderStatus(userDTO, merchantOrder.getId(), MerchantOrderStatusEnum.REJECTED, isEvent);
        }

        return new MerchantOrderUpdateDTO(merchantOrder, userDTO.getEmail());
    }

    public MerchantOrderUpdateDTO rejectMerchantOrderByShippingOrder(UserDTO userDTO, ShippingOrder shippingOrder, boolean isEvent) throws NotFoundException {
        return rejectMerchantOrderByOrder(userDTO, shippingOrder.getOrderId(), isEvent);
    }

    public MerchantOrderUpdateDTO approveMerchantOrder(int id, MerchantOrderUpdateDTO merchantOrderUpdateDTO, boolean isEvent) throws NotFoundException, BadPayloadException {
        if (isIdNotEqualToOrderId(id, merchantOrderUpdateDTO)) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        MerchantOrder merchantOrder = getUserMerchantOrderById(merchantOrderUpdateDTO.getUserDTO(), merchantOrderUpdateDTO.getId(), isEvent);

        if (!MerchantOrderStatusEnum.APPROVED.equals(merchantOrder.getStatus())) {
            merchantOrder = changeMerchantOrderStatus(merchantOrderUpdateDTO.getUserDTO(), merchantOrder.getId(), MerchantOrderStatusEnum.APPROVED, isEvent);
        }
        return new MerchantOrderUpdateDTO(merchantOrder, merchantOrderUpdateDTO.getUserDTO().getEmail());
    }

    public MerchantOrder shipMerchantOrder(UserDTO userDTO, int id, boolean isEvent) throws NotFoundException {
        return changeMerchantOrderStatus(userDTO, id, MerchantOrderStatusEnum.SHIPPED, isEvent);
    }

    public MerchantOrder deliverMerchantOrder(UserDTO userDTO, int id, boolean isEvent) throws NotFoundException {
        return changeMerchantOrderStatus(userDTO, id, MerchantOrderStatusEnum.DELIVERED, isEvent);
    }

    public MerchantOrder getUserMerchantOrderById(UserDTO userDTO, int id, boolean isEvent) throws NotFoundException {

        if (isEvent) {
            return this.merchantOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
        }

        switch (userDTO.getRole()) {
            case MERCHANT -> {
                return this.merchantOrderRepository.findById(id)
                        .filter(o -> o.getUserId() == userDTO.getUserId())
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

    public MerchantOrder getUserMerchantOrderByOrder(UserDTO userDTO, int orderId) throws NotFoundException {
        switch (userDTO.getRole()) {
            case MERCHANT -> {
                return this.merchantOrderRepository.findByOrderId(orderId)
                        .filter(o -> o.getUserId() == userDTO.getUserId())
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            case ADMIN -> {
                return this.merchantOrderRepository.findByOrderId(orderId)
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            case USER -> {
                return this.merchantOrderRepository.findByOrderId(orderId)
                        .filter(o -> o.getUserId() == userDTO.getUserId())
                        .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
            }
            default -> throw new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE);
        }
    }

    private MerchantOrder changeMerchantOrderStatus(UserDTO userDTO, int id, MerchantOrderStatusEnum status, boolean isEvent) throws NotFoundException {
        MerchantOrder merchantOrder = getUserMerchantOrderById(userDTO, id, isEvent);

        merchantOrder.setStatus(status);

        merchantOrder = this.merchantOrderRepository.save(merchantOrder);
        return merchantOrder;
    }

    private boolean isIdNotEqualToOrderId(int id, MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        return id != merchantOrderUpdateDTO.getId();
    }

    public void deleteMerchantOrderByOrderId(int orderId, boolean isEvent) {
        this.merchantOrderRepository.deleteByOrderId(orderId);
    }
}
