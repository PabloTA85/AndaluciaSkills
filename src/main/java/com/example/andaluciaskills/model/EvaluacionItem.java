package com.example.andaluciaskills.model;

import jakarta.persistence.Column;
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
public class EvaluacionItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idEvaluacionItem;

  @ManyToOne
  @JoinColumn(name = "Evaluacion_id")
  private Evaluacion evaluacion;

  @ManyToOne
  @JoinColumn(name = "Item_id")
  private Item item;

  //Esto puede ser nulo
  @Column(nullable = true)
  private int valoracion;
  private String comentario;
  //Restricciones a aplicar, por ejemplo, no puede ser mayor a 10, etc.
  
}