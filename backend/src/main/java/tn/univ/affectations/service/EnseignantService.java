package tn.univ.affectations.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.univ.affectations.exception.*;
import tn.univ.affectations.model.Enseignant;
import tn.univ.affectations.repo.*;

@Service
@Transactional
public class EnseignantService {
    private final EnseignantRepository repo;
    private final AffectationRepository affectations;

    public EnseignantService(EnseignantRepository repo, AffectationRepository affectations) {
        this.repo = repo; this.affectations = affectations;
    }

    @Transactional(readOnly = true)
    public List<Enseignant> findAll() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public Enseignant get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Enseignant introuvable : " + id));
    }

    public Enseignant create(Enseignant e) {
        e.setId(null);
        if (repo.existsByEmailIgnoreCase(e.getEmail()))                                  // RG1
            throw new BusinessException("RG1", "Un enseignant avec cet email existe déjà.");
        return repo.save(e);
    }

    public Enseignant update(Long id, Enseignant data) {
        Enseignant e = get(id);
        if (repo.existsByEmailIgnoreCaseAndIdNot(data.getEmail(), id))                   // RG1
            throw new BusinessException("RG1", "Un enseignant avec cet email existe déjà.");
        e.setNom(data.getNom()); e.setPrenom(data.getPrenom()); e.setEmail(data.getEmail());
        e.setGrade(data.getGrade()); e.setDepartement(data.getDepartement());
        return repo.save(e);
    }

    public void delete(Long id) {
        Enseignant e = get(id);
        if (affectations.existsByEnseignantId(id))                                       // RG8
            throw new BusinessException("RG8", "Suppression impossible : l'enseignant a des affectations.");
        repo.delete(e);
    }
}
