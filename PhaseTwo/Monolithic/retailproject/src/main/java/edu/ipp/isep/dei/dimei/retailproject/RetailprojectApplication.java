package edu.ipp.isep.dei.dimei.retailproject;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
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
    public CommandLineRunner demoData(MerchantRepository merchantRepository) {
        return args -> {
            Merchant merchant = null;

            Merchant merchantCheck = merchantRepository.findById(1).orElse(null);
            if (merchantCheck == null) {
                merchant = new Merchant("Merchant Dummy", "merchant@gmail.com", 2);
                merchantRepository.save(merchant);
                merchant.setId(1);
            }
        };
    }

}
