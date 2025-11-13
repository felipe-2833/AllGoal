package br.com.fiap.allgoal.model;

import br.com.fiap.allgoal.enums.StatusMeta;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "gs_meta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meta")
    private Long idMeta;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "{meta.titulo.notblank}")
    private String titulo;

    @Lob
    @Column(columnDefinition = "CLOB")
    @NotBlank(message = "{meta.descricao.notblank}")
    private String descricao;

    @Column(name = "xp_recompensa", nullable = false)
    @PositiveOrZero(message = "{meta.xp.positive}")
    private Integer xpRecompensa;

    @Column(name = "moedas_recompensa", nullable = false)
    @PositiveOrZero(message = "{meta.moedas.positive}")
    private Integer moedasRecompensa;

    @Column(name = "data_criacao")
    @PastOrPresent(message = "{meta.data.pastorpresent}")
    private LocalDate dataCriacao;

    @Column(name = "status_meta", nullable = false, length = 20)
    @NotNull(message = "{meta.status.notnull}")
    @Enumerated(EnumType.STRING)
    private StatusMeta statusMeta;
}
