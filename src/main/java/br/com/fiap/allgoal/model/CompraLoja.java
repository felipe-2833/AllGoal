package br.com.fiap.allgoal.model;

import br.com.fiap.allgoal.enums.StatusCompra;
import br.com.fiap.allgoal.enums.StatusMeta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDate;

@Entity
@Table(name = "gs_compra_loja")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraLoja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long idCompra;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_recompensa", nullable = false)
    private Recompensa recompensa;

    @Column(name = "data_compra")
    @PastOrPresent(message = "{compra.data.pastorpresent}")
    private LocalDate dataCompra;

    @Column(name = "custo_pago", nullable = false)
    @Positive(message = "{compra.custo.positive}")
    private Long custoPago;

    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "{compra.status.notnull}")
    @Enumerated(EnumType.STRING)
    private StatusCompra statusCompra;
}
