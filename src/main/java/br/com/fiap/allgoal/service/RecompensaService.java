package br.com.fiap.allgoal.service;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.model.Recompensa;
import br.com.fiap.allgoal.repository.RecompensaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;
    private final MessageHelper messageHelper;

    public RecompensaService(RecompensaRepository recompensaRepository, MessageHelper messageHelper) {
        this.recompensaRepository = recompensaRepository;
        this.messageHelper = messageHelper;
    }

    public List<Recompensa> getAllRecompensas() {
        return recompensaRepository.findAll();
    }

    public Recompensa save(Recompensa recompensa) {
        return recompensaRepository.save(recompensa);
    }

    public void deleteById(Long id) {
        recompensaRepository.delete(getRecompensa(id));
    }

    public List<Recompensa> getRecompensasDisponiveis() {
        return recompensaRepository.findByEstoqueGreaterThan(0);
    }

    public Recompensa getRecompensa(Long id) {
        return recompensaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(messageHelper.get("recompensa.notfound"))
        );
    }
}
