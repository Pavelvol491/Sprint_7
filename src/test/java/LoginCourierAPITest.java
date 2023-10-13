import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoginCourierAPITest {

    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";
    private static final String COURIER_LOGIN_ENDPOINT = "/api/v1/courier/login";

    private Faker faker = new Faker();
    private ObjectMapper objectMapper = new ObjectMapper();
    private String createdTestData; // Переменная для хранения данных, созданных для теста

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Before
    public void createTestData() {
        // Код для создания тестовых данных
        String login = "ninja";
        String password = "1234";
        LoginRequest loginRequest = new LoginRequest(login, password);
        try {
            createdTestData = objectMapper.writeValueAsString(loginRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize the LoginRequest to JSON", e);
        }
    }

    @After
    public void deleteTestData() {
        // Код для удаления тестовых данных
        createdTestData = null;
    }

    @Test
    @DisplayName("1. Тест логина курьера с правильными данными")
    @Description("Тест проверяет успешный логин курьера с правильными данными")
    public void testLoginCourierWithValidData() {
        if (createdTestData == null) {
            throw new RuntimeException("Test data is missing.");
        }

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(createdTestData)
                .post(COURIER_LOGIN_ENDPOINT);

        // Проверки
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().jsonPath().getInt("id") > 0);
    }

    @Test
    @DisplayName("2. Тест логина курьера с отсутствующими данными")
    @Description("Тест проверяет возврат ошибки при логине без логина или пароля")
    public void testLoginCourierWithMissingData() {
        if (createdTestData == null) {
            throw new RuntimeException("Test data is missing.");
        }

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{}")
                .post(COURIER_LOGIN_ENDPOINT);

        // Проверки
        assertEquals(400, response.getStatusCode());
        assertEquals("Недостаточно данных для входа", response.getBody().jsonPath().getString("message"));
    }


    @Test
    @DisplayName("3. Тест логина курьера с неправильными учетными данными")
    @Description("Тест проверяет возврат ошибки при логине с неправильным логином или паролем")
    public void testLoginCourierWithIncorrectCredentials() {
        // Логин курьера с неправильным логином и/или паролем
        String login = "invalid_login";
        String password = "invalid_password";

        LoginRequest loginRequest = new LoginRequest(login, password);
        String jsonRequest;
        try {
            jsonRequest = objectMapper.writeValueAsString(loginRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize the LoginRequest to JSON", e);
        }

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonRequest)
                .post(COURIER_LOGIN_ENDPOINT);

        // Проверки
        assertEquals(400, response.getStatusCode());
        assertEquals("Неправильный логин или пароль", response.getBody().jsonPath().getString("message"));
    }

    @Test
    @DisplayName("4. Тест логина курьера без указания логина")
    @Description("Тест проверяет возврат ошибки при логине без указания логина")
    public void testLoginCourierWithMissingLogin() {
        // Логин курьера без указания логина
        String password = "1234";

        LoginRequest loginRequest = new LoginRequest("", password);
        String jsonRequest;
        try {
            jsonRequest = objectMapper.writeValueAsString(loginRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize the LoginRequest to JSON", e);
        }

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonRequest)
                .post(COURIER_LOGIN_ENDPOINT);

        // Проверки
        assertEquals(400, response.getStatusCode());
        assertEquals("Недостаточно данных для входа", response.getBody().jsonPath().getString("message"));
    }

    @Test
    @DisplayName("5. Тест логина несуществующего курьера")
    @Description("Тест проверяет возврат ошибки при логине несуществующего курьера")
    public void testLoginNonExistentCourier() {
        // Логин несуществующего курьера
        String login = "non_existent_courier";
        String password = "1234";

        LoginRequest loginRequest = new LoginRequest(login, password);
        String jsonRequest;
        try {
            jsonRequest = objectMapper.writeValueAsString(loginRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize the LoginRequest to JSON", e);
        }

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonRequest)
                .post(COURIER_LOGIN_ENDPOINT);

        // Проверки
        assertEquals(400, response.getStatusCode());
        assertEquals("Пользователь с таким логином не существует", response.getBody().jsonPath().getString("message"));
    }
}
