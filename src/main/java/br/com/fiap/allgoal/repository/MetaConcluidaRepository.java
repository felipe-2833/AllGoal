package br.com.fiap.allgoal.repository;

import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.model.MetaConcluida;
import br.com.fiap.allgoal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetaConcluidaRepository extends JpaRepository<MetaConcluida, Long> {

    long countByUsuarioAndStatus(User usuario, Status status);
}
