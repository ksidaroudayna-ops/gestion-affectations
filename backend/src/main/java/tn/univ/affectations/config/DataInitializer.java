package tn.univ.affectations.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.univ.affectations.model.Enseignant;
import tn.univ.affectations.model.Grade;
import tn.univ.affectations.model.Matiere;
import tn.univ.affectations.model.Role;
import tn.univ.affectations.model.Utilisateur;
import tn.univ.affectations.repo.EnseignantRepository;
import tn.univ.affectations.repo.MatiereRepository;
import tn.univ.affectations.repo.UtilisateurRepository;

@Configuration
@Profile("!test")
public class DataInitializer {
    @Bean
    CommandLineRunner seed(UtilisateurRepository users, PasswordEncoder enc,
                           EnseignantRepository ens, MatiereRepository mat) {
        return args -> {
            if (users.count() == 0) {
                users.save(new Utilisateur("admin", enc.encode("admin123"), Role.ADMIN));
                users.save(new Utilisateur("responsable", enc.encode("resp123"), Role.RESPONSABLE));
            }
            if (ens.count() == 0) {
                ens.save(ens("Ben Ali", "Sami", "sami.benali@univ.tn", Grade.PROFESSEUR));
                ens.save(ens("Trabelsi", "Leila", "leila.trabelsi@univ.tn", Grade.MAITRE_ASSISTANT));
                ens.save(ens("Gharbi", "Karim", "karim.gharbi@univ.tn", Grade.ASSISTANT));
            }
            if (mat.count() == 0) {
                mat.save(mat("JEE101", "Programmation JEE", 60, 1));
                mat.save(mat("BD201", "Bases de données avancées", 45, 1));
                mat.save(mat("GP301", "Gestion de projet", 30, 2));
            }
        };
    }
    private static Enseignant ens(String n, String p, String e, Grade g) {
        Enseignant x = new Enseignant(); x.setNom(n); x.setPrenom(p); x.setEmail(e); x.setGrade(g);
        x.setDepartement("Informatique"); return x;
    }
    private static Matiere mat(String c, String i, int v, int s) {
        Matiere m = new Matiere(); m.setCode(c); m.setIntitule(i); m.setVolumeHoraire(v); m.setSemestre(s); return m;
    }
}