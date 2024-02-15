package box.springsecuritycookie.payload.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationRequest {

    @NotNull(message = "email must no be null")
    @Length(min = 6, max = 200, message
            = "email must be between 6 and 200 characters")
    @Email
    private String email;

    @NotNull(message = "password must no be null")
    @Length(min = 6, max = 200, message
            = "password must be between 6 and 200 characters")
    private String password;

    @NotNull(message = "key must not be null")
    @Length(min = 6, max = 6, message
            = "key must be 6 characters")
    private String key;
}
