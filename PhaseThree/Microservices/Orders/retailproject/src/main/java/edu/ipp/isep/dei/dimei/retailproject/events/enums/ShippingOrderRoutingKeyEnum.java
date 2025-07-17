package edu.ipp.isep.dei.dimei.retailproject.events.enums;

public enum ShippingOrderRoutingKeyEnum {
    SHIPPING_ORDER_CREATED("shipping_order_created"),
    SHIPPING_ORDER_FULL_CANCEL("shipping_order_full_cancel"),
    SHIPPING_ORDER_REJECTED("shipping_order_rejected"),
    SHIPPING_ORDER_APPROVED("shipping_order_approved"),
    SHIPPING_ORDER_SHIPPED("shipping_order_shipped"),
    SHIPPING_ORDER_DELIVERED("shipping_order_delivered"),
    SHIPPING_ORDER_DELETED("shipping_order_deleted");

    private final String key;

    ShippingOrderRoutingKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getShippingOrderKey() {
        return "shippingorder." + key;
    }
}
