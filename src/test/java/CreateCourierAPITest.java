import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CreateCourierAPITest {

    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";
    private static final String COURIER_LOGIN_ENDPOINT = "/api/v1/courier/login";
    private static final String COURIER_CREATE_ENDPOINT = "/api/v1/courier";

    private static Faker faker = new Faker();
    private Gson gson = new Gson();
    private Response loginResponse;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"ninja", "1234"}, // Правильные данные для логина
                {"", ""}, // Отсутствуют данные для логина
                {faker.name().username(), faker.internet().password()} // Данные для создания курьера
        });
    }

    private String login;
    private String password;

    public CreateCourierAPITest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Before
    public void createTestData() {
        // Код для создания тестовых данных
        if (login.isEmpty() || password.isEmpty()) {
            loginResponse = null; // В случае отсутствия данных, не выполняем логин
        } else {
            loginResponse = loginCourier(new Courier(login, password));
        }
    }

    @After
    public void deleteTestData() {

    }

    @Test
    @DisplayName("Тест логина курьера")
    @Description("Тест логина курьера с разными данными")
    public void testLoginCourier() {
        if (loginResponse == null) {
            assertEquals(400, loginResponse.getStatusCode());
            assertEquals("Недостаточно данных для входа", loginResponse.getBody().jsonPath().getString("message"));
        } else {
            assertEquals(200, loginResponse.getStatusCode());
            assertTrue(loginResponse.getBody().jsonPath().getInt("id") > 0);
        }
    }

    @Test
    @DisplayName("Тест создания курьера")
    @Description("Тест создания курьера с разными данными")
    public void testCreateCourier() {
        Response response = createCourier(new Courier(login, password));

        if (login.isEmpty() || password.isEmpty()) {
            assertEquals(400, response.getStatusCode());
            assertEquals("Недостаточно данных для создания учетной записи", response.getBody().jsonPath().getString("message"));
        } else {
            assertEquals(201, response.getStatusCode());
            assertTrue(response.getBody().jsonPath().getBoolean("ok"));
        }
    }

    private Response loginCourier(Courier courier) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post(COURIER_LOGIN_ENDPOINT);
    }

    private Response createCourier(Courier courier) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post(COURIER_CREATE_ENDPOINT);
    }
}
