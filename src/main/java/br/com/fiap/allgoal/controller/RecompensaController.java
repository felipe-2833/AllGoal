package br.com.fiap.allgoal.controller;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.enums.StatusCompra;
import br.com.fiap.allgoal.exception.CompraException;
import br.com.fiap.allgoal.model.CompraLoja;
import br.com.fiap.allgoal.model.MetaConcluida;
import br.com.fiap.allgoal.model.Recompensa;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.RecompensaRepository;
import br.com.fiap.allgoal.repository.UserRepository;
import br.com.fiap.allgoal.service.CompraLojaService;
import br.com.fiap.allgoal.service.RecompensaService;
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

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/loja")
@RequiredArgsConstructor
public class RecompensaController {

    private final UserRepository userRepository;
    private final RecompensaService recompensaService;
    private final MessageHelper messageHelper;
    private final CompraLojaService compraLojaService;

    @GetMapping
    public String loja(Model model, @AuthenticationPrincipal OAuth2User user) {
        User usuario = getUsuarioLogado(user);
        List<Recompensa> recompensas = recompensaService.getRecompensasDisponiveis();

        model.addAttribute("recompensas", recompensas);
        model.addAttribute("user", usuario);
        return "loja";
    }

    @PostMapping("/comprar")
    public String comprarItem(
            @RequestParam("recompensaId") Long recompensaId,
            @AuthenticationPrincipal OAuth2User principal,
            RedirectAttributes redirect) {

        try {
            User usuario = getUsuarioLogado(principal);
            recompensaService.comprarRecompensa(usuario.getIdUsuario(), recompensaId);
            redirect.addFlashAttribute("message", messageHelper.get("compra.sucesso"));

        } catch (CompraException e) {
            redirect.addFlashAttribute("error", messageHelper.get("erro") + ": " + e.getMessage());

        } catch (Exception e) {
            redirect.addFlashAttribute("error", messageHelper.get("erro.inesperado"));
        }

        return "redirect:/loja";
    }

    @GetMapping("/inventario")
    public String inventario(Model model, @AuthenticationPrincipal OAuth2User user) {
        User usuario = getUsuarioLogado(user);
        List<CompraLoja> compras = compraLojaService.getItensPorUsuario(usuario);

        model.addAttribute("compras", compras);
        model.addAttribute("listastatus", StatusCompra.values());
        model.addAttribute("user", usuario);
        return "inventario";
    }

    @PostMapping("/solicitar")
    public String SolicitarRecompensa(
            @RequestParam("compraId") Long compraId,
            RedirectAttributes redirect) {
            CompraLoja compra = compraLojaService.getCompraLoja(compraId);

            compra.setStatusCompra(StatusCompra.SOLICITADO);
            compraLojaService.save(compra);

            redirect.addFlashAttribute("message", messageHelper.get("solicitado.sucesso"));

        return "redirect:/loja/inventario";
    }

    @PostMapping("/inventario/solicitar")
    public String solicitarResgate(@RequestParam("compraId") Long compraId,
                                   @AuthenticationPrincipal OAuth2User user,
                                   RedirectAttributes redirect) {
        try {
            User usuario = getUsuarioLogado(user);
            compraLojaService.solicitarResgate(compraId, usuario.getIdUsuario());
            redirect.addFlashAttribute("message", messageHelper.get("inventario.solicitado.sucesso"));
        } catch (CompraException e) {
            redirect.addFlashAttribute("error", messageHelper.get("erro") + ": " + e.getMessage());
        }
        return "redirect:/loja/inventario";
    }

    @PostMapping("/inventario/reembolsar")
    public String reembolsarCompra(@RequestParam("compraId") Long compraId,
                                   @AuthenticationPrincipal OAuth2User user,
                                   RedirectAttributes redirect) {
        try {
            User usuario = getUsuarioLogado(user);
            compraLojaService.reembolsarCompra(compraId, usuario.getIdUsuario());
            redirect.addFlashAttribute("message", messageHelper.get("inventario.reembolso.sucesso"));
        } catch (CompraException e) {
            redirect.addFlashAttribute("error", messageHelper.get("erro") + ": " + e.getMessage());
        }
        return "redirect:/loja/inventario";
    }

    private User getUsuarioLogado(OAuth2User principal) {
        String email = principal.getAttribute("login") + "@github.com";
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
