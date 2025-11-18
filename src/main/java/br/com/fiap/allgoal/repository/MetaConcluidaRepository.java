package br.com.fiap.allgoal.repository;

import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.model.Meta;
import br.com.fiap.allgoal.model.MetaConcluida;
import br.com.fiap.allgoal.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MetaConcluidaRepository extends JpaRepository<MetaConcluida, Long> {

    long countByUsuarioAndStatus(User usuario, Status status);

    List<MetaConcluida> findAllByStatus(Status status);

    @Modifying
    @Transactional
    @Procedure(
            procedureName = "pkg_gs_workflow.proc_insert_meta_concluida",
            outputParameterName = "p_id_meta_concluida_out"
    )
    Long submeterMeta(
            @Param("p_id_usuario") Long idUsuario,
            @Param("p_id_meta") Long idMeta,
            @Param("p_justificativa_texto") String justificativa
    );

    @Modifying
    @Transactional
    @Procedure(procedureName = "pkg_gs_workflow.proc_coletar_recompensa_meta")
    void coletarRecompensaMeta(
            @Param("p_id_meta_concluida") Long idMetaConcluida,
            @Param("p_id_usuario_sessao") Long idUsuarioSessao
    );

    List<MetaConcluida> findByUsuario(User usuario);

    @Modifying
    void deleteByMeta(Meta meta);

    long countByMetaAndStatusIn(Meta meta, List<Status> statuses);

    Page<MetaConcluida> findByUsuarioOrderByDataSubmissaoDesc(User usuario, Pageable pageable);
    Page<MetaConcluida> findByStatusOrderByDataSubmissaoAsc(Status status, Pageable pageable);
}
