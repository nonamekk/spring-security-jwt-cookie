package box.springsecuritycookie.services;

import box.springsecuritycookie.entities.Ticket;
import box.springsecuritycookie.payload.requests.NewTicketCreationRequest;
import box.springsecuritycookie.repositories.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AdminControlsServiceImpl {

    @Autowired
    TicketRepository ticketRepository;

    public Optional<Ticket> createTicket(@NotNull NewTicketCreationRequest request) throws NoSuchAlgorithmException {
        Ticket ticket = new Ticket();
        ticket.setRole(request.getRole());
        ticket.setSecret(generateSecret());
        ticket.setUses(request.getUses());

        return Optional.of(ticketRepository.save(ticket));
    }

    public @NotNull List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    public Ticket getTicket(String key) throws Exception {
        Optional<Ticket> ticket = ticketRepository.findBySecret(key);
        return ticket.orElseThrow(() -> new Exception("Ticket not found"));
    }

    public void voidTicket(@NotNull Ticket ticket) {
        if (ticket.getUses() == 1) {
            ticketRepository.delete(ticket);
            log.info("Ticket was deleted");
            return;
        }
        ticket.setUses(ticket.getUses()-1);
        ticketRepository.save(ticket);
        log.info("Ticket uses are updated");
    }

    public void deleteTicket(Long id) throws Exception {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new Exception("Ticket was not found"));
        ticketRepository.delete(ticket);
        log.info("Ticket was deleted");
    }

    private @NotNull String generateSecret() throws NoSuchAlgorithmException {
        String randomUUID = UUID.randomUUID().toString();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(randomUUID.getBytes());
        byte[] digest = messageDigest.digest();
        String hash = DatatypeConverter.printHexBinary(digest).toLowerCase();
        return hash.substring(hash.length()-6);
    }
}
