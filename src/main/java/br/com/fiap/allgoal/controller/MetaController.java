package br.com.fiap.allgoal.controller;

import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.MetaConcluidaRepository;
import br.com.fiap.allgoal.repository.UserRepository;
import br.com.fiap.allgoal.service.MetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/meta")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;
    private final UserRepository userRepository;
    private final MetaConcluidaRepository metaConcluidaRepository;

    @GetMapping("/dashboard")
    public String index(Model model, @AuthenticationPrincipal OAuth2User user) {
        var metas = metaService.getAllMetas();
        String email = user.getAttribute("login") + "@github.com";
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        long metasConcluidas = metaConcluidaRepository.countByUsuarioAndStatus(usuario, Status.APROVADA);
        long ranking = userRepository.getRanking(usuario.getXpTotal());

        model.addAttribute("user", usuario);
        model.addAttribute("metas", metas);
        model.addAttribute("metasConcluidas", metasConcluidas);
        model.addAttribute("ranking", ranking);
        return "index";
    }
}
