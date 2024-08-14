package br.com.gmsoft.userjwe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record Emailinput(@NotBlank @Email @Length( max = 100 ) String email) {
}
