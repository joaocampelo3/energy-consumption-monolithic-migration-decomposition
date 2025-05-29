package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.ConnectionFactory;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.rabbitmq")
@RequiredArgsConstructor
@Getter
@Setter
public class RabbitMQHost {

    @Value("${spring.rabbitmq.host}")
    @NotBlank
    private String host;

    @Value("${spring.rabbitmq.port}")
    @NotBlank
    private String port;

    @Value("${spring.rabbitmq.username}")
    @NotBlank
    private String username;

    @Value("${spring.rabbitmq.password}")
    @NotBlank
    private String password;

    public ConnectionFactory getFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(getHost());
        factory.setPort(Integer.parseInt(getPort()));
        factory.setUsername(getUsername());
        factory.setPassword(getPassword());
        return factory;
    }
}
