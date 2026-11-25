package tn.univ.affectations.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.univ.affectations.model.Enseignant;
import tn.univ.affectations.service.EnseignantService;

@RestController
@RequestMapping("/api/enseignants")
public class EnseignantController {
    private final EnseignantService service;
    public EnseignantController(EnseignantService service) { this.service = service; }

    @GetMapping public List<Enseignant> all() { return service.findAll(); }
    @GetMapping("/{id}") public Enseignant one(@PathVariable Long id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Enseignant create(@Valid @RequestBody Enseignant e) { return service.create(e); }
    @PutMapping("/{id}")
    public Enseignant update(@PathVariable Long id, @Valid @RequestBody Enseignant e) { return service.update(id, e); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
