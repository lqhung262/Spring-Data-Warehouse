package com.example.demo.config;

import com.example.demo.entity.systemuser.Permission;
import com.example.demo.entity.systemuser.User;
import com.example.demo.repository.systemuser.RolePermissionRepository;
import com.example.demo.repository.systemuser.UserRepository;
import com.example.demo.repository.systemuser.UserRoleRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Permission check filter: reads JWT subject, loads local roles for that subject, then loads permissions
 * for each role from DB and checks request method+path. Skips swagger/open endpoints.
 */
@Component
public class PermissionAuthorizationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(PermissionAuthorizationFilter.class);

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/actuator/health",
            "/auth/**"
    };

    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // cache: roleShortName -> list of Permission
    private final Map<String, List<Permission>> rolePermCache = new ConcurrentHashMap<>();

    public PermissionAuthorizationFilter(RolePermissionRepository rolePermissionRepository,
                                         UserRepository userRepository,
                                         UserRoleRepository userRoleRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        // allow swagger and open endpoints
        for (String pattern : SWAGGER_WHITELIST) {
            if (pathMatcher.match(pattern, uri)) {
                filterChain.doFilter(request, response);

                return;
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // require JWT principal to proceed with permission checks; otherwise continue and let security handle auth
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            filterChain.doFilter(request, response);

            return;
        }

        try {
            String subject = jwt.getSubject();
            if (subject == null || subject.isBlank()) {
                respondForbidden(response, "no_subject");

                return;
            }

            // load local user and their roles
            Optional<User> localOpt = userRepository.findByAuthorizationServiceUserId(subject);
            if (localOpt.isEmpty()) {
                respondForbidden(response, "user_not_found_local");

                return;
            }
            User local = localOpt.get();
            List<String> roleShortNames = userRoleRepository.findRoleShortNamesByUser(local);
            if (roleShortNames == null) roleShortNames = new ArrayList<>();

            // if super admin, allow
            if (roleShortNames.stream().anyMatch("ROLE_DDC_SUPER_ADMIN"::equals)) {
                filterChain.doFilter(request, response);
                return;
            }

            // gather permissions for roles
            List<Permission> permissions = new ArrayList<>();
            for (String role : roleShortNames) {
                permissions.addAll(loadPermissionsForRole(role));
            }

            if (permissions.isEmpty()) {
                respondForbidden(response, "no_permission");
                return;
            }

            String reqMethod = request.getMethod();
            boolean allowed = permissions.stream().anyMatch(p -> {
                boolean methodMatches = p.getMethod().equalsIgnoreCase(reqMethod);
                boolean pathMatches = pathMatcher.match(p.getUrl(), uri) || pathMatcher.match(p.getUrl() + "/**", uri);

                return methodMatches && pathMatches;
            });

            if (!allowed) {
                respondForbidden(response, "forbidden_by_permission");

                return;
            }

            filterChain.doFilter(request, response);
        } catch (Throwable t) {
            // prevent any unexpected exception from causing 500; return controlled 403 with message
            log.error("Permission filter failure: {}", t.getMessage(), t);
            respondForbidden(response, "permission_check_failed: " + t.getClass().getSimpleName());
        }
    }

    private void respondForbidden(HttpServletResponse response, String code) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + code + "\"}");
    }

    private List<Permission> loadPermissionsForRole(String roleShortName) {
        return rolePermCache.computeIfAbsent(roleShortName, rn -> {
            try {
                List<Permission> perms = rolePermissionRepository.findPermissionsByRoleShortName(rn);

                return perms != null ? perms : Collections.emptyList();
            } catch (Exception e) {
                log.warn("Failed to load permissions for role {}: {}", rn, e.getMessage());

                return Collections.emptyList();
            }
        });
    }

    // cache evict helpers kept but not exposed
    public void evictCacheForRole(String roleShortName) {
        rolePermCache.remove(roleShortName);
    }

    public void evictAllCache() {
        rolePermCache.clear();
    }
}
