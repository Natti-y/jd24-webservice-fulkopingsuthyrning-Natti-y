package se.gritacademy.pokemon_uthyrning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.gritacademy.pokemon_uthyrning.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
