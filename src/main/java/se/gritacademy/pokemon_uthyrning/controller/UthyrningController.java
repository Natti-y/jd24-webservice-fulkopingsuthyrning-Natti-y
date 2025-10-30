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

import java.time.LocalDate;
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

        if (startAt.toLocalDate().isBefore(LocalDate.now())) {
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

    @PostMapping("/uthyrning/person/delete/{id}")
    public String deletePerson(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        if (!personRepo.existsById(id)) {
            redirectAttrs.addFlashAttribute("error", "Person kunde inte hittas!");
        } else {
            personRepo.deleteById(id);
            redirectAttrs.addFlashAttribute("success", "Person borttagen!");
        }
        return "redirect:/uthyrning";
    }

}
