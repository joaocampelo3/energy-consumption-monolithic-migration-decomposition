package edu.ipp.isep.dei.dimei.retailproject.domain.enums;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MerchantOrderStatusEnumTest {

    @Test
    void test_MerchantOrderStatusEnum_Success(){
        assertAll(
                () -> assertEquals("PENDING", MerchantOrderStatusEnum.PENDING.name()),
                () -> assertEquals("APPROVED", MerchantOrderStatusEnum.APPROVED.name()),
                () -> assertEquals("REJECTED", MerchantOrderStatusEnum.REJECTED.name()),
                () -> assertEquals("SHIPPED", MerchantOrderStatusEnum.SHIPPED.name()),
                () -> assertEquals("DELIVERED", MerchantOrderStatusEnum.DELIVERED.name()),
                () -> assertEquals("CANCELLED", MerchantOrderStatusEnum.CANCELLED.name())
        );

    }

}
