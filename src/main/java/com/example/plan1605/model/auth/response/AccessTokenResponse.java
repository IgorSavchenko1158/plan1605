package com.example.plan1605.model.auth.response;

public record AccessTokenResponse(String accessToken, String refreshToken, long expireIn) {

}
