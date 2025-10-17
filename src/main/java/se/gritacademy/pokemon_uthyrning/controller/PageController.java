package se.gritacademy.pokemon_uthyrning.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.gritacademy.pokemon_uthyrning.model.Loan;
import se.gritacademy.pokemon_uthyrning.model.Person;
import se.gritacademy.pokemon_uthyrning.model.PokemonCard;
import se.gritacademy.pokemon_uthyrning.repository.LoanRepository;
import se.gritacademy.pokemon_uthyrning.repository.PersonRepository;
import se.gritacademy.pokemon_uthyrning.repository.PokemonCardRepository;

import java.time.LocalDateTime;

@Controller
public class PageController {

    private final PokemonCardRepository cardRepo;
    private final PersonRepository personRepo;
    private final LoanRepository loanRepo;

    public PageController(PokemonCardRepository cardRepo, PersonRepository personRepo, LoanRepository loanRepo) {
        this.cardRepo = cardRepo;
        this.personRepo = personRepo;
        this.loanRepo = loanRepo;
    }

    // ===== Persons page =====
    @GetMapping("/persons")
    public String showPersonsForm(Model model) {
        model.addAttribute("person", new Person()); // form-backing object
        model.addAttribute("persons", personRepo.findAll()); // existing persons
        return "persons";
    }

    @PostMapping("/persons")
    public String createPerson(Person person) {
        personRepo.save(person);
        return "redirect:/persons";
    }

    // ===== Cards page =====
    @GetMapping("/cards")
    public String showCards(Model model) {
        model.addAttribute("cards", cardRepo.findAll());
        model.addAttribute("persons", personRepo.findAll()); // populate dropdown
        return "cards";
    }

    // ===== Loan a card =====
    @PostMapping("/cards/loan")
    public String loanCard(@RequestParam Long cardId,
                           @RequestParam Long personId,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
                           RedirectAttributes redirectAttrs) {

        PokemonCard card = cardRepo.findById(cardId).orElse(null);
        Person person = personRepo.findById(personId).orElse(null);

        if (card == null || person == null) {
            redirectAttrs.addAttribute("error", true);
            return "redirect:/cards";
        }

        Loan loan = new Loan();
        loan.setCard(card);
        loan.setPerson(person);
        loan.setStartAt(startAt);
        loan.setEndAt(endAt);

        loanRepo.save(loan);
        redirectAttrs.addAttribute("success", true);
        return "redirect:/cards";
    }
}
