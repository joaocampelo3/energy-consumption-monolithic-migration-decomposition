package edu.ipp.isep.dei.dimei.retailproject;

import edu.ipp.isep.dei.dimei.retailproject.controllers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class RetailprojectApplicationTests {

    @Autowired
    AuthenticationController authenticationController;
    @Autowired
    CategoryController categoryController;
    @Autowired
    ItemController itemController;
    @Autowired
    MerchantController merchantController;
    @Autowired
    MerchantOrderController merchantOrderController;
    @Autowired
    OrderController orderController;
    @Autowired
    ShippingOrderController shippingOrderController;

    @Test
    void sanity_check() {
        List.of(
                authenticationController,
                categoryController,
                itemController,
                merchantController,
                merchantOrderController,
                orderController,
                shippingOrderController
        ).forEach(Assertions::assertNotNull);
    }

}
