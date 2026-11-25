package tn.univ.affectations.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.univ.affectations.exception.*;
import tn.univ.affectations.model.Matiere;
import tn.univ.affectations.repo.*;

@Service
@Transactional
public class MatiereService {
    private final MatiereRepository repo;
    private final AffectationRepository affectations;

    public MatiereService(MatiereRepository repo, AffectationRepository affectations) {
        this.repo = repo; this.affectations = affectations;
    }

    @Transactional(readOnly = true)
    public List<Matiere> findAll() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public Matiere get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Matière introuvable : " + id));
    }

    public Matiere create(Matiere m) {
        m.setId(null);
        if (repo.existsByCodeIgnoreCase(m.getCode()))                                    // RG2
            throw new BusinessException("RG2", "Le code matière existe déjà.");
        return repo.save(m);
    }

    public Matiere update(Long id, Matiere data) {
        Matiere m = get(id);
        if (repo.existsByCodeIgnoreCaseAndIdNot(data.getCode(), id))                     // RG2
            throw new BusinessException("RG2", "Le code matière existe déjà.");
        m.setCode(data.getCode()); m.setIntitule(data.getIntitule());
        m.setVolumeHoraire(data.getVolumeHoraire()); m.setSemestre(data.getSemestre());
        return repo.save(m);
    }

    public void delete(Long id) {
        Matiere m = get(id);
        if (affectations.existsByMatiereId(id))                                          // RG9
            throw new BusinessException("RG9", "Suppression impossible : la matière est affectée.");
        repo.delete(m);
    }
}
