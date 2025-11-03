package com.example.demo.service.systemuser;

import com.example.demo.config.KeycloakProvider;
import com.example.demo.dto.systemuser.Authentication.AuthenticationRequest;
import com.example.demo.dto.systemuser.Authentication.AuthenticationResponse;
import com.example.demo.dto.systemuser.Introspect.IntrospectRequest;
import com.example.demo.dto.systemuser.Introspect.IntrospectResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    KeycloakProvider keycloakProvider;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            String url = keycloakProvider.getServerURL() + "/realms/" + keycloakProvider.getRealm() + "/protocol/openid-connect/token";

            JsonNode resp = Unirest.post(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("client_id", keycloakProvider.getClientID())
                    .field("client_secret", keycloakProvider.getClientSecret())
                    .field("username", request.getUsername())
                    .field("password", request.getPassword())
                    .field("grant_type", "password")
                    .asJson().getBody();

            String accessToken = resp.getObject().has("access_token") ? resp.getObject().getString("access_token") : null;

            return AuthenticationResponse.builder()
                    .token(accessToken)
                    .authenticated(accessToken != null)
                    .build();
        } catch (UnirestException e) {
            return AuthenticationResponse.builder()
                    .token(null)
                    .authenticated(false)
                    .build();
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            String url = keycloakProvider.getServerURL() + "/realms/" + keycloakProvider.getRealm() + "/protocol/openid-connect/token/introspect";

            JsonNode resp = Unirest.post(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("client_id", keycloakProvider.getClientID())
                    .field("client_secret", keycloakProvider.getClientSecret())
                    .field("token", request.getToken())
                    .asJson().getBody();

            boolean active = resp.getObject().has("active") && resp.getObject().getBoolean("active");

            return IntrospectResponse.builder()
                    .valid(active)
                    .build();
        } catch (UnirestException e) {
            return IntrospectResponse.builder().valid(false).build();
        }
    }
}
