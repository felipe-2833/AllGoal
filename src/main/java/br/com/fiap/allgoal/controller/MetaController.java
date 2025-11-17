package br.com.fiap.allgoal.controller;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.model.Meta;
import br.com.fiap.allgoal.model.MetaConcluida;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
        User usuario = getUsuarioLogado(user);
        long metasConcluidas = metaConcluidaRepository.countByUsuarioAndStatus(usuario, Status.CONCLUIDA_E_COLETADA);
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
            User usuario = getUsuarioLogado(user);

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

    @GetMapping
    public String meta(Model model, @AuthenticationPrincipal OAuth2User user) {
        User usuario = getUsuarioLogado(user);
        var metas = metaService.getMetasPendentes(usuario);
        List<MetaConcluida> metasHistorico = metaConcluidaService.getHistoricoPorUsuario(usuario);

        model.addAttribute("metasHistorico", metasHistorico);
        model.addAttribute("user", usuario);
        model.addAttribute("metas", metas);
        return "meta";
    }

    @PostMapping("/submeter2")
    public String submeterMeta2(
            @RequestParam("metaId") Long metaId,
            @RequestParam("justificativa") String justificativa,
            @AuthenticationPrincipal OAuth2User user,
            RedirectAttributes redirect) {

        try {
            User usuario = getUsuarioLogado(user);

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
        return "redirect:/meta";
    }

    @GetMapping("/adm")
    public String metaAdm(Model model, @AuthenticationPrincipal OAuth2User user) {
        User usuario = getUsuarioLogado(user);
        var metas = metaService.getAllMetas();
        List<MetaConcluida> metasPendentes = metaConcluidaService.getAllMetaPendentes();

        model.addAttribute("metasPendentes", metasPendentes);
        model.addAttribute("user", usuario);
        model.addAttribute("metas", metas);
        return "adm-metas";
    }

    @PostMapping("/confirmaMeta")
    public String confirmaMeta(
            @RequestParam("metaId") Long metaId,
            @RequestParam("justificativa") String justificativa,
            @RequestParam("status") String statusClicado,
            RedirectAttributes redirect) {

        try {

            metaConcluidaService.aprovarMeta(metaId, justificativa,statusClicado);
            redirect.addFlashAttribute("message", messageHelper.get("metaconcluida.avaliated.success"));

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (e.getCause() instanceof org.hibernate.exception.GenericJDBCException) {
                errorMessage = ((org.hibernate.exception.GenericJDBCException) e.getCause())
                        .getSQLException().getMessage();
            }
            errorMessage = errorMessage.replaceAll("ORA-\\d+: ", "");
            redirect.addFlashAttribute("error", "Erro: " + errorMessage);
        }
        return "redirect:/meta/adm";
    }

    @PostMapping("/refazer")
    public String deleteMetaConcluida(@RequestParam("metaConcluidaId") Long metaConcluidaId, RedirectAttributes redirect ){
        metaConcluidaService.deleteById(metaConcluidaId);
        redirect.addFlashAttribute("message", messageHelper.get("meta.remake"));
        return "redirect:/meta";
    }

    @PostMapping("/coletar")
    public String ColetarRecompensa(
            @RequestParam("metaConcluidaId") Long metaId,
            @AuthenticationPrincipal OAuth2User principal,
            RedirectAttributes redirect) {

        try {
            String email = principal.getAttribute("login") + "@github.com";
            User usuario = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            metaConcluidaService.coletarRecompensa(metaId, usuario);

            redirect.addFlashAttribute("message", messageHelper.get("meta.collect"));

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao coletar: " + e.getMessage());
        }
        return "redirect:/meta";
    }

    private User getUsuarioLogado(OAuth2User principal) {
        String email = principal.getAttribute("login") + "@github.com";
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
