package tn.univ.affectations.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "enseignant")
public class Enseignant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable = false, length = 80)
    private String nom;
    @NotBlank @Column(nullable = false, length = 80)
    private String prenom;
    @NotBlank @Email @Column(nullable = false, unique = true, length = 120)   // RG1
    private String email;
    @NotNull @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private Grade grade;
    @Column(name = "departement", length = 80)
    private String departement;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }
    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }
    public int getChargeMax() { return grade == null ? 0 : grade.getChargeMax(); }
}
