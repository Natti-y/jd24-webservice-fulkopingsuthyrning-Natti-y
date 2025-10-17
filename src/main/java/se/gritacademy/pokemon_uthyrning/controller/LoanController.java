package se.gritacademy.pokemon_uthyrning.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.gritacademy.pokemon_uthyrning.model.Loan;
import se.gritacademy.pokemon_uthyrning.model.Person;
import se.gritacademy.pokemon_uthyrning.model.PokemonCard;
import se.gritacademy.pokemon_uthyrning.repository.LoanRepository;
import se.gritacademy.pokemon_uthyrning.repository.PersonRepository;
import se.gritacademy.pokemon_uthyrning.repository.PokemonCardRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanRepository loanRepo;
    private final PersonRepository personRepo;
    private final PokemonCardRepository cardRepo;

    public LoanController(LoanRepository loanRepo, PersonRepository personRepo, PokemonCardRepository cardRepo) {
        this.loanRepo = loanRepo;
        this.personRepo = personRepo;
        this.cardRepo = cardRepo;
    }

    // ===== GET all loans =====
    @GetMapping
    public List<Loan> getAll() {
        return loanRepo.findAll();
    }

    // ===== GET loan by ID =====
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getById(@PathVariable Long id) {
        return loanRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== POST create new loan =====
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Loan loan) {
        // Check if person exists
        Person person = personRepo.findById(loan.getPerson().getId())
                .orElse(null);
        if (person == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Person not found");

        // Check if card exists
        PokemonCard card = cardRepo.findById(loan.getCard().getId())
                .orElse(null);
        if (card == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pokemon card not found");

        // Validate dates
        if (loan.getStartAt().isAfter(loan.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
        }

        loan.setPerson(person);
        loan.setCard(card);

        Loan savedLoan = loanRepo.save(loan);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLoan);
    }

    // ===== PUT update existing loan =====
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Loan updated) {
        return loanRepo.findById(id).map(existing -> {
            // Validate dates
            if (updated.getStartAt().isAfter(updated.getEndAt())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
            }

            // Update fields
            existing.setStartAt(updated.getStartAt());
            existing.setEndAt(updated.getEndAt());

            // Optionally update person and card if IDs are provided
            if (updated.getPerson() != null) {
                Person person = personRepo.findById(updated.getPerson().getId()).orElse(null);
                if (person == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Person not found");
                existing.setPerson(person);
            }

            if (updated.getCard() != null) {
                PokemonCard card = cardRepo.findById(updated.getCard().getId()).orElse(null);
                if (card == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pokemon card not found");
                existing.setCard(card);
            }

            return ResponseEntity.ok(loanRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ===== DELETE loan =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!loanRepo.existsById(id)) return ResponseEntity.notFound().build();
        loanRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
