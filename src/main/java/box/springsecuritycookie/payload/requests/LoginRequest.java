package box.springsecuritycookie.payload.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

    @NotNull(message = "email must no be null")
    @Length(min = 6, max = 200, message
            = "email must be between 6 and 200 characters")
    @Email
    private String email;

    @NotNull(message = "password must no be null")
    @Length(min = 6, max = 200, message
            = "password must be between 6 and 200 characters")
    private String password;
}
