package br.com.fiap.allgoal.repository;

import br.com.fiap.allgoal.model.Recompensa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecompensaRepository extends JpaRepository<Recompensa, Long> {

    List<Recompensa> findByEstoqueGreaterThan(Integer estoque);
}
