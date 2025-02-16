package edu.ipp.isep.dei.dimei.retailproject;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Category;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.repositories.CategoryRepository;
import edu.ipp.isep.dei.dimei.retailproject.repositories.ItemRepository;
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantRepository;
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
    public CommandLineRunner demoData(CategoryRepository categoryRepository, MerchantRepository merchantRepository, ItemRepository itemRepository) {
        return args -> {
            Merchant merchant = null;
            Category category1 = null;
            Category category2 = null;

            Merchant merchantCheck = merchantRepository.findById(1).orElse(null);
            if (merchantCheck == null) {
                merchant = new Merchant("Merchant Dummy", "merchant@gmail.com", 2);
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

}
