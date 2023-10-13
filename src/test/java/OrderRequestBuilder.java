import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class OrderRequestBuilder {
    public static Map<String, Object> createOrderRequest(String firstName, String lastName, String address,
                                                         int metroStation, String phone, int rentTime,
                                                         String deliveryDate, String comment, String[] colors) {
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("firstName", firstName);
        orderRequest.put("lastName", lastName);
        orderRequest.put("address", address);
        orderRequest.put("metroStation", metroStation);
        orderRequest.put("phone", phone);
        orderRequest.put("rentTime", rentTime);
        orderRequest.put("deliveryDate", deliveryDate);
        orderRequest.put("comment", comment);
        orderRequest.put("color", colors);

        return orderRequest;
    }
}
