package br.com.fiap.allgoal.controller;

import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.UserRepository;
import br.com.fiap.allgoal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/ranking")
    public String ranking(Model model, @AuthenticationPrincipal OAuth2User user) {
        User usuario = getUsuarioLogado(user);
        long rankingUser = userRepository.getRanking(usuario.getXpTotal());
        List<User> rankingList = userService.getTop5Ranking();

        model.addAttribute("user", usuario);
        model.addAttribute("posicao", rankingUser);
        model.addAttribute("rankingList", rankingList);
        return "ranking";
    }

    @PostMapping("/trocar-role")
    public String trocarRole(@AuthenticationPrincipal OAuth2User user) {

        User usuario = getUsuarioLogado(user);
        userService.trocarRole(usuario);

        return "redirect:/meta/dashboard";
    }

    private User getUsuarioLogado(OAuth2User principal) {
        String email = principal.getAttribute("login") + "@github.com";
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
