package br.com.gmsoft.userjwe.security;

import br.com.gmsoft.userjwe.domain.User;
import br.com.gmsoft.userjwe.repository.UserRepository;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        UserBuilder builder = null;
        if (userOptional.isPresent()) {
            var currentUser = userOptional.get();
            builder = org.springframework.security.core.userdetails.
                    User.withUsername(email);
            builder.password(currentUser.getPassword());

        } else {
            throw new UsernameNotFoundException("User with the email "+email
                    +" not found.");
        }
        return builder.build();
    }
}
