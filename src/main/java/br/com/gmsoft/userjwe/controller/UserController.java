package br.com.gmsoft.userjwe.controller;

import br.com.gmsoft.userjwe.dto.*;
import br.com.gmsoft.userjwe.dto.View;
import br.com.gmsoft.userjwe.security.JwtAuthentication;
import br.com.gmsoft.userjwe.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersion.V1+"/user")
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

    }

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered the user",content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content (
                    mediaType = "application/json",
                    schema = @Schema (defaultValue = "{ 'fieldname': 'errordescription'}")
            ))
    })
    @PostMapping()
    public ResponseEntity<String> registerUser(@JsonView({View.PostVisibility.class}) @RequestBody @Validated(UserDto.OnCreate.class) UserDto userDto)  {

        userService.registerUser(userDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Update the user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user Data",content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content (
                    mediaType = "application/json",
                    schema = @Schema (defaultValue = "{ 'fieldname': 'errordescription'}")
            ))
    })
    @PutMapping()
    public ResponseEntity<String> updateUserData(@JsonView(View.PutVisibility.class) @RequestBody @Validated(UserDto.OnUpdate.class) UserDto userDto)  {

        userService.updateUserDetails(userDto,getCurrentUserEmail());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Generate OTP for password recovery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully generated OTP",content = @Content (mediaType = "application/json", schema = @Schema(implementation = OtpTokenDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",content = @Content)
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<OtpTokenDto> forgotPassword(@RequestBody @Valid Emailinput email)  {

        OtpTokenDto otp = userService.generateOtp(email.email());
        return new ResponseEntity<>(otp, HttpStatus.CREATED);

    }


    @PostMapping("/reset-password")
    @Operation(summary = "Reset user's password using OTP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully reset password", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid OTP or email", content = @Content (
                    mediaType = "application/json",
                    schema = @Schema (defaultValue = "{ 'error': 'description'}")
            ) )
    })
    public ResponseEntity<String> resetPassword(@JsonView(UserAuthenticationInput.OnResetPassword.class)
                                                    @RequestBody @Validated(UserAuthenticationInput.OnResetPassword.class)
                                                    UserAuthenticationInput passwordInput)  {
        userService.resetPassword(passwordInput);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/delete-account")
    @Operation(summary = "Request an otp token to delete the account")
    @ApiResponses(value = { @ApiResponse(responseCode = "201",
            description = "Successfully generated OTP",
            content = @Content (mediaType = "application/json", schema = @Schema(implementation = OtpTokenDto.class)))})
    public ResponseEntity<OtpTokenDto> deleteAccount()  {

        OtpTokenDto otp = userService.generateOtp(getCurrentUserEmail());
        return new ResponseEntity<>(otp, HttpStatus.CREATED);

    }

    @Operation(summary = "Confirm account deletion using otp and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted account", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid OTP", content = @Content (
                    mediaType = "application/json",
                    schema = @Schema (defaultValue = "{ 'error': 'description'}")
            ) )
    })
    @PostMapping("/confirm-delete")
    public ResponseEntity<String> confirmDelete(@JsonView(UserAuthenticationInput.OnDeleteAccount.class)
                                                    @RequestBody @Validated(UserAuthenticationInput.OnDeleteAccount.class) UserAuthenticationInput userAuthenticationInput)  {


        userService.deleteAccount(getCurrentUserEmail(),userAuthenticationInput);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var loggedUserDetails = ((JwtAuthentication) authentication).getLoggedUserDetails();
        return loggedUserDetails.email();
    }

}