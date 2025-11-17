package br.com.fiap.allgoal.repository;

import br.com.fiap.allgoal.enums.StatusCompra;
import br.com.fiap.allgoal.model.CompraLoja;
import br.com.fiap.allgoal.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompraLojaRepository extends JpaRepository<CompraLoja, Long> {

    List<CompraLoja> findByUsuario(User usuario);

    List<CompraLoja> findAllByStatusCompra(StatusCompra status);

    @Modifying
    @Transactional
    @Procedure(procedureName = "pkg_gs_workflow.proc_solicitar_resgate")
    void solicitarResgate(
            @Param("p_id_compra") Long idCompra,
            @Param("p_id_usuario_sessao") Long idUsuarioSessao
    );

    @Modifying
    @Transactional
    @Procedure(procedureName = "pkg_gs_workflow.proc_reembolsar_compra")
    void reembolsarCompra(
            @Param("p_id_compra") Long idCompra,
            @Param("p_id_usuario_sessao") Long idUsuarioSessao
    );
}
