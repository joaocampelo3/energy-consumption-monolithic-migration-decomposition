package edu.ipp.isep.dei.dimei.retailproject.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ShippingOrderStatusEnumTest {

    @Test
    void test_ShippingOrderStatusEnum_Success() {
        assertAll(
                () -> assertEquals("PENDING", ShippingOrderStatusEnum.PENDING.name()),
                () -> assertEquals("APPROVED", ShippingOrderStatusEnum.APPROVED.name()),
                () -> assertEquals("REJECTED", ShippingOrderStatusEnum.REJECTED.name()),
                () -> assertEquals("SHIPPED", ShippingOrderStatusEnum.SHIPPED.name()),
                () -> assertEquals("DELIVERED", ShippingOrderStatusEnum.DELIVERED.name()),
                () -> assertEquals("CANCELLED", ShippingOrderStatusEnum.CANCELLED.name())
        );

    }

}
