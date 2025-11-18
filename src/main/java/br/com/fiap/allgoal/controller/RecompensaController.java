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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public String loja(Model model,
                       @AuthenticationPrincipal OAuth2User user,
                       @RequestParam(defaultValue = "0") int page) {

        User usuario = getUsuarioLogado(user);
        Page<Recompensa> recompensasPage = recompensaService.getAllRecompensas(PageRequest.of(page, 8));

        model.addAttribute("user", usuario);
        model.addAttribute("recompensas", recompensasPage);
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
    public String inventario(Model model,
                             @AuthenticationPrincipal OAuth2User user,
                             @RequestParam(defaultValue = "0") int page) {

        User usuario = getUsuarioLogado(user);
        Page<CompraLoja> comprasPage = compraLojaService.getItensPorUsuario(usuario, PageRequest.of(page, 8));

        model.addAttribute("user", usuario);
        model.addAttribute("compras", comprasPage);
        model.addAttribute("listastatus", StatusCompra.values());
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

    @GetMapping("/adm")
    public String lojaAdm(Model model,
                          @AuthenticationPrincipal OAuth2User user,
                          @RequestParam(defaultValue = "0") int pageItens,
                          @RequestParam(defaultValue = "0") int pageSolicitacoes) {

        User usuario = getUsuarioLogado(user);
        Page<Recompensa> recompensasPage = recompensaService.getAllRecompensas(PageRequest.of(pageItens, 9));
        Page<CompraLoja> solicitadasPage = compraLojaService.getAllComprasSolicitadas(PageRequest.of(pageSolicitacoes, 9));

        model.addAttribute("user", usuario);
        model.addAttribute("recompensas", recompensasPage);
        model.addAttribute("compras", solicitadasPage);

        return "adm-recompensas";
    }

    @PostMapping("/adm/concluir")
    public String concluirRecompensa(@RequestParam("compraId") Long compraId,
                                   @AuthenticationPrincipal OAuth2User user,
                                   RedirectAttributes redirect) {
        try {
            compraLojaService.concluirRecompensa(compraId);
            redirect.addFlashAttribute("message", messageHelper.get("recompensa.concluir.sucesso"));
        } catch (CompraException e) {
            redirect.addFlashAttribute("error", messageHelper.get("erro") + ": " + e.getMessage());
        }
        return "redirect:/loja/adm";
    }

    @PostMapping("/adm/criar")
    public String criarMeta(
            @RequestParam("nome_item") String nome,
            @RequestParam("descricao") String descricao,
            @RequestParam("estoque") Integer estoque,
            @RequestParam("custo_moedas") Long custo,
            RedirectAttributes redirect) {

        try {
            recompensaService.criarRecompensa(nome, descricao, estoque, custo);
            redirect.addFlashAttribute("message", messageHelper.get("reward") + " " + nome + " " + messageHelper.get("created.success"));
        } catch (Exception e) {
            redirect.addFlashAttribute("error", messageHelper.get("erro") + e.getMessage());
        }

        return "redirect:/loja/adm";
    }

    @PostMapping("/delete")
    public String deleteRecompensa(@RequestParam("recompensaId") Long recompensaId, RedirectAttributes redirect ){
        try {
            recompensaService.deleteById(recompensaId);
            redirect.addFlashAttribute("message", messageHelper.get("recompensa.deleted.success"));

        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/loja/adm";
    }

    @PostMapping("/adm/atualizar")
    public String atualizarRecompensa(
            @RequestParam("idRecompensa") Long idRecompensa,
            @RequestParam("nome") String nome,
            @RequestParam("descricao") String descricao,
            @RequestParam("estoque") Integer estoque,
            @RequestParam("custo") Long custo,
            RedirectAttributes redirect) {

        try {
            recompensaService.atualizarRecompensa(idRecompensa, nome, descricao, estoque, custo);
            redirect.addFlashAttribute("message", messageHelper.get("reward") + " " + nome + " " + messageHelper.get("updated.success"));
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao atualizar recompensa: " + e.getMessage());
        }
        return "redirect:/loja/adm";
    }
}
