package br.com.fiap.allgoal.controller;

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

    @GetMapping("/dashboard")
    public String index(Model model, @AuthenticationPrincipal OAuth2User user) {
        var metas = metaService.getAllMetas();

        model.addAttribute("motos", metas);
        model.addAttribute("user", user);
        return "index";
    }
}
