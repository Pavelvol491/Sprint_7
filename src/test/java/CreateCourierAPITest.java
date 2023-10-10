import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class Courier {
    private String login;
    private String password;

    public Courier(String login, String password) {
        this.login = login;
        this.password = password;
    }
}

public class CreateCourierAPITest {

    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";
    private static final String COURIER_LOGIN_ENDPOINT = "/api/v1/courier/login";
    private static final String COURIER_CREATE_ENDPOINT = "/api/v1/courier";

    private Faker faker = new Faker();
    private Gson gson = new Gson();

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("1. Тест логина курьера с правильными данными")
    @Description("Тест проверяет успешный логин курьера с правильными данными")
    public void testLoginCourierWithValidData() {
        String login = "ninja";
        String password = "1234";

        Response response = loginCourier(new Courier(login, password));

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().jsonPath().getInt("id") > 0);
    }

    @Test
    @DisplayName("2. Тест логина курьера с отсутствующими данными")
    @Description("Тест проверяет возврат ошибки при логине без логина или пароля")
    public void testLoginCourierWithMissingData() {
        Response response = loginCourier(new Courier("", ""));

        assertEquals(400, response.getStatusCode());
        assertEquals("Недостаточно данных для входа", response.getBody().jsonPath().getString("message"));
    }

    @Test
    @DisplayName("3. Тест создания курьера с правильными данными")
    @Description("Тест проверяет успешное создание курьера с правильными данными")
    public void testCreateCourierWithValidData() {
        String login = faker.name().username();
        String password = faker.internet().password();

        Response response = createCourier(new Courier(login, password));

        assertEquals(201, response.getStatusCode());
        assertTrue(response.getBody().jsonPath().getBoolean("ok"));
    }

    @Test
    @DisplayName("4. Тест создания курьера с отсутствующими данными")
    @Description("Тест проверяет возврат ошибки при создании курьера без обязательных данных")
    public void testCreateCourierWithMissingData() {
        Response response = createCourier(new Courier("", ""));

        assertEquals(400, response.getStatusCode());
        assertEquals("Недостаточно данных для создания учетной записи", response.getBody().jsonPath().getString("message"));
    }

    @Test
    @DisplayName("5. Тест создания курьера с существующим логином")
    @Description("Тест проверяет возврат ошибки при создании курьера с существующим логином")
    public void testCreateDuplicateCourier() {
        String login = faker.name().username();
        String password = faker.internet().password();

        createCourier(new Courier(login, password)); // Создаем курьера с теми же данными

        Response response = createCourier(new Courier(login, password)); // Пытаемся создать курьера с теми же данными снова

        assertEquals(409, response.getStatusCode());
        assertEquals("Этот логин уже используется. Попробуйте другой.", response.getBody().jsonPath().getString("message"));
    }

    private Response loginCourier(Courier courier) {
        String requestBody = gson.toJson(courier);
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(COURIER_LOGIN_ENDPOINT);
    }

    private Response createCourier(Courier courier) {
        String requestBody = gson.toJson(courier);
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(COURIER_CREATE_ENDPOINT);
    }
}
