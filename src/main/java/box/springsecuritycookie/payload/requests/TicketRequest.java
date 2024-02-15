package box.springsecuritycookie.payload.requests;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class TicketRequest {

    @Length(min = 6, max = 6, message
            = "key must be 6 characters")
    private String key;
}
