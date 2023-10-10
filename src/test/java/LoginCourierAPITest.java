import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoginCourierAPITest {

    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";

    @Test
    public void testLoginCourier() {
        // Логин курьера
        String login = "ninja";
        String password = "1234";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }")
                .post(BASE_URL + "/api/v1/courier/login");

        // Проверки
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().jsonPath().getInt("id") > 0);
    }

    @Test
    public void testLoginCourierWithMissingData() {
        // Логин курьера без логина или пароля
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{}")
                .post(BASE_URL + "/api/v1/courier/login");

        // Проверки
        assertEquals(400, response.getStatusCode());
        assertEquals("Недостаточно данных для входа", response.getBody().jsonPath().getString("message"));
    }

    @Test
    public void testLoginCourierWithIncorrectCredentials() {
        // Логин курьера с неправильным логином и/или паролем
        String login = "invalid_login";
        String password = "invalid_password";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }")
                .post(BASE_URL + "/api/v1/courier/login");

        // Проверки
        assertEquals(400, response.getStatusCode());
        assertEquals("Неправильный логин или пароль", response.getBody().jsonPath().getString("message"));
    }

    @Test
    public void testLoginCourierWithMissingField() {
        // Логин курьера без указания логина
        String password = "1234";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{ \"password\": \"" + password + "\" }")
                .post(BASE_URL + "/api/v1/courier/login");

        // Проверки
        assertEquals(400, response.getStatusCode());
        assertEquals("Недостаточно данных для входа", response.getBody().jsonPath().getString("message"));
    }

    @Test
    public void testLoginNonExistentCourier() {
        // Логин несуществующего курьера
        String login = "non_existent_courier";
        String password = "1234";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }")
                .post(BASE_URL + "/api/v1/courier/login");

        // Проверки
        assertEquals(400, response.getStatusCode());
        assertEquals("Пользователь с таким логином не существует", response.getBody().jsonPath().getString("message"));
    }
}
