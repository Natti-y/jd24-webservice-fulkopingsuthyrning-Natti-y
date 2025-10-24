package se.gritacademy.pokemon_uthyrning;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import se.gritacademy.pokemon_uthyrning.model.PokemonCard;
import se.gritacademy.pokemon_uthyrning.repository.PokemonCardRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final PokemonCardRepository repo;

    public DataLoader(PokemonCardRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) throws Exception {
        repo.deleteAll();

        // Create Pokémon cards
        PokemonCard c1 = new PokemonCard();
        c1.setNamn("Charmander");
        c1.setBeskrivning("Fire type Pokémon");

        PokemonCard c2 = new PokemonCard();
        c2.setNamn("Bulbasaur");
        c2.setBeskrivning("Grass/Poison type Pokémon");

        PokemonCard c3 = new PokemonCard();
        c3.setNamn("Squirtle");
        c3.setBeskrivning("Water type Pokémon");

        //Save
        repo.save(c1);
        repo.save(c2);
        repo.save(c3);
    }
}
