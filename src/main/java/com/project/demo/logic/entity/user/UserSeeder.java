package com.project.demo.logic.entity.user;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.rol.RoleEnum;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createDefaultUser();
    }

    private void createDefaultUser() {
        User defaultUser = new User();
        defaultUser.setName("Andrey");
        defaultUser.setLastname("Bonilla");
        defaultUser.setEmail("rbb1234@gmail.com");
        defaultUser.setPassword("user123");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        Optional<User> optionalUser = userRepository.findByEmail(defaultUser.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var user = new User();
        user.setName(defaultUser.getName());
        user.setLastname(defaultUser.getLastname());
        user.setEmail(defaultUser.getEmail());
        user.setPassword(passwordEncoder.encode(defaultUser.getPassword()));
        user.setRole(optionalRole.get());

        userRepository.save(user);
    }
}