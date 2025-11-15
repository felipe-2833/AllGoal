package br.com.fiap.allgoal.tools;

import br.com.fiap.allgoal.model.Meta;
import br.com.fiap.allgoal.model.MetaConcluida;
import br.com.fiap.allgoal.model.Recompensa;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.UserRepository;
import br.com.fiap.allgoal.service.CompraLojaService;
import br.com.fiap.allgoal.service.MetaConcluidaService;
import br.com.fiap.allgoal.service.MetaService;
import br.com.fiap.allgoal.service.RecompensaService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PerformanceTools {

    private final MetaService metaService;
    private final RecompensaService recompensaService;
    private final UserRepository userRepository;
    private final MetaConcluidaService metaConcluidaService;

    @Tool(name = "getMetasPendentes", description = "Obtém a lista de todas as metas que o funcionário ainda não submeteu")
    public List<Meta> getMetasPendentes() {
        User usuario = getUsuarioLogado();
        return metaService.getMetasPendentes(usuario);
    }

    @Tool(name = "getHistoricoMetas", description = "Obtém o histórico de metas já submetidas (aprovadas, pendentes, rejeitadas)")
    public List<MetaConcluida> getHistoricoMetas() {
        User usuario = getUsuarioLogado();
        return metaConcluidaService.getHistoricoPorUsuario(usuario);
    }

    @Tool(name = "getItensDaLoja", description = "Obtém a lista de todas as recompensas disponíveis para compra na loja")
    public List<Recompensa> getItensDaLoja() {
        return recompensaService.getRecompensasDisponiveis();
    }

    @Tool(name = "getStatusAtual", description = "Obtém o nível, XP total e moedas atuais do funcionário")
    public User getStatusAtual() {
        return getUsuarioLogado();
    }

    private User getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Utilizador não autenticado.");
        }

        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("login") + "@github.com";

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado na sessão."));
    }
}