package br.com.fiap.allgoal.service;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.enums.StatusCompra;
import br.com.fiap.allgoal.exception.CompraException;
import br.com.fiap.allgoal.model.CompraLoja;
import br.com.fiap.allgoal.model.MetaConcluida;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.CompraLojaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public CompraLoja save(CompraLoja compraLoja) {
        return compraLojaRepository.save(compraLoja);
    }

    public void solicitarResgate(Long idCompra, Long idUsuario) {
        try {
            compraLojaRepository.solicitarResgate(idCompra, idUsuario);
        } catch (Exception e) {
            throw new CompraException(limparMensagemOracle(e));
        }
    }

    public void concluirRecompensa(Long idCompra) {
        try {
            CompraLoja compraLoja = getCompraLoja(idCompra);
            compraLoja.setStatusCompra(StatusCompra.CONCLUIDO);
            compraLojaRepository.save(compraLoja);
        } catch (Exception e) {
            throw new CompraException(limparMensagemOracle(e));
        }
    }

    public void reembolsarCompra(Long idCompra, Long idUsuario) {
        try {
            compraLojaRepository.reembolsarCompra(idCompra, idUsuario);
        } catch (Exception e) {
            throw new CompraException(limparMensagemOracle(e));
        }
    }

    private String limparMensagemOracle(Exception e) {
        if (e.getCause() instanceof GenericJDBCException) {
            try {
                String errorMessage = ((GenericJDBCException) e.getCause()).getSQLException().getMessage();
                return errorMessage.replaceAll("ORA-\\d+: ", "").trim();
            } catch (Exception ignored) { }
        }
        return e.getMessage();
    }

    public Page<CompraLoja> getItensPorUsuario(User usuario, Pageable pageable) {
        return compraLojaRepository.findInventarioOrdenado(usuario, pageable);
    }

    public Page<CompraLoja> getAllComprasSolicitadas(Pageable pageable) {
        return compraLojaRepository.findAllByStatusCompra(StatusCompra.SOLICITADO, pageable);
    }

    public CompraLoja getCompraLoja(Long id) {
        return compraLojaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(messageHelper.get("compraLoja.notfound"))
        );
    }
}
