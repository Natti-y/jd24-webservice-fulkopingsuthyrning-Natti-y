package se.gritacademy.pokemon_uthyrning.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.gritacademy.pokemon_uthyrning.model.PokemonCard;
import se.gritacademy.pokemon_uthyrning.repository.PokemonCardRepository;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/api/cards")
public class PokemonCardController {

    private final PokemonCardRepository repo;

    public PokemonCardController(PokemonCardRepository repo) {
        this.repo = repo;
    }

    @Operation(summary = "Get all Pokémon cards")
    @GetMapping
    public List<PokemonCard> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Get a Pokémon card by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PokemonCard> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Create a new Pokémon card")
    @PostMapping
    public String createCard(@Valid PokemonCard card, RedirectAttributes redirectAttrs) {
        repo.save(card);
        redirectAttrs.addFlashAttribute("success", "Pokémonkort tillagt!");
        return "redirect:/uthyrning";
    }


    @Operation(summary = "Update an existing Pokémon card")
    @PutMapping("/{id}")
    public ResponseEntity<PokemonCard> update(@PathVariable Long id, @Valid @RequestBody PokemonCard updated) {
        return repo.findById(id).map(existing -> {
            existing.setNamn(updated.getNamn());
            existing.setBeskrivning(updated.getBeskrivning());
            repo.save(existing);
            return ResponseEntity.ok(existing);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Delete a Pokémon card")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
