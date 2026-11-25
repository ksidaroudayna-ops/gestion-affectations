package tn.univ.affectations.controller;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tn.univ.affectations.model.Utilisateur;
import tn.univ.affectations.repo.UtilisateurRepository;
import tn.univ.affectations.security.JwtService;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    private final UtilisateurRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UtilisateurRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest r) {
        return users.findByUsername(r.username())
                .filter(u -> encoder.matches(r.password(), u.getPassword()))
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of(
                        "token", jwt.generate(u.getUsername(), u.getRole().name()),
                        "username", u.getUsername(), "role", u.getRole().name())))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("message", "Identifiants invalides.")));
    }
}
