package br.com.gmsoft.userjwe.security;

import java.io.Serial;
import java.io.Serializable;

public record LoggedUserDetails(String name, String email, String phone) implements Serializable {
    @Serial
    private static final long serialVersionUID = -1402661843145739369L;

}
