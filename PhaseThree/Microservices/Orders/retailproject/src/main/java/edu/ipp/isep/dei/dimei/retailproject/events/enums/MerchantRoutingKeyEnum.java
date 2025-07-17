package edu.ipp.isep.dei.dimei.retailproject.events.enums;

public enum MerchantRoutingKeyEnum {
    MERCHANT_CREATED("merchant_created"),
    MERCHANT_UPDATED("merchant_updated"),
    MERCHANT_DELETED("merchant_deleted");

    private final String key;

    MerchantRoutingKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getMerchantKey() {
        return "merchant." + key;
    }
}
