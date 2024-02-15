package box.springsecuritycookie.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupHousekeeper {

    @Autowired
    AdminUserServiceImpl adminUserService;

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        adminUserService.createAdmin();
    }
}
