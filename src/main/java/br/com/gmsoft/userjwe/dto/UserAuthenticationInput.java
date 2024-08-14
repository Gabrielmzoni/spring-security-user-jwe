package br.com.gmsoft.userjwe.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserAuthenticationInput(@JsonView(OnResetPassword.class)
                                      @NotBlank(groups = OnResetPassword.class)
                                      @Email(groups = OnResetPassword.class)
                                      @Length(groups = OnResetPassword.class, max = 100 ) String email,
                                      @JsonView(OnDeleteAccount.class)
                                      @NotBlank(groups = {OnResetPassword.class,OnDeleteAccount.class})
                                      @Length(groups = {OnResetPassword.class,OnDeleteAccount.class},min = 6, max = 6) String otp,
                                      @JsonView(OnDeleteAccount.class)
                                      @NotBlank(groups = {OnResetPassword.class})
                                      @Length(groups = {OnResetPassword.class},min = 6, max = 20) String password){

    public interface OnResetPassword extends OnDeleteAccount{}
    public interface OnDeleteAccount {}
}
