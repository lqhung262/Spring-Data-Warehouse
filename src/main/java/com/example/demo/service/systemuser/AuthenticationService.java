package com.example.demo.service.systemuser;

import com.example.demo.config.KeycloakProvider;
import com.example.demo.dto.systemuser.Authentication.AuthenticationRequest;
import com.example.demo.dto.systemuser.Authentication.AuthenticationResponse;
import com.example.demo.dto.systemuser.Introspect.IntrospectRequest;
import com.example.demo.dto.systemuser.Introspect.IntrospectResponse;
import com.example.demo.repository.systemuser.UserRepository;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    KeycloakProvider keycloakProvider;
    UserRepository userRepository;

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

            // If token obtained, check local user enabled status (prevent login if local user exists and is disabled)
            if (accessToken != null) {
                try {
                    var realm = keycloakProvider.getInstance().realm(keycloakProvider.getRealm());
                    List<UserRepresentation> users = realm.users().search(request.getUsername(), 0, 1);
                    if (users != null && !users.isEmpty()) {
                        String kcId = users.getFirst().getId();
                        var local = userRepository.findByAuthorizationServiceUserId(kcId);
                        if (local.isPresent() && local.get().getIsEnabled() != null && !local.get().getIsEnabled()) {
                            // user disabled locally -> reject login
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is disabled");
                        }
                    }
                } catch (ResponseStatusException rse) {
                    // rethrow permission/disabled exceptions
                    throw rse;
                } catch (Exception ignored) {
                    // if any error during admin lookup, allow login (fail-open) or change behavior as needed
                }
            }

            // If Keycloak did not return an access token, treat as invalid credentials
            if (accessToken == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }

            return AuthenticationResponse.builder()
                    .token(accessToken)
                    .authenticated(true)
                    .build();
        } catch (UnirestException e) {
            // network/Keycloak error - map to 502 Bad Gateway
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Authentication provider error", e);
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
