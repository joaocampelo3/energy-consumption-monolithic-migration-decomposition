package edu.ipp.isep.dei.dimei.retailproject;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.repositories.*;
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
    public CommandLineRunner demoData(AccountRepository accountRepository, UserRepository userRepository, AddressRepository addressRepository, CategoryRepository categoryRepository, MerchantRepository merchantRepository, ItemRepository itemRepository) {
        return args -> {
            Account account1 = new Account("admin_email@gmail.com", "$2a$10$CoZ5c8.S3Iht/V3SRCOnP.dH.trp/rvmjtloGXlmDcdCDrNP51Qg2", RoleEnum.ADMIN);
            Account account2 = new Account("johndoe1234@gmail.com", "$2a$10$J7jrwtYh2UAoOjNgsrZTEOKCqnn3UdT5Prj7cL08bDLT3pJOvVdYe", RoleEnum.USER);
            Account account3 = new Account("merchant@gmail.com", "$2a$10$MdZ6GNLCdfkrhmHFdLHO0eXFn5z6Omd2rsZBTgINB44mfUim18w2u", RoleEnum.MERCHANT);

            accountRepository.save(account1);
            account1.setId(1);
            accountRepository.save(account2);
            account2.setId(2);
            accountRepository.save(account3);
            account3.setId(3);

            User user1 = new User(1, "Admin", "OfEverything", account1);
            User user2 = new User(2, "John", "Doe", account2);
            User user3 = new User(3, "Merchant", "Dummy", account3);

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);

            Address merchantAddress = new Address(2, "5th Avenue", "10128", "New York", "USA", user3);

            addressRepository.save(new Address(1, "Different Street", "1234", "Lisbon", "Portugal", user2));
            addressRepository.save(merchantAddress);

            Category category1 = new Category("Category 1 description", "Category 1");
            Category category2 = new Category("Category 2 description", "Category 2");

            categoryRepository.save(category1);
            categoryRepository.save(category2);

            Merchant merchant = new Merchant("Merchant Dummy", "merchant@gmail.com", merchantAddress);
            merchantRepository.save(merchant);

            itemRepository.save(new Item("Item 1", "ABC-12345-S-BL", "Item 1 description", 8, 20000, category1, merchant));
            itemRepository.save(new Item("Item 2", "ABC-12345-XL-BL", "Item 2 description", 5, 30000, category1, merchant));
        };
    }
}
