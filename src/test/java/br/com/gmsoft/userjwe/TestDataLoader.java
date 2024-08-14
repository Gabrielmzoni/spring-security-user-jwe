package br.com.gmsoft.userjwe;

import br.com.gmsoft.userjwe.domain.User;
import br.com.gmsoft.userjwe.dto.UserDto;
import br.com.gmsoft.userjwe.repository.UserRepository;
import br.com.gmsoft.userjwe.service.UserServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Component
public class TestDataLoader {

    private final UserServiceImpl userService;

    private final UserRepository userRepository;

    @Value("${userjwe.userdatatest}")
    public String userDataFileName;

    Logger logger = LoggerFactory.getLogger(TestDataLoader.class);

    @Autowired
    public TestDataLoader(UserServiceImpl userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @PostConstruct
    public void loadData() throws IOException {

        Reader readerUser = new InputStreamReader(new ClassPathResource(userDataFileName).getInputStream(), StandardCharsets.UTF_8);

        Gson gson = new Gson();

        List<UserDto> users = gson.fromJson(readerUser, new TypeToken<List<UserDto>>(){}.getType());

        for (UserDto user : users) {
            userService.registerUser(user);
        }

        for (User user : userRepository.findAll()) {
            logger.info("Name: {}, e-mail: {}, password: {}", user.getName(), user.getEmail(), user.getPassword());

        }
    }
}