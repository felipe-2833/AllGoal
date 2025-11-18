package br.com.fiap.allgoal.repository;

import br.com.fiap.allgoal.model.Recompensa;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Modifying
    @Transactional
    @Procedure(procedureName = "pkg_gs_admin.proc_insert_recompensa", outputParameterName = "p_id_recompensa_out")
    Long inserirRecompensa(
            @Param("p_nome_item") String nomeItem,
            @Param("p_descricao") String descricao,
            @Param("p_custo_moedas") Long custoMoedas,
            @Param("p_estoque") Integer estoque
    );

    Page<Recompensa> findAll(Pageable pageable);
}
