package se.gritacademy.pokemon_uthyrning.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.gritacademy.pokemon_uthyrning.model.Person;
import se.gritacademy.pokemon_uthyrning.repository.PersonRepository;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonRepository personRepo;

    public PersonController(PersonRepository personRepo) {
        this.personRepo = personRepo;
    }

    @Operation(summary = "Get all persons")
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Person> persons = personRepo.findAll();
        return ResponseEntity.ok(persons);
    }

    @Operation(summary = "Get a person by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return personRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found"));
    }

    @Operation(summary = "Create a new person")
    @PostMapping
    public ResponseEntity<?> createPerson(@Valid @RequestBody Person person) {
        Person saved = personRepo.save(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Update an existing person")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerson(@PathVariable Long id, @Valid @RequestBody Person updated) {
        return personRepo.findById(id)
                .map(existing -> {
                    existing.setNamn(updated.getNamn());
                    existing.setEmail(updated.getEmail());
                    Person saved = personRepo.save(existing);
                    return ResponseEntity.<Object>ok(saved); // <- här gör vi typen explicit
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found"));
    }

    @Operation(summary = "Delete a person")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable Long id) {
        if (!personRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found");
        }
        personRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
