package br.com.fiap.allgoal.repository;

import br.com.fiap.allgoal.model.Meta;
import br.com.fiap.allgoal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {

    @Query(value =
            "SELECT m.* FROM gs_meta m " +
                    "WHERE m.status_meta = 'ATIVA' " +
                    "AND NOT EXISTS (" +
                    "    SELECT 1 FROM gs_meta_concluida mc " +
                    "    WHERE mc.id_meta = m.id_meta " +
                    "    AND mc.id_usuario = :idUsuario " +
                    "    AND mc.status IN ('APROVADA', 'PENDENTE_APROVACAO', 'REJEITADA', 'CONCLUIDA_E_COLETADA')" +
                    ")",
            nativeQuery = true)
    List<Meta> findMetasPendentesPorUsuario(@Param("idUsuario") Long idUsuario);

    @Modifying
    @Transactional
    @Procedure(procedureName = "pkg_gs_admin.proc_insert_meta",
            outputParameterName = "p_id_meta_out"
    )
    Long inserirMeta(
            @Param("p_titulo") String titulo,
            @Param("p_descricao") String descricao,
            @Param("p_xp_recompensa") Integer xp,
            @Param("p_moedas_recompensa") Integer moedas,
            @Param("p_status_meta") String status
    );

    @Query("SELECT m FROM Meta m WHERE m.statusMeta = 'ATIVA' " +
            "AND NOT EXISTS (SELECT mc FROM MetaConcluida mc WHERE mc.meta = m AND mc.usuario = :usuario) " +
            "ORDER BY m.xpRecompensa DESC")
    Page<Meta> findMetasPendentesParaUsuario(@Param("usuario") User usuario, Pageable pageable);
}
