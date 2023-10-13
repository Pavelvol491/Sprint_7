import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class OrdersAPITest {

    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru/api/v1/orders";

    @Test
    public void testGetOrderList() {
        // Получение списка заказов
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get(BASE_URL);

        // Проверки
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody().jsonPath().getList("orders"));
    }

    @Test
    public void testGetOrderListWithParameters() {
        // Получение списка заказов с параметрами
        int courierId = 1;
        String metroStation = "1";
        int limit = 10;
        int page = 0;

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("courierId", courierId)
                .queryParam("nearestStation", "{\"nearestStation\":[\"" + metroStation + "\"]}")
                .queryParam("limit", limit)
                .queryParam("page", page)
                .get(BASE_URL);

        // Проверки
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody().jsonPath().getList("orders"));
    }

    @Test
    public void testGetOrderListWithInvalidCourierId() {
        // Получение списка заказов с недействительным courierId
        int courierId = 999;

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("courierId", courierId)
                .get(BASE_URL);

        // Проверки
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void testGetOrderByTrack() {
        // Получение заказа по номеру трека
        int trackNumber = 123456;

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get(BASE_URL + "/track?t=" + trackNumber);

        // Проверки
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody().jsonPath().get("order"));
    }

    @Test
    public void testGetOrderByInvalidTrack() {
        // Получение заказа по недействительному номеру трека
        int trackNumber = 999999;

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get(BASE_URL + "/track?t=" + trackNumber);

        // Проверки
        assertEquals(400, response.getStatusCode());
        assertFalse(response.getBody().jsonPath().getBoolean("success"));
    }

    @Test
    public void testGetOrderWithoutTrack() {
        // Получение заказа без указания номера трека
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get(BASE_URL + "/track");

        // Проверки
        assertEquals(400, response.getStatusCode());
    }
}
