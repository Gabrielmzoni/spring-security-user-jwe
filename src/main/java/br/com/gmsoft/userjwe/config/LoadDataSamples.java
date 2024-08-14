package br.com.gmsoft.userjwe.config;

import br.com.gmsoft.userjwe.domain.User;
import br.com.gmsoft.userjwe.repository.UserRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration()
@ConditionalOnExpression("${userjwe.enablefakedata} == true")
public class LoadDataSamples {

    Logger logger = LoggerFactory.getLogger(this.getClass().toString().substring(0, this.getClass().toString().indexOf("$$")));


    @Bean()
    CommandLineRunner initDatabase(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        return args -> {
            var faker = new Faker();
            logger.info("creating fake data...");
            for (int i = 0; i < 10; i++) {
                var name = faker.name().fullName();
                var email = faker.internet().emailAddress();
                var phone = faker.phoneNumber().subscriberNumber(10);
                var secretPhrase = "What is your favorite color?";
                var secretAnswer = faker.color().name();
                var password = "12345678";
                var user = new User(null, name, email,passwordEncoder.encode(password), phone, secretPhrase, secretAnswer);
                user = userRepository.save(user);

                logger.info("User {} created | e-mail {} | password {}",  name, email, password);


                userRepository.save(user);
            }
            logger.info("fake data created!");
        };
    }
}