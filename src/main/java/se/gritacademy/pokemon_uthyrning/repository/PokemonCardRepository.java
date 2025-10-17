package se.gritacademy.pokemon_uthyrning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.gritacademy.pokemon_uthyrning.model.PokemonCard;

public interface PokemonCardRepository extends JpaRepository<PokemonCard, Long> {
}
