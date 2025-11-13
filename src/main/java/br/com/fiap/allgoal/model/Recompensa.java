package br.com.fiap.allgoal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gs_recompensa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recompensa")
    private Long idRecompensa;

    @Column(name = "nome_item", nullable = false, length = 100)
    @NotBlank(message = "{recompensa.nome.notblank}")
    private String nomeItem;

    @Column(length = 255)
    @NotBlank(message = "{recompensa.descricao.notblank}")
    private String descricao;

    @Column(name = "custo_moedas", nullable = false)
    @Positive(message = "{recompensa.custo.positive}")
    private Long custoMoedas;

    @Column(nullable = false)
    @PositiveOrZero(message = "{recompensa.estoque.positiveorzero}")
    private Integer estoque;
}
