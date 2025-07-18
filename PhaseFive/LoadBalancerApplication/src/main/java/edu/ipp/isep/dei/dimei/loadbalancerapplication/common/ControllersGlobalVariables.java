package edu.ipp.isep.dei.dimei.loadbalancerapplication.common;

public class ControllersGlobalVariables {
    /**WRITE*/
    public static final String USERS_URL = "http://USERMICROSERVICEAPP";
    public static final String ITEMS_URL = "http://ITEMMICROSERVICEAPP";
    public static final String ORDERS_URL = "http://ORDERMICROSERVICEAPP";
    public static final String MERCHANT_ORDERS_URL = "http://MERCHANTORDERMICROSERVICEAPP";
    public static final String SHIPPING_ORDERS_URL = "http://SHIPPINGORDERMICROSERVICEAPP";
    public static final String CATEGORY_URL = ITEMS_URL + "/categories";
    public static final String ITEM_URL = ITEMS_URL + "/items";
    public static final String MERCHANT_URL = MERCHANT_ORDERS_URL + "/merchants";
    public static final String MERCHANT_ORDER_URL = MERCHANT_ORDERS_URL + "/merchantorders";
    public static final String ORDER_URL = ORDERS_URL + "/orders";
    public static final String SHIPPING_ORDER_URL = SHIPPING_ORDERS_URL + "/shippingorders";

    /**READ*/
    public static final String ITEMS_READ_URL = "http://ITEMREADMICROSERVICEAPP";
    public static final String ORDERS_READ_URL = "http://ORDERREADMICROSERVICEAPP";
    public static final String MERCHANT_ORDERS_READ_URL = "http://MERCHANTORDERREADMICROSERVICEAPP";
    public static final String SHIPPING_ORDERS_READ_URL = "http://SHIPPINGORDERREADMICROSERVICEAPP";
    public static final String CATEGORY_READ_URL = ITEMS_READ_URL + "/categories";
    public static final String ITEM_READ_URL = ITEMS_READ_URL + "/items";
    public static final String MERCHANT_READ_URL = MERCHANT_ORDERS_READ_URL + "/merchants";
    public static final String MERCHANT_ORDER_READ_URL = MERCHANT_ORDERS_READ_URL + "/merchantorders";
    public static final String ORDER_READ_URL = ORDERS_READ_URL + "/orders";
    public static final String SHIPPING_ORDER_READ_URL = SHIPPING_ORDERS_READ_URL + "/shippingorders";

    private ControllersGlobalVariables() {
    }
}
