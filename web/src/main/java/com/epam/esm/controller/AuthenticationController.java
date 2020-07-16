package com.epam.esm.controller;

import com.epam.esm.authentication.AuthenticationRequestDto;
import com.epam.esm.jwt.AuthenticationResultDto;
import com.epam.esm.jwt.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Valid
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/login")
    public AuthenticationResultDto login(@RequestBody @Valid AuthenticationRequestDto requestDto) {
        return authenticationService.getAuthenticationResult(requestDto);
    }
}
