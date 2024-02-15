package box.springsecuritycookie.payload.responses;

import box.springsecuritycookie.entities.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UsersResponse {
    List<ShortUser> users;

    public UsersResponse(List<User> usersFull) {
        users = new ArrayList<>();
        for (User user : usersFull) {
            users.add(new ShortUser(user));
        }
    }
}

