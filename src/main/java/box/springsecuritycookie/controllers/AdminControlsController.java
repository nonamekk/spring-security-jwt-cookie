package box.springsecuritycookie.controllers;

import box.springsecuritycookie.entities.Ticket;
import box.springsecuritycookie.entities.User;
import box.springsecuritycookie.payload.requests.ByIdRequest;
import box.springsecuritycookie.payload.requests.NewTicketCreationRequest;
import box.springsecuritycookie.payload.responses.TicketResponse;
import box.springsecuritycookie.payload.responses.UsersResponse;
import box.springsecuritycookie.security.utils.JwtUtils;
import box.springsecuritycookie.services.AdminControlsServiceImpl;
import box.springsecuritycookie.services.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/v1/admin/controls")
@SpringBootApplication
public class AdminControlsController {

    @Autowired
    AdminControlsServiceImpl adminControlsService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/create_ticket")
    public ResponseEntity<?> createTicket(@Validated @RequestBody NewTicketCreationRequest ticketCreationRequest) throws Exception {
        Optional<Ticket> ticket = adminControlsService.createTicket(ticketCreationRequest);
        Ticket newTicket = ticket.orElseThrow(() -> new Exception("Ticket was not created"));
        log.info("Ticket was created");
        return ResponseEntity.ok().body(new TicketResponse(newTicket));
    }

    @GetMapping("/tickets")
    public ResponseEntity<?> getAllTickets() {
        List<Ticket> tickets = adminControlsService.getAll();
        log.info("Tickets was obtained");
        return ResponseEntity.ok().body(Map.of("tickets", tickets));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userDetailsService.getAll();
        log.info("Users were obtained");
        return ResponseEntity.ok().body(new UsersResponse(users));
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(
            @Validated @RequestBody @NotNull ByIdRequest deleteUserRequest,
            @CookieValue("__Host-auth-token") String cookie,
            HttpServletRequest request
    ) throws Exception {
        userDetailsService.deleteUser(
                deleteUserRequest.getId(),
                jwtUtils.extractSubject(cookie)
        );
        log.info("User was deleted");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/ticket")
    public ResponseEntity<?> deleteTicket(@Validated @RequestBody @NotNull ByIdRequest deleteTicketRequest) throws Exception {
        adminControlsService.deleteTicket(deleteTicketRequest.getId());
        log.info("Ticket was deleted");
        return ResponseEntity.noContent().build();
    }
}
