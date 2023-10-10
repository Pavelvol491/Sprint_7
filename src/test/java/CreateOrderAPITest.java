import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreateOrderAPITest {

    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru/api/v1/orders";

    @Test
    @DisplayName("Тест создания заказа")
    @Description("Тест проверяет создание заказа с указанными цветами")
    public void testCreateOrder() {
        createOrder();
    }

    @Step("Создание заказа с цветами: {firstColor}, {secondColor}")
    private void createOrder() {

        Map<String, Object> orderRequest = OrderRequestBuilder.createOrderRequest(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                new String[]{"BLACK", "GREY"}
        );


        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(orderRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(BASE_URL);


        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getBody().jsonPath().getInt("track"));
    }
}
