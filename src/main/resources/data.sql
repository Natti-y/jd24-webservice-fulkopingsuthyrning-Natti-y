CREATE TABLE person (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        namn VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE pokemon_card (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              namn VARCHAR(255) NOT NULL,
                              beskrivning VARCHAR(255)
);

CREATE TABLE loan (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      person_id BIGINT NOT NULL,
                      card_id BIGINT NOT NULL,
                      start_at TIMESTAMP NOT NULL,
                      end_at TIMESTAMP NOT NULL,
                      FOREIGN KEY (person_id) REFERENCES person(id),
                      FOREIGN KEY (card_id) REFERENCES pokemon_card(id)
);


-- Persons
INSERT INTO person (namn, email) VALUES ('Alice', 'alice@example.com');
INSERT INTO person (namn, email) VALUES ('Bob', 'bob@example.com');

-- Pokémon cards
INSERT INTO pokemon_card (namn, beskrivning) VALUES ('Bulbasaur', 'Grass/Poison type Pokémon');
INSERT INTO pokemon_card (namn, beskrivning) VALUES ('Charmander', 'Fire type Pokémon');
