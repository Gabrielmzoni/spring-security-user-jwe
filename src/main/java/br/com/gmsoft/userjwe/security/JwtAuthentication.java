package br.com.gmsoft.userjwe.security;

import br.com.gmsoft.userjwe.dto.AccountCredentialsInput;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.Objects;

public class JwtAuthentication extends UsernamePasswordAuthenticationToken  {
    private LoggedUserDetails loggedUserDetails;

    public JwtAuthentication(LoggedUserDetails loggedUserDetails) {
        super(loggedUserDetails.email(), null, Collections.emptyList());
        this.loggedUserDetails = loggedUserDetails;
    }

    public JwtAuthentication(AccountCredentialsInput credentials) {
        super(credentials.email(), credentials.password(), Collections.emptyList());

    }

    public LoggedUserDetails getLoggedUserDetails() {
        return loggedUserDetails;
    }

     @Override
     public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        JwtAuthentication that = (JwtAuthentication) obj;
        return loggedUserDetails.equals(that.loggedUserDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), loggedUserDetails);
    }
}