package box.springsecuritycookie.services;

import box.springsecuritycookie.entities.User;
import box.springsecuritycookie.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                new UsernameNotFoundException(("User with email=" + email + " not found")));
    }

    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                new UsernameNotFoundException(("User with email=" + email + " not found")));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id, String deleteResponsibleEmail) throws Exception {
        Optional<User> user = userRepository.findById(id);
        User userToDelete = user.orElseThrow(()-> new Exception("No user with such id found"));

        if (!userToDelete.getEmail().equals(deleteResponsibleEmail)) {
            userRepository.deleteById(id);
            return;
        }
        throw new Exception("Cannot delete self");
    }

}
