package edu.ipp.isep.dei.dimei.retailproject.events.enums;

public enum ItemRoutingKeyEnum {
    ITEM_CREATED("item_created"),
    ITEM_ADD_STOCK("item_add_stock"),
    ITEM_REMOVE_STOCK("item_remove_stock"),
    ITEM_DELETED("item_deleted");

    private final String key;

    ItemRoutingKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getItemKey() {
        return "item." + key;
    }
}
