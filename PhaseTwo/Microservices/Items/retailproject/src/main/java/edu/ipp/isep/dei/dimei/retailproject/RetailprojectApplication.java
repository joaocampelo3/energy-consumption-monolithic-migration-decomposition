package edu.ipp.isep.dei.dimei.retailproject;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker.RabbitMQHost;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Category;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.events.ItemEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.CategoryRepository;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemRepository;
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "edu.ipp.isep.dei.dimei.retailproject.repositories")
public class RetailprojectApplication {

    private static final String RPC_QUEUE_NAME = "q.products_rpc_queue";
    private static final Logger logger = LoggerFactory.getLogger(RetailprojectApplication.class);

    @Autowired
    private static RabbitMQHost rabbitMQHost;
    @Autowired
    private static CategoryRepository categoryRepository;
    @Autowired
    private static ItemRepository itemRepository;

    public RetailprojectApplication(RabbitMQHost rabbitMQHost, CategoryRepository categoryRepository, ItemRepository itemRepository) {
        RetailprojectApplication.rabbitMQHost = rabbitMQHost;
        RetailprojectApplication.categoryRepository = categoryRepository;
        RetailprojectApplication.itemRepository = itemRepository;
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(RetailprojectApplication.class, args);

        // Create 1 clients
        RPCClient rpcClient1 = new RPCClient("Product RPC Client");
        rpcClient1.start();

        // Finalize
        rpcClient1.join();
    }

    @Bean
    public CommandLineRunner demoData(CategoryRepository categoryRepository, MerchantRepository merchantRepository, ItemRepository itemRepository) {
        return args -> {
            Merchant merchant = null;
            Category category1 = null;
            Category category2 = null;

            Merchant merchantCheck = merchantRepository.findById(1).orElse(null);
            if (merchantCheck == null) {
                merchant = new Merchant("Merchant Dummy", "merchant@gmail.com");
                merchantRepository.save(merchant);
                merchant.setId(1);
            }

            Category categoryCheck = categoryRepository.findById(1).orElse(null);
            if (categoryCheck == null) {
                category1 = new Category("Category 1 description", "Category 1");
                categoryRepository.save(category1);
                category1.setId(1);
            }

            categoryCheck = categoryRepository.findById(2).orElse(null);
            if (categoryCheck == null) {
                category2 = new Category("Category 2 description", "Category 2");
                categoryRepository.save(category2);
                category2.setId(2);
            }

            if (merchantCheck != null) {
                if (category1 != null) {
                    Item item1 = itemRepository.findById(1).orElse(null);
                    if (item1 == null) {
                        itemRepository.save(new Item("Item 1", "ABC-12345-S-BL", "Item 1 description", 8, 200, category1, merchant));
                    }
                }
                if (category2 != null) {
                    Item item2 = itemRepository.findById(2).orElse(null);
                    if (item2 == null) {
                        itemRepository.save(new Item("Item 2", "ABC-12345-XL-BL", "Item 2 description", 5, 300, category2, merchant));
                    }
                }
            }
        };
    }

    public static class RPCClientImpl implements AutoCloseable {

        private Connection connection;
        private Channel channel;
        private String exclusiveQueueName;

        public RPCClientImpl() throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitMQHost.getHost());
            factory.setPort(Integer.parseInt(rabbitMQHost.getPort()));
            factory.setUsername(rabbitMQHost.getUsername());
            factory.setPassword(rabbitMQHost.getPassword());

            connection = factory.newConnection();
            channel = connection.createChannel();

            // Tip: use "" to generate random name and don't use auto-delete feature, because "basicCancel"
            // we use in the call() method will delete our queue
            exclusiveQueueName = channel.queueDeclare("", false, true, false, null).getQueue();//.getQueue();
            System.out.println("Queue name:" + exclusiveQueueName);
        }

        private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();
        }

        public static List<?> convertObjectToList(Object obj) {
            if(obj == null)
                return null;

            List<?> list = new ArrayList<>();
            if (obj.getClass().isArray()) {
                list = Arrays.asList((Object[]) obj);
            } else if (obj instanceof Collection) {
                list = new ArrayList<>((Collection<?>) obj);
            }
            return list;
        }

        public void call() throws IOException, InterruptedException, ExecutionException {
            String corrId = UUID.randomUUID().toString();

            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(exclusiveQueueName)
                    .build();

            channel.basicPublish(/*exchange*/"", RPC_QUEUE_NAME, props, "GetAllItems".getBytes(StandardCharsets.UTF_8));

            // Code to consume only one message and stop consuming more messages
            AtomicReference<List<String>> response = new AtomicReference<>();

            String ctag = channel.basicConsume(exclusiveQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    String decodedString = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    response.set(Arrays.asList(decodedString.split(";")));
                }
            }, consumerTag -> {
            });

            //Simple delay
            Thread.sleep(7000);

            List<ItemEvent> itemEventList = new ArrayList<>();
            List<String> itemEventStringList = new ArrayList<>();
            itemEventStringList = response.get();
            channel.basicCancel(ctag);
            itemEventStringList = response.get();

            if (itemEventStringList != null && !itemEventStringList.isEmpty()) {
                for (String s : itemEventStringList) {
                    itemEventList.add(ItemEvent.fromJson(s));
                }
            }

            if (itemEventList != null && !itemEventList.isEmpty()) {
                for (ItemEvent itemEvent : itemEventList) {
                    if (EventTypeEnum.CREATE.compareTo(itemEvent.getEventTypeEnum())==0){
                        logger.info("CREATE ACTION: "+ itemEvent.getId());
                        try {
                            itemRepository.save(itemEvent.toItem());
                        } catch (InvalidQuantityException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (EventTypeEnum.UPDATE.compareTo(itemEvent.getEventTypeEnum())==0) {
                        logger.info("UPDATE ACTION: "+ itemEvent.getId());
                        try {
                            itemRepository.save(itemEvent.toItem());
                        } catch (InvalidQuantityException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (EventTypeEnum.DELETE.compareTo(itemEvent.getEventTypeEnum())==0) {
                        logger.info("DELETE ACTION: "+ itemEvent.getId());
                        itemRepository.deleteById(itemEvent.getId());
                    }
                }
            }
        }

        public void close() throws IOException {
            connection.close();
        }
    }


    public static class RPCClient extends Thread {
        private final String m_name;
        private RPCClientImpl m_clientImpl;

        public RPCClient(String name) {
            m_name = name;
        }

        public int getRandomNumber(int min, int max) {
            return (int) ((Math.random() * (max - min)) + min);
        }

        public void run() {
            try {
                // Simple delay
                Thread.sleep(7000);

                m_clientImpl = new RPCClientImpl();

                System.out.println(" [x] " + m_name + " requesting GetAllProducts()");
                m_clientImpl.call();

                Thread.sleep(getRandomNumber(0, 10) * 1000);

                System.out.println(" [x] " + m_name + " request ending...");

            } catch (IOException | InterruptedException | TimeoutException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
