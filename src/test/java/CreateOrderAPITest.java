import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class CreateOrderAPITest {

    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru/api/v1/orders";

    private String color1;
    private String color2;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"BLACK", null}, // Указан только первый цвет
                {null, "GREY"}, // Указан только второй цвет
                {"BLACK", "GREY"}, // Указаны оба цвета
                {null, null} // Не указаны цвета
        });
    }

    public CreateOrderAPITest(String color1, String color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    @Test
    public void testCreateOrder() {
        // Создание заказа
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{ \"firstName\": \"Naruto\", \"lastName\": \"Uchiha\", \"address\": \"Konoha, 142 apt.\", \"metroStation\": 4, \"phone\": \"+7 800 355 35 35\", \"rentTime\": 5, \"deliveryDate\": \"2020-06-06\", \"comment\": \"Saske, come back to Konoha\", \"color\": [" + (color1 != null ? "\"" + color1 + "\"" : "") + (color1 != null && color2 != null ? ", " : "") + (color2 != null ? "\"" + color2 + "\"" : "") + "] }")
                .post(BASE_URL);

        // Проверки
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getBody().jsonPath().getInt("track"));
    }
}
