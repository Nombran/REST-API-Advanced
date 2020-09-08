package com.epam.esm.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Class AuthenticationController for Rest Api Advanced Task.
 *
 * @author ARTSIOM BERASTSEN
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthenticationController {
    /**
     * Field authenticationService
     *
     * @see AuthenticationService
     */
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * POST method login, that returns JWT created by user credentials <br>
     * <p>
     * [POST /api/v1/auth/login]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param requestDto represents user's credentials
     * @return AuthenticationRequestDto object with JWT and it's expiration time
     * @see AuthenticationRequestDto
     * @see AuthenticationService
     */
    @PostMapping(value = "/login")
    public AuthenticationResultDto login(@RequestBody @Valid AuthenticationRequestDto requestDto) {
        return authenticationService.getAuthenticationResult(requestDto);
    }
}
