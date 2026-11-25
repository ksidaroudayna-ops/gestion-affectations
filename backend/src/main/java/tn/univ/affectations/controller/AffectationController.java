package tn.univ.affectations.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.univ.affectations.model.Affectation;
import tn.univ.affectations.service.AffectationService;
import tn.univ.affectations.service.AffectationService.AffectationRequest;

@RestController
@RequestMapping("/api/affectations")
public class AffectationController {
    private final AffectationService service;
    public AffectationController(AffectationService service) { this.service = service; }

    @GetMapping public List<Affectation> all() { return service.findAll(); }
    @GetMapping("/{id}") public Affectation one(@PathVariable Long id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Affectation create(@RequestBody AffectationRequest r) { return service.create(r); }
    @PutMapping("/{id}")
    public Affectation update(@PathVariable Long id, @RequestBody AffectationRequest r) { return service.update(id, r); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

    /** Charge horaire d'un enseignant : GET /api/affectations/charge?enseignantId=1&annee=2025-2026 */
    @GetMapping("/charge")
    public Map<String, Object> charge(@RequestParam Long enseignantId, @RequestParam String annee) {
        return service.charge(enseignantId, annee);
    }
}
