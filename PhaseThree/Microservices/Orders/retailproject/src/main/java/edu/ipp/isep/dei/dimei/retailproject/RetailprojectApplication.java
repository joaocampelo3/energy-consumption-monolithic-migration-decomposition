package edu.ipp.isep.dei.dimei.retailproject;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "edu.ipp.isep.dei.dimei.retailproject.repositories")
public class RetailprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetailprojectApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(ItemRepository itemRepository) {
        return args -> {
            Item item1 = itemRepository.findById(1).orElse(itemRepository.save(new Item("Item 1", "ABC-12345-S-BL", "Item 1 description", 8, 20000, 1, 1)));
            Item item2 = itemRepository.findById(2).orElse(itemRepository.save(new Item("Item 2", "ABC-12345-XL-BL", "Item 2 description", 5, 30000, 2, 1)));
        };
    }
}
