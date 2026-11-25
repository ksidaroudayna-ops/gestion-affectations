package tn.univ.affectations.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.univ.affectations.exception.*;
import tn.univ.affectations.model.*;
import tn.univ.affectations.repo.*;

@Service
@Transactional
public class AffectationService {

    public record AffectationRequest(Long enseignantId, Long matiereId, TypeSeance typeSeance,
                                     Integer heures, String anneeUniversitaire) {}

    private final AffectationRepository repo;
    private final EnseignantService enseignants;
    private final MatiereService matieres;

    public AffectationService(AffectationRepository repo, EnseignantService enseignants, MatiereService matieres) {
        this.repo = repo; this.enseignants = enseignants; this.matieres = matieres;
    }

    @Transactional(readOnly = true)
    public List<Affectation> findAll() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public Affectation get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Affectation introuvable : " + id));
    }

    public Affectation create(AffectationRequest r) {
        Affectation a = new Affectation();
        fill(a, r);
        check(a, 0);
        return repo.save(a);
    }

    public Affectation update(Long id, AffectationRequest r) {
        Affectation a = get(id);
        int ancienneHeures = a.getHeures();
        Long ancienEns = a.getEnseignant().getId(), ancienneMat = a.getMatiere().getId();
        String ancienneAnnee = a.getAnneeUniversitaire();
        fill(a, r);
        // on retire l'ancienne valeur du total uniquement si on reste sur la même clé de comptage
        int retireEns = ancienEns.equals(a.getEnseignant().getId()) && ancienneAnnee.equals(a.getAnneeUniversitaire()) ? ancienneHeures : 0;
        int retireMat = ancienneMat.equals(a.getMatiere().getId()) && ancienneAnnee.equals(a.getAnneeUniversitaire()) ? ancienneHeures : 0;
        check(a, retireEns, retireMat, id);
        return repo.save(a);
    }

    public void delete(Long id) { repo.delete(get(id)); }

    /** Charge d'un enseignant pour une année donnée. */
    @Transactional(readOnly = true)
    public Map<String, Object> charge(Long enseignantId, String annee) {
        Enseignant e = enseignants.get(enseignantId);
        int total = repo.totalHeuresEnseignant(enseignantId, annee);
        return Map.of("enseignantId", enseignantId, "anneeUniversitaire", annee,
                "chargeMax", e.getChargeMax(), "heuresAffectees", total,
                "heuresRestantes", e.getChargeMax() - total);
    }

    private void fill(Affectation a, AffectationRequest r) {
        if (r.enseignantId() == null || r.matiereId() == null || r.typeSeance() == null
                || r.heures() == null || r.anneeUniversitaire() == null)
            throw new BusinessException("VALIDATION", "Tous les champs de l'affectation sont obligatoires.");
        if (!r.anneeUniversitaire().matches("\\d{4}-\\d{4}"))
            throw new BusinessException("VALIDATION", "Année universitaire attendue au format 2025-2026.");
        a.setEnseignant(enseignants.get(r.enseignantId()));
        a.setMatiere(matieres.get(r.matiereId()));
        a.setTypeSeance(r.typeSeance());
        a.setHeures(r.heures());
        a.setAnneeUniversitaire(r.anneeUniversitaire());
    }

    private void check(Affectation a, int retire) { check(a, retire, retire, null); }

    private void check(Affectation a, int retireEns, int retireMat, Long selfId) {
        if (a.getHeures() <= 0)                                                          // RG4
            throw new BusinessException("RG4", "Le nombre d'heures doit être strictement positif.");

        Long ens = a.getEnseignant().getId(), mat = a.getMatiere().getId();
        String annee = a.getAnneeUniversitaire();

        boolean doublon = repo.findAll().stream().anyMatch(x ->                          // RG7
                !x.getId().equals(selfId)
                && x.getEnseignant().getId().equals(ens) && x.getMatiere().getId().equals(mat)
                && x.getTypeSeance() == a.getTypeSeance() && x.getAnneeUniversitaire().equals(annee));
        if (doublon)
            throw new BusinessException("RG7",
                    "Cette affectation (enseignant, matière, type, année) existe déjà.");

        int totalEns = repo.totalHeuresEnseignant(ens, annee) - retireEns + a.getHeures();
        int max = a.getEnseignant().getChargeMax();
        if (totalEns > max)                                                              // RG5
            throw new BusinessException("RG5", "Charge maximale dépassée : " + totalEns
                    + " h > " + max + " h autorisées pour le grade " + a.getEnseignant().getGrade() + ".");

        int totalMat = repo.totalHeuresMatiere(mat, annee) - retireMat + a.getHeures();
        int vol = a.getMatiere().getVolumeHoraire();
        if (totalMat > vol)                                                              // RG6
            throw new BusinessException("RG6", "Volume horaire de la matière dépassé : " + totalMat
                    + " h > " + vol + " h.");
    }
}
