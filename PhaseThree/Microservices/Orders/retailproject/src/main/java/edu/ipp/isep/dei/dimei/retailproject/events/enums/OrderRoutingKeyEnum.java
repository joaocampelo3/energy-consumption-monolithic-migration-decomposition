package edu.ipp.isep.dei.dimei.retailproject.events.enums;

public enum OrderRoutingKeyEnum {
    ORDER_CREATED("order_created"),
    ORDER_FULL_CANCEL("order_full_cancel"),
    ORDER_REJECTED("order_rejected"),
    ORDER_APPROVED("order_approved"),
    ORDER_SHIPPED("order_shipped"),
    ORDER_DELIVERED("order_delivered"),
    ORDER_DELETED("order_deleted");

    private final String key;

    OrderRoutingKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getOrderKey() {
        return "order." + key;
    }
}
