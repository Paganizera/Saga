package saga.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import saga.database.User;
import saga.database.repositories.UserRepository;
import saga.dto.LoginDTO;
import saga.security.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRep;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRep, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRep = userRep;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTO.Response> login(@RequestBody LoginDTO.Request request) {
        User user = userRep.findByUsername(request.username());

        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new LoginDTO.Response(token));
    }
}