package br.com.gmsoft.userjwe.service;

import br.com.gmsoft.userjwe.domain.User;
import br.com.gmsoft.userjwe.dto.OtpTokenDto;
import br.com.gmsoft.userjwe.dto.UserAuthenticationInput;
import br.com.gmsoft.userjwe.dto.UserDto;
import br.com.gmsoft.userjwe.service.exception.UserAlreadyExistsException;
import br.com.gmsoft.userjwe.service.exception.UserNotFoundException;
import br.com.gmsoft.userjwe.mapper.UserMapper;
import br.com.gmsoft.userjwe.repository.UserRepository;
import br.com.gmsoft.userjwe.security.LoggedUserDetails;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    UserMapper userMapper = UserMapper.INSTANCE;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    private final Cache<String, String> otpCache;

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        otpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
        this.userRepository = userRepository;
    }
    public UserDto registerUser(UserDto userDto) {

        Optional<User> userByEmail = userRepository.findByEmail(userDto.email());

        if (userByEmail.isPresent()) {
            throw new UserAlreadyExistsException("e-mail", userDto.email());
        }

        Optional<User> userByPhone = userRepository.findByPhone(userDto.phone());

        if (userByPhone.isPresent()) {
            throw new UserAlreadyExistsException("phone", userDto.phone());
        }

        var user = userMapper.userDtoToUser(userDto);
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        userDto = userMapper.userToUserDto(user);
        return userDto;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUserDetails(UserDto userdto, String email) {

        var user = findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        user.setName(userdto.name());
        user.setPhone(userdto.phone());
        user.setEmail(userdto.email());
        user.setSecretPhrase(userdto.secretPhrase());
        user.setSecretAnswer(userdto.secretAnswer());
        userRepository.save(user);

    }

    public LoggedUserDetails getLoggedUserDetails(String email) {

        return userRepository.findUserDetailsByEmail(email).orElseThrow(() -> new UserNotFoundException(email));

    }

    public OtpTokenDto generateOtp(String email) {

        findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        var otp = String.valueOf(secureRandom.nextInt(900000) + 100000);
        otpCache.put(email, otp);
        return new OtpTokenDto(otp);
    }

    public void validateOtp(String email, String otp) {
        String cachedOtp = otpCache.getIfPresent(email);
        if (cachedOtp == null || !cachedOtp.equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }
        otpCache.invalidate(email);
    }

    public void resetPassword(UserAuthenticationInput passwordInput) {

        validateOtp(passwordInput.email(), passwordInput.otp());
        var userToUpdate = userRepository.findByEmail(passwordInput.email()).orElseThrow(() -> new UserNotFoundException(passwordInput.email()));
        userToUpdate.setPassword(passwordEncoder.encode(passwordInput.password()));
        userRepository.save(userToUpdate);

    }

    public void deleteAccount(String email,UserAuthenticationInput userAuthenticationInput) {

        validateOtp(email, userAuthenticationInput.otp());
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        if (passwordEncoder.matches(userAuthenticationInput.password(), user.getPassword()))
            userRepository.delete(user);
        else
            throw new BadCredentialsException("Invalid password");
    }


}