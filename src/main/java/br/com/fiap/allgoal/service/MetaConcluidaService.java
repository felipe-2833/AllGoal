package br.com.fiap.allgoal.service;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.model.MetaConcluida;
import br.com.fiap.allgoal.repository.MetaConcluidaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaConcluidaService {

    private final MetaConcluidaRepository metaConcluidaRepository;
    private final MessageHelper messageHelper;

    public MetaConcluidaService(MetaConcluidaRepository metaConcluidaRepository, MessageHelper messageHelper) {
        this.metaConcluidaRepository = metaConcluidaRepository;
        this.messageHelper = messageHelper;
    }

    public List<MetaConcluida> getAllMetaConcluidas() {
        return metaConcluidaRepository.findAll();
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

    public MetaConcluida getMetaConcluida(Long id) {
        return metaConcluidaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(messageHelper.get("metaConcluida.notfound"))
        );
    }

}
