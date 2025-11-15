package br.com.fiap.allgoal.repository;

import br.com.fiap.allgoal.model.CompraLoja;
import br.com.fiap.allgoal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraLojaRepository extends JpaRepository<CompraLoja, Long> {

    List<CompraLoja> findByUsuario(User usuario);

}
