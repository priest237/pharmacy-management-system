package com.pharmacy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class RecaptchaService {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final RestClient restClient;
    private final String secretKey;

    public RecaptchaService(@Value("${recaptcha.secret-key}") String secretKey) {
        this.restClient = RestClient.builder().baseUrl(VERIFY_URL).build();
        this.secretKey = secretKey;
    }

    public boolean isValid(String responseToken, String remoteIp) {
        if (responseToken == null || responseToken.isBlank()) {
            return false;
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secretKey);
        form.add("response", responseToken);
        if (remoteIp != null && !remoteIp.isBlank()) {
            form.add("remoteip", remoteIp);
        }

        RecaptchaVerificationResponse response = restClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(RecaptchaVerificationResponse.class);

        return response != null && Boolean.TRUE.equals(response.success());
    }

    public record RecaptchaVerificationResponse(Boolean success, String challenge_ts,
                                                String hostname, List<String> errorCodes) {
    }
}
