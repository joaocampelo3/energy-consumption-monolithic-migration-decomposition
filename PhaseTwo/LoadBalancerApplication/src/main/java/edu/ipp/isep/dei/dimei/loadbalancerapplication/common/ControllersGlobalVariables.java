package edu.ipp.isep.dei.dimei.loadbalancerapplication.common;

public class ControllersGlobalVariables {
    public static final String MONOLITH_URL = "http://RETAILPROJECTMONOLITH";
    public static final String USERS_URL = "http://USERMICROSERVICEAPP";
    public static final String ITEMS_URL = "http://ITEMMICROSERVICEAPP";
    public static final String CATEGORY_URL = ITEMS_URL + "/categories";
    public static final String ITEM_URL = ITEMS_URL + "/items";
    public static final String MERCHANT_URL = MONOLITH_URL + "/merchants";
    public static final String MERCHANT_ORDER_URL = MONOLITH_URL + "/merchantorders";
    public static final String ORDER_URL = MONOLITH_URL + "/orders";
    public static final String SHIPPING_ORDER_URL = MONOLITH_URL + "/shippingorders";

    private ControllersGlobalVariables() {
    }
}
