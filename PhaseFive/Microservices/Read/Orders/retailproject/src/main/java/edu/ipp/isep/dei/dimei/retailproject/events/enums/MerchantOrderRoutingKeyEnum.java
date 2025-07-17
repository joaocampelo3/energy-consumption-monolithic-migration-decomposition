package edu.ipp.isep.dei.dimei.retailproject.events.enums;

public enum MerchantOrderRoutingKeyEnum {
    MERCHANT_ORDER_CREATED("merchant_order_created"),
    MERCHANT_ORDER_FULL_CANCEL("merchant_order_full_cancel"),
    MERCHANT_ORDER_REJECTED("merchant_order_rejected"),
    MERCHANT_ORDER_APPROVED("merchant_order_approved"),
    MERCHANT_ORDER_SHIPPED("merchant_order_shipped"),
    MERCHANT_ORDER_DELIVERED("merchant_order_delivered"),
    MERCHANT_ORDER_DELETED("merchant_order_deleted");

    private final String key;

    MerchantOrderRoutingKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getMerchantOrderKey() {
        return "merchantorder." + key;
    }
}
