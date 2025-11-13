package com.example.demo.controller.systemuser;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.systemuser.Authentication.AuthenticationRequest;
import com.example.demo.dto.systemuser.Authentication.AuthenticationResponse;
import com.example.demo.dto.systemuser.Introspect.IntrospectRequest;
import com.example.demo.dto.systemuser.Introspect.IntrospectResponse;
import com.example.demo.service.systemuser.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;


    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        var result = authenticationService.authenticate(authenticationRequest);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) {
        var result = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
