package saga.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import saga.database.User;
import saga.database.repositories.UserRepository;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminSeeder(
            UserRepository userRep,
            PasswordEncoder passEnc,
            @Value("${admin.seed.username:#{null}}") String adminUsername,
            @Value("${admin.seed.password:#{null}}") String adminPassword) {
        this.userRepository = userRep;
        this.passwordEncoder = passEnc;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping admin seed.");
            return;
        }

        if (adminUsername == null || adminPassword == null) {
            log.warn("No admin user existent. " +
                    "Set ADMIN_SEED_USERNAME and ADMIN_SEED_PASSWORD env vars to create the first admin.");
            return;
        }

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);

        log.info("Seeded initial admin user '{}'.", adminUsername);
    }
}