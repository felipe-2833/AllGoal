package br.com.fiap.allgoal.repository;

import br.com.fiap.allgoal.model.Recompensa;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecompensaRepository extends JpaRepository<Recompensa, Long> {

    List<Recompensa> findByEstoqueGreaterThan(Integer estoque);

    @Modifying
    @Transactional
    @Procedure(procedureName = "pkg_gs_workflow.proc_comprar_recompensa")
    void comprarRecompensa(
            @Param("p_id_usuario") Long idUsuario,
            @Param("p_id_recompensa") Long idRecompensa
    );
}
