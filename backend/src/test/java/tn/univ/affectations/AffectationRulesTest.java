package tn.univ.affectations;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tn.univ.affectations.exception.BusinessException;
import tn.univ.affectations.model.*;
import tn.univ.affectations.service.*;
import tn.univ.affectations.service.AffectationService.AffectationRequest;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AffectationRulesTest {
    @Autowired EnseignantService enseignants;
    @Autowired MatiereService matieres;
    @Autowired AffectationService affectations;

    Enseignant ens(String email, Grade g) {
        Enseignant e = new Enseignant(); e.setNom("N"); e.setPrenom("P"); e.setEmail(email); e.setGrade(g);
        return enseignants.create(e);
    }
    Matiere mat(String code, int vol) {
        Matiere m = new Matiere(); m.setCode(code); m.setIntitule("I"); m.setVolumeHoraire(vol);
        return matieres.create(m);
    }
    AffectationRequest req(Enseignant e, Matiere m, TypeSeance t, int h) {
        return new AffectationRequest(e.getId(), m.getId(), t, h, "2025-2026");
    }
    String rule(Runnable r) { return assertThrows(BusinessException.class, r::run).getRule(); }

    @Test void rg1_emailUnique() {
        ens("a@x.tn", Grade.ASSISTANT);
        assertEquals("RG1", rule(() -> ens("A@x.tn", Grade.ASSISTANT)));
    }
    @Test void rg2_codeUnique() {
        mat("M1", 10);
        assertEquals("RG2", rule(() -> mat("m1", 10)));
    }
    @Test void rg4_heuresPositives() {
        var e = ens("b@x.tn", Grade.ASSISTANT); var m = mat("M2", 10);
        assertEquals("RG4", rule(() -> affectations.create(req(e, m, TypeSeance.CM, 0))));
    }
    @Test void rg5_chargeMax() {
        var e = ens("c@x.tn", Grade.VACATAIRE);           // 120 h max
        var m1 = mat("M3", 100); var m2 = mat("M4", 100);
        affectations.create(req(e, m1, TypeSeance.CM, 100));
        assertEquals("RG5", rule(() -> affectations.create(req(e, m2, TypeSeance.CM, 30))));
        affectations.create(req(e, m2, TypeSeance.CM, 20)); // exactement 120 : accepté
    }
    @Test void rg6_volumeMatiere() {
        var e1 = ens("d@x.tn", Grade.PROFESSEUR); var e2 = ens("e@x.tn", Grade.PROFESSEUR);
        var m = mat("M5", 40);
        affectations.create(req(e1, m, TypeSeance.CM, 30));
        assertEquals("RG6", rule(() -> affectations.create(req(e2, m, TypeSeance.TD, 11))));
    }
    @Test void rg7_doublon() {
        var e = ens("f@x.tn", Grade.PROFESSEUR); var m = mat("M6", 40);
        affectations.create(req(e, m, TypeSeance.CM, 10));
        assertEquals("RG7", rule(() -> affectations.create(req(e, m, TypeSeance.CM, 5))));
    }
    @Test void rg8_rg9_suppression() {
        var e = ens("g@x.tn", Grade.PROFESSEUR); var m = mat("M7", 40);
        var a = affectations.create(req(e, m, TypeSeance.CM, 10));
        assertEquals("RG8", rule(() -> enseignants.delete(e.getId())));
        assertEquals("RG9", rule(() -> matieres.delete(m.getId())));
        affectations.delete(a.getId());
        enseignants.delete(e.getId()); matieres.delete(m.getId());
    }
    @Test void modification_ne_compte_pas_deux_fois() {
        var e = ens("h@x.tn", Grade.VACATAIRE); var m = mat("M8", 200);
        var a = affectations.create(req(e, m, TypeSeance.CM, 100));
        affectations.update(a.getId(), req(e, m, TypeSeance.CM, 120)); // 120 <= 120
        assertEquals(0, ((Number) affectations.charge(e.getId(), "2025-2026").get("heuresRestantes")).intValue());
    }
}
