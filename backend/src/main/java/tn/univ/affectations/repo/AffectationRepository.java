package tn.univ.affectations.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.univ.affectations.model.*;

public interface AffectationRepository extends JpaRepository<Affectation, Long> {
    List<Affectation> findByEnseignantId(Long enseignantId);
    List<Affectation> findByMatiereId(Long matiereId);
    boolean existsByEnseignantId(Long enseignantId);
    boolean existsByMatiereId(Long matiereId);
    boolean existsByEnseignantIdAndMatiereIdAndTypeSeanceAndAnneeUniversitaire(
            Long ensId, Long matId, TypeSeance type, String annee);

    @Query("select coalesce(sum(a.heures),0) from Affectation a " +
           "where a.enseignant.id = :ens and a.anneeUniversitaire = :annee")
    int totalHeuresEnseignant(@Param("ens") Long ens, @Param("annee") String annee);

    @Query("select coalesce(sum(a.heures),0) from Affectation a " +
           "where a.matiere.id = :mat and a.anneeUniversitaire = :annee")
    int totalHeuresMatiere(@Param("mat") Long mat, @Param("annee") String annee);
}
