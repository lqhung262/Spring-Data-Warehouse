package com.example.demo.service.systemuser;

import com.example.demo.config.KeycloakProvider;
import com.example.demo.dto.systemuser.User.UserRequest;
import com.example.demo.dto.systemuser.User.UserResponse;
import com.example.demo.entity.systemuser.User;
import com.example.demo.mapper.systemuser.UserMapper;
import com.example.demo.repository.systemuser.UserRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakProvider keycloakProvider;

    public UserResponse createUser(UserRequest request, String defaultRealmRole) {
        // 1. create Keycloak user representation
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setEnabled(true);
        kcUser.setUsername(request.getUserName());
        kcUser.setFirstName(request.getFirstName());
        kcUser.setLastName(request.getLastName());
        kcUser.setEmail(request.getEmailAddress());

        var realm = keycloakProvider.getInstance().realm(keycloakProvider.getRealm());
        var usersResource = realm.users();
        Response resp = usersResource.create(kcUser);
        String keycloakId = CreatedResponseUtil.getCreatedId(resp); // ID thực từ Keycloak

        // 2. set password
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(request.getPassword());
        usersResource.get(keycloakId).resetPassword(cred);

        // 3. assign realm role (if provided)
        if (defaultRealmRole != null && !defaultRealmRole.isBlank()) {
            var roleRep = realm.roles().get(defaultRealmRole).toRepresentation();
            usersResource.get(keycloakId).roles().realmLevel().add(Collections.singletonList(roleRep));
        }

        // 4. persist to local DB
        User entity = userMapper.toUser(request);
        entity.setAuthorizationServiceUserId(keycloakId);
        entity.setFullName(request.getFirstName() + " " + request.getLastName());
        User saved = userRepository.save(entity);

        return userMapper.toUserResponse(saved);
    }
}
