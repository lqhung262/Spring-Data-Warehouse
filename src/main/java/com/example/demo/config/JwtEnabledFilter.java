package com.example.demo.config;

import com.example.demo.repository.systemuser.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Reject requests whose JWT subject maps to a local user that is disabled (isEnabled == false).
 * The filter expects the JWT to be already decoded and put into the SecurityContext by the
 * resource server JWT processing. It reads the 'sub' claim and looks up local user by
 * authorizationServiceUserId.
 */
@Component
@RequiredArgsConstructor
public class JwtEnabledFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            String sub = jwt.getSubject();
            if (sub != null && !sub.isBlank()) {
                userRepository.findByAuthorizationServiceUserId(sub).ifPresent(user -> {
                    if (user.getIsEnabled() != null && !user.getIsEnabled()) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        try {
                            response.getWriter().write("{\"error\":\"user_disabled\"}");
                        } catch (IOException ignored) {
                        }
                    }
                });
                // if user enabled or not present, continue; If disabled, we already wrote response.
                if (response.isCommitted()) return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

