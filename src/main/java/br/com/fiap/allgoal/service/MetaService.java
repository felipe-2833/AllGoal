package br.com.fiap.allgoal.service;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.model.Meta;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.MetaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaService {

    private final MetaRepository metaRepository;
    private final MessageHelper messageHelper;

    public MetaService(MetaRepository metaRepository, MessageHelper messageHelper) {
        this.metaRepository = metaRepository;
        this.messageHelper = messageHelper;
    }

    public List<Meta> getAllMetas() {
        return metaRepository.findAll();
    }

    public Meta save(Meta meta) {
        return metaRepository.save(meta);
    }

    public void deleteById(Long id) {
        metaRepository.delete(getMeta(id));
    }

    public List<Meta> getMetasPendentes(User usuario) {
        return metaRepository.findMetasPendentesPorUsuario(usuario.getIdUsuario());
    }

    public Meta getMeta(Long id) {
        return metaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(messageHelper.get("meta.notfound"))
        );
    }
}
