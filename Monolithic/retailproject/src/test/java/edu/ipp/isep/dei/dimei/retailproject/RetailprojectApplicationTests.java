package edu.ipp.isep.dei.dimei.retailproject;

import edu.ipp.isep.dei.dimei.retailproject.controllers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class RetailprojectApplicationTests {

    @Autowired
    AuthenticationController authenticationController;
    //@Test
    void contextLoads() {
    }

    @Test
    void sanity_check() {
        List.of(
                authenticationController
        ).forEach(Assertions::assertNotNull);
    }

}
