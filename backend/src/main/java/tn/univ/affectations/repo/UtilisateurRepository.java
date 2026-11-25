package tn.univ.affectations.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.univ.affectations.model.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByUsername(String username);
}
