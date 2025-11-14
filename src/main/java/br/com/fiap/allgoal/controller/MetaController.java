package br.com.fiap.allgoal.controller;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.MetaConcluidaRepository;
import br.com.fiap.allgoal.repository.UserRepository;
import br.com.fiap.allgoal.service.MetaConcluidaService;
import br.com.fiap.allgoal.service.MetaService;
import br.com.fiap.allgoal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/meta")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;
    private final UserRepository userRepository;
    private final MetaConcluidaRepository metaConcluidaRepository;
    private final UserService userService;
    private final MetaConcluidaService metaConcluidaService;
    private final MessageHelper messageHelper;

    @GetMapping("/dashboard")
    public String index(Model model, @AuthenticationPrincipal OAuth2User user) {
        String email = user.getAttribute("login") + "@github.com";
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        long metasConcluidas = metaConcluidaRepository.countByUsuarioAndStatus(usuario, Status.APROVADA);
        long ranking = userRepository.getRanking(usuario.getXpTotal());
        var metas = metaService.getMetasPendentes(usuario);
        long xpRestante = userService.getXpRestanteParaProximoNivel(usuario);

        model.addAttribute("user", usuario);
        model.addAttribute("metas", metas);
        model.addAttribute("metasConcluidas", metasConcluidas);
        model.addAttribute("ranking", ranking);
        model.addAttribute("xpRestante", xpRestante);
        return "index";
    }

    @PostMapping("/submeter")
    public String submeterMeta(
            @RequestParam("metaId") Long metaId,
            @RequestParam("justificativa") String justificativa,
            @AuthenticationPrincipal OAuth2User user,
            RedirectAttributes redirect) {

        try {
            String email = user.getAttribute("login") + "@github.com";
            User usuario = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            metaConcluidaService.submeterMeta(usuario.getIdUsuario(), metaId, justificativa);
            redirect.addFlashAttribute("message", messageHelper.get("metaconcluida.cromplete.success"));

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (e.getCause() instanceof org.hibernate.exception.GenericJDBCException) {
                errorMessage = ((org.hibernate.exception.GenericJDBCException) e.getCause())
                        .getSQLException().getMessage();
            }
            errorMessage = errorMessage.replaceAll("ORA-\\d+: ", "");
            redirect.addFlashAttribute("error", "Erro: " + errorMessage);
        }
        return "redirect:/meta/dashboard";
    }
}
