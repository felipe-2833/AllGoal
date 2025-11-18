package br.com.fiap.allgoal.service;

import br.com.fiap.allgoal.config.MessageHelper;
import br.com.fiap.allgoal.enums.Roles;
import br.com.fiap.allgoal.model.User;
import br.com.fiap.allgoal.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, MessageHelper messageHelper) {
        this.userRepository = userRepository;
    }

    public User register(OAuth2User principal) {
        var login = Optional.ofNullable(principal.getAttributes().get("login"))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException("Login não fornecido pelo OAuth2User"));

        var user = userRepository.findByEmail(login + "@github.com");
        return user.orElseGet(() -> userRepository.save(new User(principal, login + "@github.com")));
    }

    public Long getXpRestanteParaProximoNivel(User usuario) {
        long nivel = usuario.getNivel();
        long xpTotal = usuario.getXpTotal();

        long xpTeto = (long) (100 * (nivel * (nivel + 1) / 2.0));

        return xpTeto - xpTotal;
    }

    public Long getXpNecessatio(User usuario) {
        long nivel = usuario.getNivel();
        long xpTotal = usuario.getXpTotal();
        long xp = 0;
        for (long j = 1; j < nivel; j++) {
            xp = xp + (100 * j);
        }

        xp = xpTotal - xp;

        return xp;
    }

    public void trocarRole(User user) {
        if (user.getRole() == Roles.ADMIN) {
            user.setRole(Roles.FUNCIONARIO);
        } else {
            user.setRole(Roles.ADMIN);
        }
        userRepository.save(user);
    }

    public List<User> getTop5Ranking() {
        return userRepository.findTop5ByRoleOrderByXpTotalDesc(Roles.FUNCIONARIO);
    }
}
