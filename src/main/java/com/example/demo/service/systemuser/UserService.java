package com.example.demo.service.systemuser;

import com.example.demo.config.KeycloakProvider;
import com.example.demo.dto.systemuser.Role.RoleResponse;
import com.example.demo.dto.systemuser.User.UserRequest;
import com.example.demo.dto.systemuser.User.UserResponse;
import com.example.demo.entity.systemuser.Role;
import com.example.demo.entity.systemuser.RolePermission;
import com.example.demo.entity.systemuser.User;
import com.example.demo.entity.systemuser.UserRole;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.systemuser.UserMapper;
import com.example.demo.repository.systemuser.RolePermissionRepository;
import com.example.demo.repository.systemuser.RoleRepository;
import com.example.demo.repository.systemuser.UserRepository;
import com.example.demo.repository.systemuser.UserRoleRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final KeycloakProvider keycloakProvider;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;

    @Value("${entities.systemuser.user}")
    private String entityName;

    @Transactional
    public UserResponse createUser(UserRequest request, String defaultRealmRole) {
        // check uniqueness of username and email in local DB before creating Keycloak user
        userRepository.findByUserName(request.getUserName()).ifPresent(u -> {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "username already exists");
        });
        userRepository.findByEmailAddress(request.getEmailAddress()).ifPresent(u -> {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "email address already exists");
        });

        // create Keycloak user and set password
        String keycloakId = createKeycloakUser(request);

        var realm = keycloakProvider.getInstance().realm(keycloakProvider.getRealm());
        var usersResource = realm.users();

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(request.getPassword());
        usersResource.get(keycloakId).resetPassword(cred);

        // assign realm roles
        assignRealmRoles(keycloakId, defaultRealmRole, request.getRoleIds());

        // persist to local DB
        User entity = userMapper.toUser(request);
        entity.setAuthorizationServiceUserId(keycloakId);
        entity.setFullName(request.getFirstName() + " " + request.getLastName());
        if (request.getIsEnabled() != null) entity.setIsEnabled(request.getIsEnabled());
        User saved = userRepository.save(entity);

        // persist user roles (local join table) if roleIds provided
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<UserRole> urs = request.getRoleIds().stream().map(rid -> {
                UserRole ur = new UserRole();
                ur.setUserId(saved);
                Role rr = new Role();
                rr.setRoleId(rid);
                ur.setRoleId(rr);
                return ur;
            }).collect(Collectors.toSet());
            userRoleRepository.saveAll(urs);
        }

        // build response with roles
        UserResponse respU = userMapper.toUserResponse(saved);
        respU.setRoles(getRolesForUser(saved));
        return respU;
    }

    private String createKeycloakUser(UserRequest request) {
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setEnabled(request.getIsEnabled() == null || request.getIsEnabled());
        kcUser.setUsername(request.getUserName());
        kcUser.setFirstName(request.getFirstName());
        kcUser.setLastName(request.getLastName());
        kcUser.setEmail(request.getEmailAddress());

        var realm = keycloakProvider.getInstance().realm(keycloakProvider.getRealm());
        var usersResource = realm.users();
        Response resp;
        try {
            resp = usersResource.create(kcUser);
        } catch (jakarta.ws.rs.WebApplicationException wae) {
            String body = "";
            try {
                if (wae.getResponse() != null) {
                    Object entity = wae.getResponse().getEntity();
                    body = entity == null ? "" : entity.toString();
                }
            } catch (Exception ignored) {
                // intentionally ignored when trying to read the error body
            }
            String msg = "Failed to create user in Keycloak: " + wae.getMessage() + " responseBody=" + body;
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg, wae);
        }

        return handleKeycloakCreateResponse(resp);
    }

    private String handleKeycloakCreateResponse(Response resp) {
        try (Response r = resp) {
            if (r.getStatus() != 201) {
                String respBody = "";
                try {
                    Object ent = r.getEntity();
                    respBody = ent == null ? "" : ent.toString();
                } catch (Exception ignored) {
                    // intentionally ignored when reading response entity
                }
                String msg = "Keycloak user create returned status " + r.getStatus() + ", body=" + respBody;
                try {
                    HttpStatus mapped = HttpStatus.valueOf(r.getStatus());
                    throw new ResponseStatusException(mapped, msg);
                } catch (IllegalArgumentException iae) {
                    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, msg);
                }
            }
            return CreatedResponseUtil.getCreatedId(r);
        }
    }

    private void assignRealmRoles(String keycloakId, String defaultRealmRole, java.util.Collection<Long> roleIds) {
        var realm = keycloakProvider.getInstance().realm(keycloakProvider.getRealm());
        var usersResource = realm.users();
        if (roleIds != null && !roleIds.isEmpty()) {
            List<Role> roles = roleIds.stream().map(id -> {
                Role r = new Role();
                r.setRoleId(id);
                return r;
            }).toList();
            for (Role r : roles) {
                try {
                    var roleRep = realm.roles().get(r.getShortName()).toRepresentation();
                    usersResource.get(keycloakId).roles().realmLevel().add(Collections.singletonList(roleRep));
                } catch (Exception ignored) {
                    // ignore if realm role not found; application can sync roles later
                }
            }
        } else if (defaultRealmRole != null && !defaultRealmRole.isBlank()) {
            var roleRep = realm.roles().get(defaultRealmRole).toRepresentation();
            usersResource.get(keycloakId).roles().realmLevel().add(Collections.singletonList(roleRep));
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(entityName));

        // remove from keycloak
        deleteKeycloakUser(user);

        // remove user roles
        // bulk delete user-role relationships to avoid ConcurrentModificationException
        userRoleRepository.deleteByUser(user);

        // remove user record
        userRepository.deleteById(userId);
    }

    private void deleteKeycloakUser(User user) {
        if (user == null || user.getAuthorizationServiceUserId() == null) return;
        deleteKeycloakUserResource(user.getAuthorizationServiceUserId());
    }

    private void deleteKeycloakUserResource(String authorizationServiceUserId) {
        try {
            var realm = keycloakProvider.getInstance().realm(keycloakProvider.getRealm());
            var usersResource = realm.users();
            try (Response ignoredResp = usersResource.delete(authorizationServiceUserId)) {
                // consume status to avoid empty-try warning
                ignoredResp.getStatus();
            }
        } catch (Exception ignored) {
            // intentionally ignored: best-effort removal from Keycloak
        }
    }

    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> {
                    UserResponse ur = userMapper.toUserResponse(user);
                    ur.setRoles(getRolesForUser(user));
                    return ur;
                });
    }

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(entityName));
        UserResponse ur = userMapper.toUserResponse(user);
        ur.setRoles(getRolesForUser(user));
        return ur;
    }

    public Page<UserResponse> searchUsers(String q, Pageable pageable) {
        String query = q == null ? "" : q;
        return userRepository
                .findByUserNameContainingIgnoreCaseOrEmailAddressContainingIgnoreCase(query, query, pageable)
                .map(user -> {
                    UserResponse ur = userMapper.toUserResponse(user);
                    ur.setRoles(getRolesForUser(user));
                    return ur;
                });
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserRequest request) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(entityName));

        // update Keycloak basic profile if present
        try {
            updateKeycloakProfileIfPresent(existing, request);
        } catch (Exception ignored) {
            // intentionally ignored: Keycloak update is best-effort
        }

        // update local entity using mapper (ignores null fields)
        userMapper.updateUser(existing, request);
        // Only overwrite fullName when caller provided firstName or lastName in the request.
        if (request.getFirstName() != null || request.getLastName() != null) {
            String f = request.getFirstName() == null ? "" : request.getFirstName();
            String l = request.getLastName() == null ? "" : request.getLastName();
            String full = (f + " " + l).trim();
            existing.setFullName(full);
        }
        User saved = userRepository.save(existing);

        // update local user-role join table if roleIds provided
        if (request.getRoleIds() != null) {
            // bulk delete existing relationships to avoid ConcurrentModificationException
            userRoleRepository.deleteByUser(saved);
            // add new ones
            if (!request.getRoleIds().isEmpty()) {
                Set<UserRole> urs = request.getRoleIds().stream().map(rid -> {
                    UserRole ur = new UserRole();
                    ur.setUserId(saved);
                    Role rr = new Role();
                    rr.setRoleId(rid);
                    ur.setRoleId(rr);
                    return ur;
                }).collect(Collectors.toSet());
                userRoleRepository.saveAll(urs);
            }
        }

        // build response with roles
        UserResponse ur = userMapper.toUserResponse(saved);
        ur.setRoles(getRolesForUser(saved));
        return ur;
    }

    private void updateKeycloakProfileIfPresent(User existing, UserRequest request) {
        var realm = keycloakProvider.getInstance().realm(keycloakProvider.getRealm());
        var usersResource = realm.users();
        if (existing.getAuthorizationServiceUserId() == null) return;

        // fetch and update representation
        UserRepresentation rep = usersResource.get(existing.getAuthorizationServiceUserId()).toRepresentation();
        if (request.getUserName() != null) rep.setUsername(request.getUserName());
        if (request.getFirstName() != null) rep.setFirstName(request.getFirstName());
        if (request.getLastName() != null) rep.setLastName(request.getLastName());
        if (request.getEmailAddress() != null) rep.setEmail(request.getEmailAddress());
        if (request.getIsEnabled() != null) rep.setEnabled(request.getIsEnabled());
        usersResource.get(existing.getAuthorizationServiceUserId()).update(rep);

        // update password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setTemporary(false);
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(request.getPassword());
            usersResource.get(existing.getAuthorizationServiceUserId()).resetPassword(cred);
        }
    }

    private Set<RoleResponse> getRolesForUser(User user) {
        return userRoleRepository.findByUserId(user).stream()
                .map(UserRole::getRoleId)
                .map(Role::getRoleId)
                .map(roleId -> roleRepository.findById(roleId).orElse(null))
                .map(this::buildRoleResponse)
                .collect(Collectors.toSet());
    }

    private RoleResponse buildRoleResponse(Role r) {
        if (r == null) return null;
        RoleResponse rr = RoleResponse.builder()
                .roleId(r.getRoleId())
                .shortName(r.getShortName())
                .description(r.getDescription())
                .note(r.getNote())
                .build();
        var perms = rolePermissionRepository.findByRoleId(r).stream()
                .map(RolePermission::getPermissionId)
                .map(p -> com.example.demo.dto.systemuser.Permission.PermissionResponse.builder()
                        .permissionId(p.getPermissionId())
                        .shortName(p.getShortName())
                        .description(p.getDescription())
                        .url(p.getUrl())
                        .method(p.getMethod())
                        .isPublic(p.getIsPublic())
                        .build())
                .collect(Collectors.toSet());
        rr.setPermissions(perms);
        return rr;
    }
}
