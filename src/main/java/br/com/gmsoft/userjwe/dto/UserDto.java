package br.com.gmsoft.userjwe.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;


public record UserDto(@JsonView(View.GetVisibility.class) Long id,

                      @JsonView(View.PutVisibility.class)
                      @NotBlank(groups = OnCreate.class) @Length( min = 10, max = 100 ,groups = {OnCreate.class,OnUpdate.class })String name,

                      @JsonView(View.PutVisibility.class)
                      @NotBlank(groups = OnCreate.class) @Email(groups = OnCreate.class) @Length( max = 100 ,groups = {OnCreate.class,OnUpdate.class })String email,

                      @JsonView(View.PostVisibility.class)
                      @NotBlank(groups = OnCreate.class)
                      @Length(min = 8, max = 20, groups = OnCreate.class) String password,

                      @JsonView(View.PutVisibility.class)
                      @Pattern(regexp="^[1-9]{2}[9][0-9]{8}$",groups = {OnCreate.class,OnUpdate.class }) String phone,

                      @JsonView(View.PutVisibility.class)
                      @NotBlank(groups = OnCreate.class) @Length( min = 10, max = 100 ,groups = {OnCreate.class,OnUpdate.class }) String secretPhrase,

                      @JsonView(View.PutVisibility.class)
                      @NotBlank(groups = OnCreate.class) @Length( min = 3, max = 100 ,groups = {OnCreate.class,OnUpdate.class }) String secretAnswer) {

    public interface OnCreate {}
    public interface OnUpdate {}
}



