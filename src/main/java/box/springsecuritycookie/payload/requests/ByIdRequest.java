package box.springsecuritycookie.payload.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ByIdRequest {

    @NotNull(message = "id must not be null")
    @Min(0)
    private Long id;
}
