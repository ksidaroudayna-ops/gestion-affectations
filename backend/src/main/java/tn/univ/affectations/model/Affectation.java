package tn.univ.affectations.model;

import jakarta.persistence.*;

@Entity
@Table(name = "affectation",
       uniqueConstraints = @UniqueConstraint(   // RG7
           columnNames = {"enseignant_id", "matiere_id", "type_seance", "annee_universitaire"}))
public class Affectation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.EAGER) @JoinColumn(name = "enseignant_id")
    private Enseignant enseignant;
    @ManyToOne(optional = false, fetch = FetchType.EAGER) @JoinColumn(name = "matiere_id")
    private Matiere matiere;
    @Enumerated(EnumType.STRING) @Column(name = "type_seance", nullable = false, length = 5)
    private TypeSeance typeSeance;
    @Column(nullable = false)
    private Integer heures;
    @Column(name = "annee_universitaire", nullable = false, length = 9)
    private String anneeUniversitaire;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Enseignant getEnseignant() { return enseignant; }
    public void setEnseignant(Enseignant enseignant) { this.enseignant = enseignant; }
    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }
    public TypeSeance getTypeSeance() { return typeSeance; }
    public void setTypeSeance(TypeSeance typeSeance) { this.typeSeance = typeSeance; }
    public Integer getHeures() { return heures; }
    public void setHeures(Integer heures) { this.heures = heures; }
    public String getAnneeUniversitaire() { return anneeUniversitaire; }
    public void setAnneeUniversitaire(String a) { this.anneeUniversitaire = a; }
}
