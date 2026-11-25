package tn.univ.affectations.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.univ.affectations.model.Matiere;
import tn.univ.affectations.service.MatiereService;

@RestController
@RequestMapping("/api/matieres")
public class MatiereController {
    private final MatiereService service;
    public MatiereController(MatiereService service) { this.service = service; }

    @GetMapping public List<Matiere> all() { return service.findAll(); }
    @GetMapping("/{id}") public Matiere one(@PathVariable Long id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Matiere create(@Valid @RequestBody Matiere m) { return service.create(m); }
    @PutMapping("/{id}")
    public Matiere update(@PathVariable Long id, @Valid @RequestBody Matiere m) { return service.update(id, m); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
