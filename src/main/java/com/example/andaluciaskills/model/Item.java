package com.example.andaluciaskills.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idItem;

  private String descripcion;
  private int peso;
  private int gradosConsecucion;  

  @ManyToOne
  @JoinColumn(name = "Prueba_id")
  @JsonIgnore
  private Prueba prueba;
}