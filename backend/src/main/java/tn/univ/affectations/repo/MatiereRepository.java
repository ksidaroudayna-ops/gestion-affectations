package tn.univ.affectations.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.univ.affectations.model.Matiere;

public interface MatiereRepository extends JpaRepository<Matiere, Long> {
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
