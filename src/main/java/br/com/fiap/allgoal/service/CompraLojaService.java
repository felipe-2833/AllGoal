package br.com.fiap.allgoal.service;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.model.CompraLoja;
import br.com.fiap.allgoal.repository.CompraLojaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraLojaService {

    private final CompraLojaRepository compraLojaRepository;
    private final MessageHelper messageHelper;

    public CompraLojaService(CompraLojaRepository compraLojaRepository, MessageHelper messageHelper) {
        this.compraLojaRepository = compraLojaRepository;
        this.messageHelper = messageHelper;
    }

    public List<CompraLoja> getAllCompraLojas() {
        return compraLojaRepository.findAll();
    }

    public CompraLoja save(CompraLoja compraLoja) {
        return compraLojaRepository.save(compraLoja);
    }

    public void deleteById(Long id) {
        compraLojaRepository.delete(getCompraLoja(id));
    }

    public CompraLoja getCompraLoja(Long id) {
        return compraLojaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(messageHelper.get("compraLoja.notfound"))
        );
    }
}
