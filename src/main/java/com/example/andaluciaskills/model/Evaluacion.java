package com.example.andaluciaskills.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Evaluacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idEvaluacion;

  @ManyToOne
  @JoinColumn(name = "Participante_id")
  private Participante participante;

  @ManyToOne
  @JoinColumn(name = "Prueba_id")
  private Prueba prueba;

  @ManyToOne
  @JoinColumn(name = "User_id")
  private Usuario evaluador;

  private Float notaFinal;
}
