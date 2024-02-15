package box.springsecuritycookie.payload.responses;

import box.springsecuritycookie.entities.Role;
import box.springsecuritycookie.entities.User;
import lombok.Getter;

@Getter
public class ShortUser {
    private final Long id;
    private final String email;
    private final Role role;

    ShortUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

}
