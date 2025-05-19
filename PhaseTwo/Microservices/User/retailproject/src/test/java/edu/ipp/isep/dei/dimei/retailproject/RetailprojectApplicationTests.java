package edu.ipp.isep.dei.dimei.retailproject;

import edu.ipp.isep.dei.dimei.retailproject.controllers.AuthenticationController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class RetailprojectApplicationTests {

    @Autowired
    AuthenticationController authenticationController;
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void sanity_check() {
        List.of(
                authenticationController
        ).forEach(Assertions::assertNotNull);
    }

    @Test
    void testSpringApplicationRun() {
        MockedStatic<SpringApplication> springApplicationMock = Mockito.mockStatic(SpringApplication.class);

        String[] args = {"--spring.main.web-environment=false"};

        // Call the main method
        RetailprojectApplication.main(args);

        // Verify SpringApplication.run() was called with the correct arguments
        springApplicationMock.verify(() -> SpringApplication.run(RetailprojectApplication.class, args));

        springApplicationMock.close();
    }
}
