package tn.univ.affectations.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.univ.affectations.model.Enseignant;

public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
