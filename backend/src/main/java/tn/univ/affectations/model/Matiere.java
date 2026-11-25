package tn.univ.affectations.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "matiere")
public class Matiere {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable = false, unique = true, length = 20)   // RG2
    private String code;
    @NotBlank @Column(nullable = false, length = 150)
    private String intitule;
    @NotNull @Min(1) @Column(name = "volume_horaire", nullable = false)  // RG3
    private Integer volumeHoraire;
    @Min(1) @Max(2)
    private Integer semestre = 1;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    public Integer getVolumeHoraire() { return volumeHoraire; }
    public void setVolumeHoraire(Integer volumeHoraire) { this.volumeHoraire = volumeHoraire; }
    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
}
