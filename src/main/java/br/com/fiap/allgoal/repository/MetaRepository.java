package br.com.fiap.allgoal.repository;

import br.com.fiap.allgoal.model.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {

    @Query(value =
            "SELECT m.* FROM gs_meta m " +
                    "WHERE m.status_meta = 'ATIVA' " +
                    "AND NOT EXISTS (" +
                    "    SELECT 1 FROM gs_meta_concluida mc " +
                    "    WHERE mc.id_meta = m.id_meta " +
                    "    AND mc.id_usuario = :idUsuario " +
                    "    AND mc.status IN ('APROVADA', 'PENDENTE_APROVACAO')" +
                    ")",
            nativeQuery = true)
    List<Meta> findMetasPendentesPorUsuario(@Param("idUsuario") Long idUsuario);
}
