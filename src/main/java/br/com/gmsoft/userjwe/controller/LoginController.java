package br.com.gmsoft.userjwe.controller;

import br.com.gmsoft.userjwe.dto.AccountCredentialsInput;
import br.com.gmsoft.userjwe.security.JwtAuthentication;
import br.com.gmsoft.userjwe.service.JwtService;
import br.com.gmsoft.userjwe.service.UserService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController()
@RequestMapping(ApiVersion.V1+"/user/login")
public class LoginController {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final Gson gson = new Gson();

    @Autowired
    public LoginController(JwtService jwtService, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Operation(summary = "Login and receive a JWE token in the response header")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "200", description = "Successfully authenticated the user", headers = @Header(name = HttpHeaders.AUTHORIZATION, description = "Bearer JWT token"), content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body",content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",content = @Content)
    })
    @PostMapping()
    public ResponseEntity<String> getToken(@RequestBody AccountCredentialsInput credentials) {

        var creds = new JwtAuthentication(credentials);

        var auth = authenticationManager.authenticate(creds);

        var loggedUserDetails = userService.getLoggedUserDetails(auth.getName());

        var jwe = jwtService.getAuthorizationToken(gson.toJson(loggedUserDetails));

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwe)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization").build();
    }

}