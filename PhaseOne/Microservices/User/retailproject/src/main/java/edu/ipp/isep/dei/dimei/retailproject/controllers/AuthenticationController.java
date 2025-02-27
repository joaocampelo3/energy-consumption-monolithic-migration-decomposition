package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.LoginDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.RegisterDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(
            description = "Login Service",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"token\": \"Bearer xxxxxxxxxxxxxxxx\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> login(@RequestBody LoginDTO loginDTO) {
        try {
            return ResponseEntity.ok(authenticationService.login(loginDTO));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    @Operation(
            description = "Register Service",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Register user successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"token\": \"Bearer xxxxxxxxxxxxxxxx\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterDTO registerDTO) {
        return ResponseEntity.ok(authenticationService.register(registerDTO));
    }

    @PostMapping("/register/admin")
    @Operation(
            description = "Register Service",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Register admin user successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"token\": \"Bearer xxxxxxxxxxxxxxxx\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<AuthenticationResponse> registerAdmin(@RequestBody RegisterDTO registerDTO) {
        return ResponseEntity.ok(authenticationService.registerAdmin(registerDTO));
    }

    @PostMapping("/register/merchant")
    @Operation(
            description = "Register Service",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Register merchant user successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"token\": \"Bearer xxxxxxxxxxxxxxxx\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<AuthenticationResponse> registerMerchant(@RequestBody RegisterDTO registerDTO) {
        return ResponseEntity.ok(authenticationService.registerMerchant(registerDTO));
    }

}
