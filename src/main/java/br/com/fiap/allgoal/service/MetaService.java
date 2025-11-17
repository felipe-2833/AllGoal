package br.com.fiap.allgoal.service;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.enums.StatusMeta;
import br.com.fiap.allgoal.model.Meta;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.MetaConcluidaRepository;
import br.com.fiap.allgoal.repository.MetaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MetaService {

    private final MetaRepository metaRepository;
    private final MessageHelper messageHelper;
    private final MetaConcluidaRepository metaConcluidaRepository;

    public MetaService(MetaRepository metaRepository, MessageHelper messageHelper, MetaConcluidaRepository metaConcluidaRepository) {
        this.metaRepository = metaRepository;
        this.messageHelper = messageHelper;
        this.metaConcluidaRepository = metaConcluidaRepository;
    }

    public List<Meta> getAllMetas() {
        return metaRepository.findAll();
    }

    public Meta save(Meta meta) {
        return metaRepository.save(meta);
    }

    @Transactional
    public void deleteById(Long id) {
        Meta meta = getMeta(id);
        List<Status> statusBloqueantes = List.of(Status.APROVADA, Status.CONCLUIDA_E_COLETADA);
        long contagemConcluidos = metaConcluidaRepository.countByMetaAndStatusIn(meta, statusBloqueantes);

        if (contagemConcluidos > 0) {
            throw new RuntimeException(
                    messageHelper.get("meta.delete.exception")+ " " + contagemConcluidos + " " + messageHelper.get("users")
            );
        }

        metaConcluidaRepository.deleteByMeta(meta);
        metaRepository.delete(meta);
    }

    @Transactional
    public void atualizarMeta(Long idMeta, String titulo, String descricao, Integer xp, Integer moedas, String status) {
        try {
            Meta metaExistente = getMeta(idMeta);
            StatusMeta statusEnum = StatusMeta.valueOf(status);
            metaExistente.setTitulo(titulo);
            metaExistente.setDescricao(descricao);
            metaExistente.setXpRecompensa(xp);
            metaExistente.setMoedasRecompensa(moedas);
            metaExistente.setStatusMeta(statusEnum);
            metaRepository.save(metaExistente);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar meta: " + limparMensagemOracle(e));
        }
    }

    public List<Meta> getMetasPendentes(User usuario) {
        return metaRepository.findMetasPendentesPorUsuario(usuario.getIdUsuario());
    }

    @Transactional
    public void criarMeta(String titulo, String descricao, Integer xp, Integer moedas, String status) {
        try {
            metaRepository.inserirMeta(titulo, descricao, xp, moedas, status);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao chamar procedure: " + limparMensagemOracle(e));
        }
    }

    public Meta getMeta(Long id) {
        return metaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(messageHelper.get("meta.notfound"))
        );
    }

    private String limparMensagemOracle(Exception e) {
        Throwable cause = e.getCause();
        if (cause instanceof org.hibernate.exception.GenericJDBCException) {
            try {
                String errorMessage = ((org.hibernate.exception.GenericJDBCException) cause)
                        .getSQLException().getMessage();
                return errorMessage.replaceAll("ORA-\\d+: ", "").trim();
            } catch (Exception sqlEx) {
                return cause.getMessage();
            }
        }
        return e.getMessage();
    }
}
