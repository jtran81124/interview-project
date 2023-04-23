package com.shepherdmoney.interviewproject;


import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {

        return args -> {
//            log.info("Deleting All Existing Data...");
//            repository.deleteAll();
//            log.info("Preloading " + repository.save(new User("Bilbo Baggins", "burglar@email.com")));
//            log.info("Preloading " + repository.save(new User("Frodo Baggins", "thief@email.com")));
        };
    }
}
