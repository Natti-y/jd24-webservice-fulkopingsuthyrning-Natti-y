package se.gritacademy.pokemon_uthyrning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.gritacademy.pokemon_uthyrning.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
