package br.com.fiap.allgoal.service;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.enums.StatusCompra;
import br.com.fiap.allgoal.enums.StatusMeta;
import br.com.fiap.allgoal.exception.CompraException;
import br.com.fiap.allgoal.model.CompraLoja;
import br.com.fiap.allgoal.model.Meta;
import br.com.fiap.allgoal.model.Recompensa;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.CompraLojaRepository;
import br.com.fiap.allgoal.repository.RecompensaRepository;
import br.com.fiap.allgoal.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;
    private final MessageHelper messageHelper;
    private final CompraLojaRepository compraLojaRepository;
    private final UserRepository userRepository;

    public RecompensaService(RecompensaRepository recompensaRepository, MessageHelper messageHelper, CompraLojaRepository compraLojaRepository, UserRepository userRepository) {
        this.recompensaRepository = recompensaRepository;
        this.messageHelper = messageHelper;
        this.compraLojaRepository = compraLojaRepository;
        this.userRepository = userRepository;
    }

    public List<Recompensa> getAllRecompensas() {
        return recompensaRepository.findAll();
    }

    public Recompensa save(Recompensa recompensa) {
        return recompensaRepository.save(recompensa);
    }

    @Transactional
    public void deleteById(Long id) {
        Recompensa recompensa = getRecompensa(id);
        List<CompraLoja> compras = compraLojaRepository.findAllByRecompensa(recompensa);

        for (CompraLoja compra : compras) {
            if (compra.getStatusCompra() == StatusCompra.COMPRADO ||
                    compra.getStatusCompra() == StatusCompra.SOLICITADO) {

                User usuario = compra.getUsuario();
                usuario.setMoedas(usuario.getMoedas() + compra.getCustoPago());
                userRepository.save(usuario);
            }
        }
        compraLojaRepository.deleteAll(compras);
        recompensaRepository.delete(recompensa);
    }

    @Transactional
    public void atualizarRecompensa(Long idRecompensa, String nome, String descricao, Integer estoque, Long custo) {
        try {
            Recompensa recompensa = getRecompensa(idRecompensa);
            recompensa.setNomeItem(nome);
            recompensa.setDescricao(descricao);
            recompensa.setEstoque(estoque);
            recompensa.setCustoMoedas(custo);
            recompensaRepository.save(recompensa);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar recompensa: " + limparMensagemOracle(e));
        }
    }

    public Page<Recompensa> getAllRecompensas(Pageable pageable) {
        return recompensaRepository.findAll(pageable);
    }

    public List<Recompensa> getRecompensasDisponiveis() {
        return recompensaRepository.findByEstoqueGreaterThan(0);
    }

    public void comprarRecompensa(Long idUsuario, Long idRecompensa) {
        try {
            recompensaRepository.comprarRecompensa(idUsuario, idRecompensa);

        } catch (Exception e) {
            String errorMessage = "Erro desconhecido ao processar a compra.";
            if (e.getCause() instanceof GenericJDBCException) {
                try {
                    errorMessage = ((GenericJDBCException) e.getCause())
                            .getSQLException().getMessage();
                    errorMessage = errorMessage.replaceAll("ORA-\\d+: ", "").trim();
                } catch (Exception ignored) { }
            }
            throw new CompraException(errorMessage);
        }
    }

    @Transactional
    public void criarRecompensa(String nome, String descricao, Integer estoque, Long custo) {
        try {
            recompensaRepository.inserirRecompensa(nome, descricao, custo, estoque);
        } catch (Exception e) {
            throw new RuntimeException(limparMensagemOracle(e));
        }
    }

    public Recompensa getRecompensa(Long id) {
        return recompensaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(messageHelper.get("recompensa.notfound"))
        );
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
}
