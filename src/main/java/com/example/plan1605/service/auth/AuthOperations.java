package com.example.plan1605.service.auth;

import com.example.plan1605.exceptions.InvalidRefreshTokenException;
import com.example.plan1605.model.auth.PlannerUserDetails;
import com.example.plan1605.model.auth.response.AccessTokenResponse;

public interface AuthOperations {

    AccessTokenResponse getToken(PlannerUserDetails userDetails);

    AccessTokenResponse refreshToken(String refreshToken)
            throws InvalidRefreshTokenException;

    void invalidateToken(String refreshToken, String ownerEmail) throws InvalidRefreshTokenException;
}
