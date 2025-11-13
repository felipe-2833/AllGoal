package br.com.fiap.allgoal.model;

import br.com.fiap.allgoal.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDate;

@Entity
@Table(name = "gs_meta_concluida")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaConcluida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meta_concluida")
    private Long idMetaConcluida;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_meta", nullable = false)
    private Meta meta;

    @Column(name = "data_submissao")
    @PastOrPresent(message = "{metaconcluida.datasub.pastorpresent}")
    private LocalDate dataSubmissao;

    @Column(nullable = false, length = 20)
    @NotNull(message = "{metaconcluida.status.notnull}")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "data_aprovacao")
    @PastOrPresent(message = "{metaconcluida.dataprov.pastorpresent}")
    private LocalDate dataAprovacao;

    @Column(name = "justificativa_texto", length = 500)
    @NotNull(message = "{metaconcluida.justificativa.notnull}")
    private String justificativaTexto;
}
