package com.example.andaluciaskills.controller;

import com.example.andaluciaskills.converter.ParticipanteConverter;
import com.example.andaluciaskills.dto.ParticipanteDTO;
import com.example.andaluciaskills.model.Especialidad;
import com.example.andaluciaskills.model.Participante;
import com.example.andaluciaskills.model.Usuario;
import com.example.andaluciaskills.service.EspecialidadService;
import com.example.andaluciaskills.service.ParticipanteService;
import com.example.andaluciaskills.service.UsuarioService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/participante")
public class ParticipanteController {

    @Autowired
    private ParticipanteService participanteService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EspecialidadService especialidadService;

    // Obtener todos los participantes como DTOs
    @GetMapping("/all")
    public ResponseEntity<List<ParticipanteDTO>> getAllParticipantes() {
        List<Participante> participantes = participanteService.findAll();
        List<ParticipanteDTO> dtos = participantes.stream().map(participante -> {
            ParticipanteDTO dto = new ParticipanteDTO();
            dto.setIdParticipante(participante.getIdParticipante());
            dto.setNombre(participante.getNombre());
            dto.setApellidos(participante.getApellidos());
            dto.setEmail(participante.getEmail());
            dto.setCentro(participante.getCentro());
            dto.setNombreEspecialidad(participante.getEspecialidad().getNombre());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Obtener un participante por ID como DTO
    @GetMapping("/{id}")
    public ResponseEntity<ParticipanteDTO> getParticipanteById(@PathVariable Long id) {
        Participante participante = participanteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado"));
        ParticipanteDTO participanteDTO = ParticipanteConverter.toDTO(participante);
        return ResponseEntity.ok(participanteDTO);
    }

    // Crear un participante a partir de un DTO
    @PostMapping("/create")
    public ResponseEntity<String> createParticipante(@RequestBody ParticipanteDTO participanteDTO) {
        Especialidad especialidad = especialidadService
                .findByNombre(participanteDTO.getNombreEspecialidad())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        Participante participante = ParticipanteConverter.toEntity(participanteDTO, especialidad);
        participanteService.save(participante);

        return ResponseEntity.ok("Participante creado exitosamente");
    }

    @PostMapping("/create/byEspecialidad/{idUser}")
    public ResponseEntity<Map<String, Object>> createParticipanteByEspecialidad(
            @RequestBody ParticipanteDTO participanteDTO,
            @PathVariable Long idUser) {

        Map<String, Object> response = new HashMap<>();

        // Verificar que todos los campos están completos
        if (participanteDTO == null || participanteDTO.getNombre() == null || participanteDTO.getApellidos() == null ||
                participanteDTO.getEmail() == null || participanteDTO.getCentro() == null) {
            response.put("status", "error");
            response.put("message", "Por favor, completa todos los campos del participante.");
            return ResponseEntity.badRequest().body(response);
        }

        // Buscar el usuario por su ID
        Usuario usuario = usuarioService.findById(idUser).orElse(null);
        if (usuario == null) {
            response.put("status", "error");
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Obtener la especialidad del usuario
        Especialidad especialidad = usuario.getEspecialidad();
        if (especialidad == null) {
            response.put("status", "error");
            response.put("message", "El usuario no tiene una especialidad asignada");
            return ResponseEntity.badRequest().body(response);
        }

        // Crear el participante
        Participante participante = new Participante();
        participante.setNombre(participanteDTO.getNombre());
        participante.setApellidos(participanteDTO.getApellidos());
        participante.setEmail(participanteDTO.getEmail());
        participante.setCentro(participanteDTO.getCentro());
        participante.setEspecialidad(especialidad);

        try {
            participanteService.save(participante);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error al guardar el participante.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        // Respuesta exitosa
        response.put("status", "success");
        response.put("message", "Participante creado exitosamente con la especialidad del usuario logeado");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateParticipante(@PathVariable Long id,
            @RequestBody ParticipanteDTO participanteDTO) {
        try {
            // Buscar al participante por id
            Participante participante = participanteService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Participante no encontrado"));

            // Si el nombre de la especialidad ha cambiado, actualízalo
            if (participanteDTO.getNombreEspecialidad() != null && !participanteDTO.getNombreEspecialidad().isEmpty()) {
                // Buscar la especialidad
                Especialidad especialidad = especialidadService
                        .findByNombre(participanteDTO.getNombreEspecialidad())
                        .orElseThrow(() -> new EntityNotFoundException("Especialidad con el nombre "
                                + participanteDTO.getNombreEspecialidad() + " no encontrada"));

                // Actualizar la especialidad
                participante.setEspecialidad(especialidad);
            }

            // Actualizar los datos del participante con los nuevos valores
            participante.setNombre(participanteDTO.getNombre());
            participante.setApellidos(participanteDTO.getApellidos());
            participante.setEmail(participanteDTO.getEmail());
            participante.setCentro(participanteDTO.getCentro());

            // Guardar el participante actualizado
            participanteService.save(participante);

            // Responder con un mensaje JSON
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Participante actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al modificar el participante"));
        }
    }

    // Eliminar un participante
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipante(@PathVariable Long id) {
        if (!participanteService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        participanteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener los participantes con la misma especialidad que el experto logeado
    @GetMapping("/porEspecialidad/{username}")
    public ResponseEntity<List<Participante>> getParticipantesByEspecialidad(@PathVariable String username) {
        Usuario usuario = usuarioService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Especialidad especialidadExperto = usuario.getEspecialidad();
        if (especialidadExperto != null) {
            List<Participante> participantes = participanteService
                    .findByEspecialidad(especialidadExperto.getIdEspecialidad());
            return participantes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(participantes);
        }

        return ResponseEntity.badRequest().build();
    }
}
