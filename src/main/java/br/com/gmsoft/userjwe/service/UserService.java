package br.com.gmsoft.userjwe.service;

import br.com.gmsoft.userjwe.domain.User;
import br.com.gmsoft.userjwe.dto.OtpTokenDto;
import br.com.gmsoft.userjwe.dto.UserAuthenticationInput;
import br.com.gmsoft.userjwe.dto.UserDto;
import br.com.gmsoft.userjwe.security.LoggedUserDetails;

import java.util.Optional;

public interface UserService {

    UserDto registerUser(UserDto userDto);

    Optional<User> findByEmail(String email);

    void updateUserDetails(UserDto userdto, String email);

    LoggedUserDetails getLoggedUserDetails(String email);

    OtpTokenDto generateOtp(String email);

    void validateOtp(String email, String otp);

    void resetPassword(UserAuthenticationInput passwordInput);

    void deleteAccount(String email,UserAuthenticationInput userAuthenticationInput);

}
