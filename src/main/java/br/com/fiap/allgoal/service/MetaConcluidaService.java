package br.com.fiap.allgoal.service;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.model.MetaConcluida;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.MetaConcluidaRepository;
import br.com.fiap.allgoal.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MetaConcluidaService {

    private final MetaConcluidaRepository metaConcluidaRepository;
    private final MessageHelper messageHelper;

    public MetaConcluidaService(MetaConcluidaRepository metaConcluidaRepository, MessageHelper messageHelper, UserService userService, UserRepository userRepository) {
        this.metaConcluidaRepository = metaConcluidaRepository;
        this.messageHelper = messageHelper;
    }

    public List<MetaConcluida> getAllMetaConcluidas() {
        return metaConcluidaRepository.findAll();
    }

    public List<MetaConcluida> getAllMetaPendentes(){
        return metaConcluidaRepository.findAllByStatus(Status.PENDENTE_APROVACAO);
    }

    public MetaConcluida save(MetaConcluida metaConcluida) {
        return metaConcluidaRepository.save(metaConcluida);
    }

    public void deleteById(Long id) {
        metaConcluidaRepository.delete(getMetaConcluida(id));
    }

    public void submeterMeta(Long idUsuario, Long idMeta, String justificativa) {
        metaConcluidaRepository.submeterMeta(idUsuario, idMeta, justificativa);
    }

    public void aprovarMeta(Long idMetaAprovada, String justificativa, String statusClicado){
        MetaConcluida meta = getMetaConcluida(idMetaAprovada);
        if(statusClicado.equals("APROVAR")){
            meta.setStatus(Status.APROVADA);
        }
        else {
            meta.setStatus(Status.REJEITADA);
        }
        meta.setJustificativaTexto(justificativa);
        meta.setDataAprovacao(LocalDate.now());
        metaConcluidaRepository.save(meta);
    }

    public Page<MetaConcluida> getHistoricoPorUsuario(User usuario, Pageable pageable) {
        return metaConcluidaRepository.findByUsuarioOrderByDataSubmissaoDesc(usuario, pageable);
    }

    public Page<MetaConcluida> getAllMetaPendentes(Pageable pageable) {
        return metaConcluidaRepository.findByStatusOrderByDataSubmissaoAsc(Status.PENDENTE_APROVACAO, pageable);
    }

    public void coletarRecompensa(Long metaConcluidaId, User usuario) {
        try {
            metaConcluidaRepository.coletarRecompensaMeta(metaConcluidaId, usuario.getIdUsuario());
        } catch (Exception e) {
            throw new RuntimeException(limparMensagemOracle(e));
        }
    }

    public List<MetaConcluida> getHistoricoPorUsuario(User usuario) {
        return metaConcluidaRepository.findByUsuario(usuario);
    }

    public MetaConcluida getMetaConcluida(Long id) {
        return metaConcluidaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(messageHelper.get("metaConcluida.notfound"))
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
