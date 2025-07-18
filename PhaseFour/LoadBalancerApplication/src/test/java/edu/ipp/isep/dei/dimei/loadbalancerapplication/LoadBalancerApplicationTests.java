package edu.ipp.isep.dei.dimei.loadbalancerapplication;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoadBalancerApplicationTests {

    @Test
    void contextLoads() {
        MockedStatic<SpringApplication> springApplicationMock = Mockito.mockStatic(SpringApplication.class);

        String[] args = {"--spring.main.web-environment=false"};

        // Call the main method
        LoadBalancerApplication.main(args);

        // Verify SpringApplication.run() was called with the correct arguments
        springApplicationMock.verify(() -> SpringApplication.run(LoadBalancerApplication.class, args));

        springApplicationMock.close();
    }

}
