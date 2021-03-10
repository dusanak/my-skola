package com.skillsfighters.runnable.helper;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class HttpHelperAuth {
    private static final String FIREBASE_DEBUG_TOKEN = "skillsfighters";

    private static HttpHeaders createHeadersAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("firebaseToken", FIREBASE_DEBUG_TOKEN);
        return headers;
    }

    public static <T> ResponseEntity<T> createAuth(String urlAdd, String requestJson, Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(requestJson, createHeadersAuth());
        return restTemplate.exchange(urlAdd, HttpMethod.PUT, entity, clazz);
    }

    public static void deleteAuth(String urlDelete) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, createHeadersAuth());
        restTemplate.exchange(urlDelete, HttpMethod.DELETE, entity, Void.class);
    }

    public static <T> ResponseEntity<T> showAuth(String urlShow, Class<T> clazz) {
        RestTemplate rt = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, createHeadersAuth());
        return rt.exchange(urlShow, HttpMethod.GET, entity, clazz);
    }

    public static <T> ResponseEntity<T> updateAuth(String urlUpdate, String requestJson, Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(requestJson, createHeadersAuth());
        return restTemplate.postForEntity(urlUpdate, entity, clazz);
    }

    public static <T> ResponseEntity<T> updateToNowAuth(String urlUpdateToNow, Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, createHeadersAuth());
        return restTemplate.postForEntity(urlUpdateToNow, entity, clazz);
    }
}
