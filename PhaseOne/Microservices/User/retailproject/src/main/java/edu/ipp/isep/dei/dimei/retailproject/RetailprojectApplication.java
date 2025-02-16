package edu.ipp.isep.dei.dimei.retailproject;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Address;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.repositories.AccountRepository;
import edu.ipp.isep.dei.dimei.retailproject.repositories.AddressRepository;
import edu.ipp.isep.dei.dimei.retailproject.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "edu.ipp.isep.dei.dimei.retailproject.repositories")
public class RetailprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetailprojectApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(AccountRepository accountRepository, UserRepository userRepository, AddressRepository addressRepository) {
        return args -> {
            Account account1 = new Account("admin_email@gmail.com", "$2a$10$CoZ5c8.S3Iht/V3SRCOnP.dH.trp/rvmjtloGXlmDcdCDrNP51Qg2", RoleEnum.ADMIN);
            Account account2 = new Account("johndoe1234@gmail.com", "$2a$10$J7jrwtYh2UAoOjNgsrZTEOKCqnn3UdT5Prj7cL08bDLT3pJOvVdYe", RoleEnum.USER);
            Account account3 = new Account("merchant@gmail.com", "$2a$10$MdZ6GNLCdfkrhmHFdLHO0eXFn5z6Omd2rsZBTgINB44mfUim18w2u", RoleEnum.MERCHANT);
            ArrayList<Account> accountList = new ArrayList<>();
            accountList.add(account1);
            accountList.add(account2);
            accountList.add(account3);

            User user1 = new User(1, "Admin", "OfEverything", account1);
            User user2 = new User(2, "John", "Doe", account2);
            User user3 = new User(3, "Merchant", "Dummy", account3);
            ArrayList<User> userList = new ArrayList<>();
            userList.add(user1);
            userList.add(user2);
            userList.add(user3);


            for (int i = 1; i <= accountList.size(); i++) {
                Account account = accountRepository.findById(i).orElse(null);
                if (account == null) {
                    accountRepository.save(accountList.get(i));
                    accountList.get(i).setId(i);

                    User user = userRepository.findById(i).orElse(null);
                    if (user != null) {
                        userRepository.save(user);
                        userList.get(i).setId(i);
                        Address address;
                        switch (i) {
                            case 2:
                                address = addressRepository.findById(1).orElse(null);
                                if (address == null) {
                                    addressRepository.save(new Address(1, "Different Street", "1234", "Lisbon", "Portugal", userList.get(i)));
                                }
                                break;
                            case 3:
                                address = addressRepository.findById(2).orElse(null);
                                if (address == null) {
                                    addressRepository.save(new Address(2, "5th Avenue", "10128", "New York", "USA", userList.get(i)));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            addressRepository.save(new Address(1, "Different Street", "1234", "Lisbon", "Portugal", user2));
            addressRepository.save(new Address(2, "5th Avenue", "10128", "New York", "USA", user3));
        };
    }
}
