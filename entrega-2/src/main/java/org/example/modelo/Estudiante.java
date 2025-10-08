package org.example.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(nullable = false, unique = true)
    private String nroLibreta; // clave “natural” única

    @ToString.Include private String nombres;
    @ToString.Include private String apellido;
    private Integer edad;
    private String genero;
    private String documento;
    private String ciudadResidencia;

    @OneToMany(mappedBy = "estudiante")
    @ToString.Exclude
    private Set<EstudianteCarrera> inscripciones = new HashSet<>();

    @Builder
    public Estudiante(String nroLibreta, String nombres, String apellido,
                      Integer edad, String genero, String documento, String ciudadResidencia) {
        this.nroLibreta = nroLibreta;
        this.nombres = nombres;
        this.apellido = apellido;
        this.edad = edad;
        this.genero = genero;
        this.documento = documento;
        this.ciudadResidencia = ciudadResidencia;
    }
}
