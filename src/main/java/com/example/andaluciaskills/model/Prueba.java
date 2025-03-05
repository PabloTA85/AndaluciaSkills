package com.example.andaluciaskills.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Prueba {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrueba;
    private String enunciado;
    private int puntuacionMaxima;
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "Especialidad_id")
    private Especialidad especialidad;

    // Relación inversa con Item
    @OneToMany(mappedBy = "prueba", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    // Campo para almacenar el PDF como un array de bytes
    @Lob  // Indicamos que este campo debe ser tratado como un LOB
    private byte[] pdfData;

    // Métodos para acceder y modificar los datos del PDF
    public void setPdfData(byte[] bytes) {
        this.pdfData = bytes; // Almacena directamente los bytes
    }

    public byte[] getPdfData() {
        return this.pdfData; // Devuelve los bytes
    }
}

