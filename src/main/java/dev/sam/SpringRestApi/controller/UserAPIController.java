package dev.sam.SpringRestApi.controller;

import dev.sam.SpringRestApi.dto.LoginDTO;
import dev.sam.SpringRestApi.dto.UserDTO;
import dev.sam.SpringRestApi.response.AuthenticationResponse;
import dev.sam.SpringRestApi.service.UserAuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAPIController {
    private final UserAuthenticationService authService;

    public UserAPIController(UserAuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid UserDTO userDTO) {
        return authService.register(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login (@RequestBody @Valid LoginDTO request) {
        return authService.authenticateUser(request);
    }
}
