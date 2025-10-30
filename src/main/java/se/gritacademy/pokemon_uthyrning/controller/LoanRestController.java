package se.gritacademy.pokemon_uthyrning.controller;

import io.swagger.v3.oas.annotations.Operation;
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
public class LoanRestController {

    private final LoanRepository loanRepo;
    private final PersonRepository personRepo;
    private final PokemonCardRepository cardRepo;

    public LoanRestController(LoanRepository loanRepo, PersonRepository personRepo, PokemonCardRepository cardRepo) {
        this.loanRepo = loanRepo;
        this.personRepo = personRepo;
        this.cardRepo = cardRepo;
    }

    @Operation(summary = "Get all loans")
    @GetMapping
    public List<Loan> getAll() {
        return loanRepo.findAll();
    }

    @Operation(summary = "Get a loan by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getById(@PathVariable Long id) {
        return loanRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new loan")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Loan loan) {
        Person person = personRepo.findById(loan.getPerson().getId()).orElse(null);
        if (person == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Person not found");

        PokemonCard card = cardRepo.findById(loan.getCard().getId()).orElse(null);
        if (card == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pokemon card not found");

        if (loan.getStartAt().isAfter(loan.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
        }

        if (loan.getStartAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date cannot be in the past");
        }

        loan.setPerson(person);
        loan.setCard(card);
        Loan savedLoan = loanRepo.save(loan);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLoan);
    }

    @Operation(summary = "Update an existing loan")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Loan updated) {
        return loanRepo.findById(id).map(existing -> {
            if (updated.getStartAt().isAfter(updated.getEndAt())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
            }

            if (updated.getStartAt().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date cannot be in the past");
            }

            existing.setStartAt(updated.getStartAt());
            existing.setEndAt(updated.getEndAt());

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

    @Operation(summary = "Delete a loan by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!loanRepo.existsById(id)) return ResponseEntity.notFound().build();
        loanRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
