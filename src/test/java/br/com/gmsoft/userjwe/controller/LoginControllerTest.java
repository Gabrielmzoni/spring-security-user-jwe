package br.com.gmsoft.userjwe.controller;

import br.com.gmsoft.userjwe.BaseTests;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class LoginControllerTest extends BaseTests {

    @Autowired
    private MockMvc mockMvc;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String BASE_URL_LOGIN = ApiVersion.V1 + "/user/login";

    private static Map<String, JsonObject> payloads;

    private static final Gson gson = new Gson();

    private static final String shouldLogIn_withSuccess = "Deve logar com sucesso";
    private static final String shouldLogIn_withErrorInvalidPassword = "Deve retornar erro de senha inválida";
    private static final String shouldLogIn_withErrorInvalidUser = "Deve retornar erro de usuário inválido";

    private void performPostRequest(String content, ResultMatcher... matchers) {
        try {
            mockMvc.perform(MockMvcRequestBuilders.post(LoginControllerTest.BASE_URL_LOGIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpectAll(matchers);
        } catch (Exception e) {
            logger.error("Error performing POST request", e);
        }
    }

    @BeforeAll
    static void loadPayloads() throws IOException, FileNotFoundException {

        FileReader reader = new FileReader("src/test/resources/test-payloads.json");
        payloads = gson.fromJson(reader, new TypeToken<Map<String, JsonObject>>() {
        }.getType());
        reader.close();
    }


    @Test
    @DisplayName(shouldLogIn_withSuccess)
    void shouldLogIn_withSuccess() {

        performPostRequest(payloads.get("VALID_LOGIN_2").toString(),
                status().isOk(),
                header().exists("Authorization"));


    }

    @Test
    @DisplayName(shouldLogIn_withErrorInvalidPassword)
    void shouldLogIn_withErrorInvalidPassword() {

        performPostRequest(payloads.get("INVALID_LOGIN_2_WRONG_PASS").toString(),
                status().isUnauthorized(),
                header().doesNotExist("Authorization"));


    }

    @Test
    @DisplayName(shouldLogIn_withErrorInvalidUser)
    void shouldLogIn_withErrorInvalidUser() {

        performPostRequest(payloads.get("INVALID_LOGIN_2_WRONG_USER").toString(),
                status().isUnauthorized(),
                header().doesNotExist("Authorization"));

    }

}
