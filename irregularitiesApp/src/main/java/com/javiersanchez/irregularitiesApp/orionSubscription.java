package com.javiersanchez.irregularitiesApp;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class orionSubscription {

    private static final String ORION_SUBSCRIPTION_URL = "http://localhost:1026/v2/subscriptions";

    @Autowired
    private RestTemplate restTemplate;

    public void createSubscription() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{" +
                "\"description\": \"Subscription for receiving notifications when device is near an irregularity zone\","
                +
                "\"subject\": {" +
                "\"entities\": [{" +
                "\"idPattern\": \".*\"," +
                "\"type\": \"UnidadMantenimiento\"" +
                "}]," +
                "\"condition\": {" +
                "\"attrs\": []" +
                "}" +
                "}," +
                "\"notification\": {" +
                "\"http\": {" +
                "\"url\": \"http://host.docker.internal:1029/notification\"" +
                "}," +
                "\"attrs\": [\"zonas\"]" +
                "}" +
                "}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        restTemplate.postForEntity(ORION_SUBSCRIPTION_URL, requestEntity, String.class);
    }

    public void deleteAllSubscriptions() {
        try {
            // Obtener todas las suscripciones existentes
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:1026/v2/subscriptions",
                    String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONArray subscriptions = new JSONArray(response.getBody());

                // Eliminar cada suscripción existente
                for (int i = 0; i < subscriptions.length(); i++) {
                    JSONObject subscription = subscriptions.getJSONObject(i);
                    String subscriptionId = subscription.getString("id");
                    restTemplate.delete("http://localhost:1026/v2/subscriptions" + "/" + subscriptionId);
                }
            }
        } catch (Exception e) {
            // Manejar errores
        }
    }
}
