package se.gritacademy.pokemon_uthyrning.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.gritacademy.pokemon_uthyrning.model.Loan;
import se.gritacademy.pokemon_uthyrning.model.Person;
import se.gritacademy.pokemon_uthyrning.model.PokemonCard;
import se.gritacademy.pokemon_uthyrning.repository.LoanRepository;
import se.gritacademy.pokemon_uthyrning.repository.PersonRepository;
import se.gritacademy.pokemon_uthyrning.repository.PokemonCardRepository;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UthyrningController {

    private final PokemonCardRepository cardRepo;
    private final PersonRepository personRepo;
    private final LoanRepository loanRepo;

    public UthyrningController(PokemonCardRepository cardRepo, PersonRepository personRepo, LoanRepository loanRepo) {
        this.cardRepo = cardRepo;
        this.personRepo = personRepo;
        this.loanRepo = loanRepo;
    }

    @GetMapping("/uthyrning")
    public String showUthyrningPage(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("persons", personRepo.findAll());
        model.addAttribute("cards", cardRepo.findAll());
        model.addAttribute("now", LocalDateTime.now());
        return "uthyrning";
    }

    @PostMapping("/uthyrning/person")
    public String createPerson(Person person, RedirectAttributes redirectAttrs) {
        personRepo.save(person);
        redirectAttrs.addFlashAttribute("success", "Ny person skapad!");
        return "redirect:/uthyrning";
    }

    @PostMapping("/uthyrning/loan")
    public String loanCard(@RequestParam Long cardId,
                           @RequestParam Long personId,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
                           RedirectAttributes redirectAttrs) {

        PokemonCard card = cardRepo.findById(cardId).orElse(null);
        Person person = personRepo.findById(personId).orElse(null);

        if (card == null || person == null) {
            redirectAttrs.addFlashAttribute("error", "Kunde inte skapa lån – kontrollera valen.");
            return "redirect:/uthyrning";
        }

        if (startAt.isBefore(LocalDateTime.now())) {
            redirectAttrs.addFlashAttribute("error", "Starttid kan inte vara bakåt i tiden!");
            return "redirect:/uthyrning";
        }

        if (startAt.isAfter(endAt)) {
            redirectAttrs.addFlashAttribute("error", "Starttid måste vara före sluttid!");
            return "redirect:/uthyrning";
        }

        Loan loan = new Loan();
        loan.setCard(card);
        loan.setPerson(person);
        loan.setStartAt(startAt);
        loan.setEndAt(endAt);
        loanRepo.save(loan);

        redirectAttrs.addFlashAttribute("success", "Lån registrerat!");
        return "redirect:/uthyrning";
    }

    @RestController
    @RequestMapping("/api/loans")
    public static class LoanRestController {

        private final LoanRepository loanRepo;
        private final PersonRepository personRepo;
        private final PokemonCardRepository cardRepo;

        public LoanRestController(LoanRepository loanRepo, PersonRepository personRepo, PokemonCardRepository cardRepo) {
            this.loanRepo = loanRepo;
            this.personRepo = personRepo;
            this.cardRepo = cardRepo;
        }

        @GetMapping
        public List<Loan> getAll() {
            return loanRepo.findAll();
        }

        @GetMapping("/{id}")
        public ResponseEntity<Loan> getById(@PathVariable Long id) {
            return loanRepo.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

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

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
            if (!loanRepo.existsById(id)) return ResponseEntity.notFound().build();
            loanRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }
}
