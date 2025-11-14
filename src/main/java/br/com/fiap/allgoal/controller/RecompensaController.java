package br.com.fiap.allgoal.controller;

import br.com.fiap.allgoal.enums.Status;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/loja")
@RequiredArgsConstructor
public class RecompensaController {

    private final UserRepository userRepository;

    @GetMapping
    public String loja(Model model, @AuthenticationPrincipal OAuth2User user) {
        String email = user.getAttribute("login") + "@github.com";
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        model.addAttribute("user", usuario);
        return "loja";
    }
}
