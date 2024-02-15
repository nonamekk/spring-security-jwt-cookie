package box.springsecuritycookie.services;

import box.springsecuritycookie.entities.Ticket;
import box.springsecuritycookie.entities.User;
import box.springsecuritycookie.repositories.UserRepository;
import box.springsecuritycookie.payload.requests.RegistrationRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationServiceImpl {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AdminControlsServiceImpl adminControlsService;

    public void addUser(@NotNull RegistrationRequest user) throws Exception {
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(encoder.encode(user.getPassword()));

        Ticket ticket = adminControlsService.getTicket(user.getKey());

        newUser.setRole(ticket.getRole());
        Optional<User> optionalUser = Optional.of(userRepository.save(newUser));
        adminControlsService.voidTicket(ticket);
    }
}
