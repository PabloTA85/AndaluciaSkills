package com.example.andaluciaskills.controller;

import com.example.andaluciaskills.converter.EspecialidadConverter;
import com.example.andaluciaskills.dto.EspecialidadDTO;
import com.example.andaluciaskills.model.Especialidad;
import com.example.andaluciaskills.service.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/especialidad")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    // Obtener todas las especialidades
    @GetMapping("/all")
    public List<EspecialidadDTO> getAllEspecialidades() {
        List<Especialidad> especialidades = especialidadService.findAll();
        return especialidades.stream()
                .map(EspecialidadConverter::toDTO)
                .toList();
    }

    // Obtener especialidad por ID
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> getEspecialidadById(@PathVariable Long id) {
        Optional<Especialidad> especialidad = especialidadService.findById(id);
        return especialidad.map(e -> ResponseEntity.ok(EspecialidadConverter.toDTO(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createEspecialidad(@RequestBody EspecialidadDTO especialidadDTO) {
        Especialidad especialidad = EspecialidadConverter.toEntity(especialidadDTO);

        if (especialidadService.findByCodigo(especialidad.getCodigo()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Error: Ya existe una especialidad con el código " + especialidad.getCodigo()));
        }
        if (especialidadService.findByNombre(especialidad.getNombre()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Error: Ya existe una especialidad con el nombre " + especialidad.getNombre()));
        }

        especialidadService.save(especialidad);
        return ResponseEntity.ok(Map.of("message", "Especialidad creada con éxito"));
    }

    // Actualizar especialidad
    @PutMapping("/{id}")
    public ResponseEntity<String> updateEspecialidad(@PathVariable Long id,
            @RequestBody EspecialidadDTO especialidadDTO) {

        Optional<Especialidad> existingEspecialidadOpt = especialidadService.findById(id);
        if (!existingEspecialidadOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Especialidad especialidad = EspecialidadConverter.toEntity(especialidadDTO);
        especialidad.setIdEspecialidad(id);

        if (especialidadService.findByCodigo(especialidad.getCodigo()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Error: Ya existe una especialidad con el código " + especialidad.getCodigo());
        }
        if (especialidadService.findByNombre(especialidad.getNombre()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Error: Ya existe una especialidad con el nombre " + especialidad.getNombre());
        }

        especialidadService.save(especialidad);
        return ResponseEntity.ok("Especialidad actualizada con éxito");
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<String> deleteEspecialidad(@PathVariable String codigo) {
        // Buscar la especialidad por su código
        Optional<Especialidad> especialidad = especialidadService.findByCodigo(codigo);

        if (!especialidad.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Especialidad no encontrada con código: " + codigo);
        }

        try {
            // Eliminar la especialidad utilizando el ID
            especialidadService.delete(especialidad.get().getIdEspecialidad());
            return ResponseEntity.ok("Especialidad eliminada con éxito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la especialidad: " + e.getMessage());
        }
    }

}
