package com.OPD.OPDManagment.controller;

import com.OPD.OPDManagment.dto.LoginRequestDto;
import com.OPD.OPDManagment.dto.LoginResponseDto;
import com.OPD.OPDManagment.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = authService.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@Valid @RequestBody com.OPD.OPDManagment.dto.RegisterRequestDto registerRequestDto) {
        LoginResponseDto response = authService.register(registerRequestDto);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Map<String, String>>> getAvailableRoles() {
        List<Map<String, String>> roles = List.of(
                Map.of("code", "PATIENT", "label", "Patient Portal", "description", "Book appointments & view health records"),
                Map.of("code", "DOCTOR", "label", "Doctor Portal", "description", "Manage OPD queue & conduct consultations"),
                Map.of("code", "ADMIN", "label", "Hospital Administrator", "description", "System configuration & full clinic management")
        );
        return ResponseEntity.ok(roles);
    }
}
