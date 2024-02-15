package box.springsecuritycookie.repositories;

import box.springsecuritycookie.entities.Ticket;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findBySecret(String secret);
    @NotNull List<Ticket> findAll();
}
