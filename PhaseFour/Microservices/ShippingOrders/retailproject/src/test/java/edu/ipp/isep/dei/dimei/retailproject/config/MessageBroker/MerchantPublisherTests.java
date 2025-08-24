package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.MerchantEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MerchantPublisherTests {
    private final static String EXCHANGE_NAME = "merchant";
    @InjectMocks
    MerchantPublisher merchantPublisher;
    @Mock
    RabbitMQHost rabbitMQHost;
    @Mock
    ConnectionFactory connectionFactory;
    @Mock
    Connection connection;
    @Mock
    Channel channel;

    UserDTO userDTO;
    AddressDTO addressDTO;
    MerchantDTO merchantDTO;
    MerchantEvent merchantEvent;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException {
        userDTO = UserDTO.builder().userId(1).email("merchant1@gmail.com").role(RoleEnum.MERCHANT).build();
        addressDTO = AddressDTO.builder().id(1).street("5th Avenue").zipCode("10128").city("New York").country("USA").build();
        merchantDTO = MerchantDTO.builder().id(1).name("Merchant1").email(userDTO.getEmail()).addressId(addressDTO.getId()).userDTO(userDTO).addressDTO(addressDTO).build();
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    void test_PublishEventSkelleton(MerchantDTO merchantDTO, EventTypeEnum eventTypeEnum) throws IOException, TimeoutException {
        merchantEvent = new MerchantEvent(merchantDTO, eventTypeEnum);

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> merchantPublisher.publishEvent(merchantEvent));

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verify(connection).createChannel();
        verify(channel).exchangeDeclare(EXCHANGE_NAME, "topic");
    }

    @Test
    void test_PublishEvent_Create() throws IOException, TimeoutException {
        test_PublishEventSkelleton(merchantDTO, EventTypeEnum.CREATE);
    }

    @Test
    void test_PublishEvent_Delete() throws IOException, TimeoutException {
        test_PublishEventSkelleton(merchantDTO, EventTypeEnum.DELETE);
    }

    @Test
    void test_PublishEvent_Update() throws IOException, TimeoutException {
        test_PublishEventSkelleton(merchantDTO, EventTypeEnum.UPDATE);
    }
}
