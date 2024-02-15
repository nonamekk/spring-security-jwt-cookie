package box.springsecuritycookie.services;

import box.springsecuritycookie.entities.Role;
import box.springsecuritycookie.entities.User;
import box.springsecuritycookie.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminUserServiceImpl {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    public void createAdmin() {
        User user = new User();
        user.setEmail("admin@admin.com");

        String password = "admin";
        user.setPassword(encoder.encode(password));

        user.setRole(Role.ADMIN);
        userRepository.save(user);
        log.info("User admin is created. Email: " + user.getEmail() + " password: " + password);
    }
}
