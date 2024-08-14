package br.com.gmsoft.userjwe;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = UserJweApplication.class,properties = "spring.profiles.active:test")
@AutoConfigureMockMvc
public abstract class BaseTests {

}
