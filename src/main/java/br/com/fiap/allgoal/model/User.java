package br.com.fiap.allgoal.model;

import br.com.fiap.allgoal.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Entity
@Table(name = "gs_usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "avatar_url", length = 1000)
    private String avatarUrl;

    @Column(nullable = false)
    @Positive(message = "{user.nivel.positive}")
    private Integer nivel = 1;

    @Column(name = "xp_total", nullable = false)
    @PositiveOrZero(message = "{user.xp.positiveorzero}")
    private Long xpTotal = 0L;

    @Column(nullable = false)
    @PositiveOrZero(message = "{user.moedas.positiveorzero}")
    private Long moedas = 0L;

    @Column(nullable = false, length = 20)
    @NotNull(message = "{user.role.notnull}")
    @Enumerated(EnumType.STRING)
    private Roles role;

    public User(OAuth2User principal, String email) {
        this.nome = principal.getAttributes().get("name").toString();
        this.email = email;
        this.avatarUrl = principal.getAttributes().get("avatar_url").toString();
    }

}
