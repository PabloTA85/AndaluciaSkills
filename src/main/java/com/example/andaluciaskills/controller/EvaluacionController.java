package com.example.andaluciaskills.controller;

import com.example.andaluciaskills.converter.EvaluacionConverter;
import com.example.andaluciaskills.dto.EvaluacionDTO;
import com.example.andaluciaskills.service.EvaluacionService;
import com.example.andaluciaskills.model.Evaluacion;
import com.example.andaluciaskills.service.ParticipanteService; // Importa el servicio de Participante
import com.example.andaluciaskills.service.PruebaService; // Importa el servicio de Prueba
import com.example.andaluciaskills.service.UsuarioService; // Importa el servicio de Usuario
import com.example.andaluciaskills.model.Participante;
import com.example.andaluciaskills.model.Prueba;
import com.example.andaluciaskills.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    @Autowired
    private EvaluacionConverter evaluacionConverter;

    // Inyecta los servicios para Participante, Prueba y Usuario
    @Autowired
    private ParticipanteService participanteService;

    @Autowired
    private PruebaService pruebaService;

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todas las evaluaciones
    @GetMapping("/all")
    public List<EvaluacionDTO> getAllEvaluaciones() {
        List<Evaluacion> evaluaciones = evaluacionService.findAll();
        return evaluaciones.stream()
                .map(evaluacionConverter::toDTO)
                .toList();
    }

    // Obtener una evaluación por su ID
    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionDTO> getEvaluacionById(@PathVariable Long id) {
        Optional<Evaluacion> evaluacionOptional = evaluacionService.findById(id);
        if (evaluacionOptional.isPresent()) {
            EvaluacionDTO dto = evaluacionConverter.toDTO(evaluacionOptional.get());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Crear una nueva evaluación
    @PostMapping("/crear")
    public ResponseEntity<EvaluacionDTO> createEvaluacion(@RequestBody EvaluacionDTO evaluacionDTO) {
        // Obtener los objetos correspondientes utilizando los servicios
        Participante participante = obtenerParticipantePorId(evaluacionDTO.getParticipanteId());
        Prueba prueba = obtenerPruebaPorId(evaluacionDTO.getPruebaId());
        Usuario evaluador = obtenerUsuarioPorId(evaluacionDTO.getEvaluadorId());
    
        // Convertir el DTO a entidad
        Evaluacion evaluacion = evaluacionConverter.toEntity(evaluacionDTO, participante, prueba, evaluador);
        
        // Guardar la evaluación
        Evaluacion evaluacionGuardada = evaluacionService.save(evaluacion);
    
        // Convertir la entidad guardada a DTO
        EvaluacionDTO dto = evaluacionConverter.toDTO(evaluacionGuardada);
    
        // Agregar el ID de la evaluación al DTO antes de devolverlo
        dto.setIdEvaluacion(evaluacionGuardada.getIdEvaluacion());
    
        // Retornar el DTO con el ID de la evaluación
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
    

    // Métodos auxiliares para obtener Participante, Prueba y Usuario
    private Participante obtenerParticipantePorId(Long id) {
        return participanteService.findById(id).orElseThrow(() -> new RuntimeException("Participante no encontrado"));
    }

    private Prueba obtenerPruebaPorId(Long id) {
        return pruebaService.findById(id).orElseThrow(() -> new RuntimeException("Prueba no encontrada"));
    }

    private Usuario obtenerUsuarioPorId(Long id) {
        return usuarioService.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}



