package br.com.fiap.allgoal.controller;

import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatPageController {

    private final UserRepository userRepository;

    @GetMapping("/chat")
    public String showChatPage(Model model, @AuthenticationPrincipal OAuth2User user) {
        User usuario = getUsuarioLogado(user);

        model.addAttribute("user", usuario);
        return "chat";
    }

    private User getUsuarioLogado(OAuth2User principal) {
        String email = principal.getAttribute("login") + "@github.com";
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}