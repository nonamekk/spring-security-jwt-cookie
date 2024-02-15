package box.springsecuritycookie.payload.responses;

import box.springsecuritycookie.entities.Role;
import box.springsecuritycookie.entities.Ticket;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class TicketResponse {
    private final Role role;
    private final String secret;
    private final Integer uses;

    public TicketResponse(@NotNull Ticket ticket) {
        this.role = ticket.getRole();
        this.secret = ticket.getSecret();
        this.uses = ticket.getUses();
    }
}
