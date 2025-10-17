# Pokemon Uthyrning REST API

## Beskrivning
Detta är ett REST API byggt i Java med Spring Boot för att hantera uthyrning av Pokémon-kort.  
Projektet innehåller tre entiteter: `PokemonCard`, `Person` och `Loan` (relation mellan person och kort).  
API:et hanterar CRUD-operationer för alla entiteter och inkluderar validering, felhantering och Swagger/OpenAPI-dokumentation.

---

## Teknisk specifikation
- Java 17+
- Spring Boot 3+
- Maven
- JSON som dataformat (ingen XML används)
- Swagger/OpenAPI för dokumentation

---

## Installation och körning


Swagger UI finns på: http://localhost:8080/swagger-ui.html

API Endpoints
Personer
GET /api/persons – Hämta alla personer

GET /api/persons/{id} – Hämta person med ID

POST /api/persons – Skapa ny person

PUT /api/persons/{id} – Uppdatera person

DELETE /api/persons/{id} – Radera person

Pokémon-kort
GET /api/cards – Hämta alla kort

GET /api/cards/{id} – Hämta kort med ID

POST /api/cards – Skapa nytt kort

PUT /api/cards/{id} – Uppdatera kort

DELETE /api/cards/{id} – Radera kort

Uthyrningar (Loans)
GET /api/loans – Hämta alla lån

GET /api/loans/{id} – Hämta lån med ID

POST /api/loans – Skapa nytt lån

PUT /api/loans/{id} – Uppdatera lån

DELETE /api/loans/{id} – Radera lån

Validering och felhantering
400 Bad Request – Felaktig data eller logikfel, t.ex. startdatum efter slutdatum.

404 Not Found – Resursen finns inte, t.ex. uppdatera eller radera ett kort som inte finns.

Swagger/OpenAPI
API-dokumentation är tillgänglig via Swagger UI:
http://localhost:8080/swagger-ui.html
