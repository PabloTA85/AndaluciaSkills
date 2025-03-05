package com.example.andaluciaskills.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class Especialidad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idEspecialidad;

  @Column(nullable = false, unique = true)
  @NotBlank(message = "El código no puede estar vacío")
  private String codigo;

  @Column(nullable = false, unique = true)
  @NotBlank(message = "El nombre no puede estar vacío")
  private String nombre;

  @OneToMany(mappedBy = "especialidad", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<Prueba> pruebas;

  // Constructor sin el id
  public Especialidad(String codigo, String nombre) {
    this.codigo = codigo;
    this.nombre = nombre;
  }

  // Constructor vacío, necesario para JPA
  public Especialidad() {}
}
