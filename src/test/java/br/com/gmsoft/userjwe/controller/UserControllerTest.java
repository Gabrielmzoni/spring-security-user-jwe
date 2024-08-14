package br.com.gmsoft.userjwe.controller;

import br.com.gmsoft.userjwe.BaseTests;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class UserControllerTest extends BaseTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${userjwe.testpayload}")
    private String testPayloadFile;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String BASE_URL_USER = ApiVersion.V1 + "/user";

    private static final String BASE_URL_LOGIN = ApiVersion.V1 + "/user/login";

    private static final String shouldRegisterANewUser_withSuccess = "Deve cadastrar um novo usuário com sucesso";
    private static final String shouldRegisterANewUser_withEmailAlreadyExistError = "Deve ocorrer erro ao cadastrar um usuário com e-mail já cadastrado";
    private static final String shouldRegisterANewUser_withAllFieldsWithError = "Deve ocorrer erro ao cadastrar um usuário com todos os campos inválidos";
    private static final String shouldRegisterANewUser_withPhoneAlreadyExistError = "Deve ocorrer erro ao cadastrar um usuário com telefone já cadastrado";
    private static final String shouldUpdateUserData_withSuccess = "Deve atualizar os dados do usuário com sucesso";
    private static final String shouldUpdateUserData_withErrorAllFields = "Deve atualizar os dados do usuário com erro em todos os campos";
    private static final String shouldReturnForgotPasswordToken_withSuccess = "Deve retornar o token para recuperação de senha com sucesso";
    private static final String shouldReturnDeleteAccountToken_withSuccess = "Deve retornar o token para exclusão de conta com sucesso";
    private static final String shouldReturnForgotPasswordToken_emailNotFound = "Deve ocorrer erro ao tentar recuperar senha de um e-mail não cadastrado";
    private static final String shouldGetForgotPasswordTokenAndResetPassword_withSuccess = "Deve retornar o token para recuperação de senha e reseta a senha com sucesso";
    private static final String shouldDeleteAccountAndConfirm_withSuccess = "Deve deletar a conta e confirmar a exclusão com sucesso";
    private static final String shouldDeleteAccountAndConfirm_wrongPassword = "Deve ocorrer erro ao deletar a conta e confirmar a exclusão com senha errada";
    private static final String shouldDeleteAccountAndConfirm_invalidToken = "Deve ocorrer erro ao deletar a conta e confirma a exclusão com token inválido";
    private static final String shouldDeleteAccountAndConfirm_emailNotFound = "Deve ocorrer erro ao deletar a conta e confirma a exclusão com e-mail não cadastrado";
    private static final String shouldDeleteAccountAndConfirm_invalidPassword = "Deve ocorrer erro ao deletar a conta e confirma a exclusão com senha inválida";

    private static Map<String, JsonObject> payloads;

    private static final Gson gson = new Gson();

    @PostConstruct
    void loadPayloads() throws IOException, FileNotFoundException {

        Reader readerPayloads = new InputStreamReader(new ClassPathResource(testPayloadFile).getInputStream(), StandardCharsets.UTF_8);

        payloads = gson.fromJson(readerPayloads, new TypeToken<Map<String, JsonObject>>() {
        }.getType());
        readerPayloads.close();
    }


    private Optional<MvcResult> performPostRequest(String url, String content, ResultMatcher... matchers) {
        try {
            return Optional.of(mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpectAll(matchers)
                    .andReturn());
        } catch (Exception e) {
            logger.error("Error performing POST request and getting content", e);
            return Optional.empty();
        }
    }

    private Optional<MvcResult> performPutRequestWithAuthorizationToken(String url, String token,String content, ResultMatcher... matchers) {
        try {
            return Optional.of(mockMvc.perform(MockMvcRequestBuilders.put(url)
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpectAll(matchers)
                    .andReturn());
        } catch (Exception e) {
            logger.error("Error performing PUT request", e);
            return Optional.empty();
        }
    }

    private Optional<MvcResult> performPostWithAuthorizationToken(String url, String token, String content,ResultMatcher... matchers) {
        try {
            return Optional.of(mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpectAll(matchers)
                    .andReturn());
        } catch (Exception e) {
            logger.error("Error performing POST request", e);
            return Optional.empty();
        }
    }

    private Optional<MvcResult> performPostWithAuthorizationToken(String url, String token, ResultMatcher... matchers) {
        try {
            return Optional.of(mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(matchers)
                    .andReturn());
        } catch (Exception e) {
            logger.error("Error performing POST request", e);
            return Optional.empty();
        }
    }


    @Test
    @DisplayName(shouldRegisterANewUser_withSuccess)
    void shouldRegisterANewUser_withSuccess() {

        performPostRequest(BASE_URL_USER, payloads.get("VALID_USER_TO_REGISTER").toString(), status().isCreated());

    }

    @Test
    @DisplayName(shouldRegisterANewUser_withEmailAlreadyExistError)
    void shouldRegisterANewUser_userWithEmailAlreadyExistError() {

        performPostRequest(BASE_URL_USER, payloads.get("VALID_USER_TO_REGISTER_2").toString(),
                status().isCreated());

        var email = gson.fromJson(payloads.get("VALID_USER_TO_REGISTER_2"), JsonObject.class).get("email").getAsString();

        performPostRequest(BASE_URL_USER, payloads.get("VALID_USER_TO_REGISTER_2").toString(),
                status().isConflict(),
                jsonPath("$.error").value(containsString(email)));


    }

    @Test
    @DisplayName(shouldRegisterANewUser_withAllFieldsWithError)
    void shouldRegisterANewUser_withAllFieldsWithError() {

        performPostRequest(BASE_URL_USER, payloads.get("INVALID_USER_FIELDS").toString(),
                status().isBadRequest(),
                jsonPath("$.name").exists(),
                jsonPath("$.email").exists(),
                jsonPath("$.password").exists(),
                jsonPath("$.phone").exists(),
                jsonPath("$.secretPhrase").exists(),
                jsonPath("$.secretAnswer").exists());

    }

    @Test
    @DisplayName(shouldRegisterANewUser_withPhoneAlreadyExistError)
    void shouldRegisterANewUser_userWithPhoneAlreadyExistError() {

        performPostRequest(BASE_URL_USER, payloads.get("VALID_USER_3").toString(),
                status().isCreated());

        var phone = gson.fromJson(payloads.get("VALID_USER_3"), JsonObject.class).get("phone").getAsString();

        performPostRequest(BASE_URL_USER, payloads.get("VALID_USER_3_CHANGED_EMAIL").toString(),
                status().isConflict(),
                jsonPath("$.error").value(containsString(phone)));

    }

    @Test
    @DisplayName(shouldUpdateUserData_withSuccess)
    void shouldUpdateUserData_WithSuccess() {

        Optional<MvcResult> result = performPostRequest(BASE_URL_LOGIN, payloads.get("VALID_LOGIN").toString(),
                status().isOk(), header().exists("Authorization"));

        if(result.isPresent()) {
            String token = result.get().getResponse().getHeader("Authorization");

            performPutRequestWithAuthorizationToken(BASE_URL_USER, token,payloads.get("VALID_USER_DATA_TO_UPDATE").toString(), status().isOk());
        }


    }

    @Test
    @DisplayName(shouldUpdateUserData_withErrorAllFields)
    void shouldUpdateUserData_withErrorAllFields() {

        Optional<MvcResult> result = performPostRequest(BASE_URL_LOGIN, payloads.get("VALID_LOGIN_5").toString(),
                status().isOk(), header().exists("Authorization"));

        if(result.isPresent()) {
            String token = result.get().getResponse().getHeader("Authorization");

            performPutRequestWithAuthorizationToken(BASE_URL_USER, token, payloads.get("INVALID_USER_DATA_TO_UPDATE").toString(),
                    status().isBadRequest());
        }

    }

    @Test
    @DisplayName(shouldReturnForgotPasswordToken_withSuccess)
    void shouldReturnForgotPasswordToken() {

        performPostRequest(BASE_URL_USER + "/forgot-password", payloads.get("VALID_EMAIL_1").toString(),
                status().isCreated(),
                jsonPath("$.token").exists());

    }


    @Test
    @DisplayName(shouldReturnDeleteAccountToken_withSuccess)
    void shouldReturnDeleteAccountToken() throws Exception {

        Optional<MvcResult> resultlogin = performPostRequest(BASE_URL_LOGIN, payloads.get("VALID_LOGIN_5").toString(),
                status().isOk(), header().exists("Authorization"));

        if(resultlogin.isPresent()) {
            String token = resultlogin.get().getResponse().getHeader("Authorization");

            performPostWithAuthorizationToken(BASE_URL_USER + "/delete-account", token,
                    status().isCreated(),
                    jsonPath("$.token").exists());
        }

    }

    @Test
    @DisplayName(shouldReturnForgotPasswordToken_emailNotFound)
    void shouldReturnForgotPasswordToken_EmailNotFound() {

        performPostRequest(BASE_URL_USER + "/forgot-password", payloads.get("INVALID_EMAIL").toString(),
                status().isNotFound(),
                jsonPath("$.error").exists());

    }

    @Test
    @DisplayName(shouldGetForgotPasswordTokenAndResetPassword_withSuccess)
    void shouldGetForgotPasswordTokenAndResetPassword() {

        Optional<MvcResult> result = performPostRequest(BASE_URL_USER + "/forgot-password", payloads.get("VALID_EMAIL_1").toString(),
                status().isCreated(),
                jsonPath("$.token").exists());

        if(result.isPresent()) {

            String token = null;
            try {
                token = gson.fromJson(result.get().getResponse().getContentAsString(), JsonObject.class).get("token").getAsString();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            JsonObject jsonObject = payloads.get("CHANGE_PASSWORD");
            jsonObject.addProperty("otp", token.replace("\"", ""));

            performPostRequest(BASE_URL_USER + "/reset-password", jsonObject.toString(), status().isOk());

        }

    }

    @Test()
    @DisplayName(shouldDeleteAccountAndConfirm_withSuccess)
    void shouldDeleteAccountAndConfirm() {

        Optional<MvcResult> resultlogin = performPostRequest(BASE_URL_LOGIN, payloads.get("VALID_LOGIN_3").toString(),
                status().isOk(), header().exists("Authorization"));

        if(resultlogin.isPresent()) {
            String token = resultlogin.get().getResponse().getHeader("Authorization");

            Optional<MvcResult> result = performPostWithAuthorizationToken(BASE_URL_USER + "/delete-account", token,
                    status().isCreated(),
                    jsonPath("$.token").exists());


            JsonObject jsonObject = payloads.get("DELETE_ACCOUNT_SUCCESS");

            try {
                jsonObject.add("otp", gson.fromJson(result.get().getResponse().getContentAsString(), JsonObject.class).get("token"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            performPostWithAuthorizationToken(BASE_URL_USER + "/confirm-delete", token ,jsonObject.toString(),
                    status().isOk());

        }


    }


    @Test
    @DisplayName(shouldDeleteAccountAndConfirm_invalidToken)
    void shouldDeleteAccountAndConfirm_InvalidToken() {

        Optional<MvcResult> resultlogin = performPostRequest(BASE_URL_LOGIN, payloads.get("VALID_LOGIN_5").toString(),
                status().isOk(), header().exists("Authorization"));


        if(resultlogin.isPresent()) {
            String token = resultlogin.get().getResponse().getHeader("Authorization");


            performPostWithAuthorizationToken(BASE_URL_USER + "/confirm-delete", token ,payloads.get("INVALID_TOKEN").toString(),
                    status().isNotFound());
        }


    }

    @Test
    @DisplayName(shouldDeleteAccountAndConfirm_wrongPassword)
    void setShouldDeleteAccountAndConfirm_wrongPassword() {
        Optional<MvcResult> resultlogin = performPostRequest(BASE_URL_LOGIN, payloads.get("VALID_LOGIN_5").toString(),
                status().isOk(), header().exists("Authorization"));

        if(resultlogin.isPresent()) {
            String token = resultlogin.get().getResponse().getHeader("Authorization");

            Optional<MvcResult> result = performPostWithAuthorizationToken(BASE_URL_USER + "/delete-account", token,
                    status().isCreated(),
                    jsonPath("$.token").exists());


            JsonObject jsonObject = payloads.get("DELETE_ACCOUNT_WRONG_PASS");

            try {
                jsonObject.add("otp", gson.fromJson(result.get().getResponse().getContentAsString(), JsonObject.class).get("token"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            performPostWithAuthorizationToken(BASE_URL_USER + "/confirm-delete", token ,jsonObject.toString(),
                    status().isUnauthorized());

        }

    }

}
