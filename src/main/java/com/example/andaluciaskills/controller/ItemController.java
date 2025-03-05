package com.example.andaluciaskills.controller;

import com.example.andaluciaskills.model.Item;
import com.example.andaluciaskills.model.Prueba;
import com.example.andaluciaskills.repository.PruebaRepository;
import com.example.andaluciaskills.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private PruebaRepository pruebaRepository;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_EXPERTO')")
    public List<Item> getAllItems() {
        return itemService.findAll();
    }

    @GetMapping("/especialidad/{id}")
    @PreAuthorize("hasRole('ROLE_EXPERTO')")
    public List<Item> getItemsByEspecialidad(@PathVariable Long id) {
        return itemService.findByEspecialidadId(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_EXPERTO')")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.findById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

 @PostMapping("/create/{pruebaId}")
@PreAuthorize("hasRole('ROLE_EXPERTO')")
public ResponseEntity<Void> createItem(@RequestBody List<Item> items, @PathVariable Long pruebaId) {
    if (items == null || items.isEmpty()) {
        return ResponseEntity.badRequest().build(); // 400 Bad Request si la lista está vacía
    }
    
    try {
        // Obtén la prueba que corresponde al ID
        Prueba prueba = pruebaRepository.findById(pruebaId)
            .orElseThrow(() -> new RuntimeException("Prueba no encontrada con id: " + pruebaId));

        // Asocia la prueba a cada item y guarda
        items.forEach(item -> item.setPrueba(prueba));
        
        // Guarda todos los items
        itemService.saveAll(items);
        
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created si todo va bien
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 si hay un error
    }
}


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_EXPERTO')")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        if (!itemService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        item.setIdItem(id);
        return ResponseEntity.ok(itemService.save(item));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_EXPERTO')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (!itemService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
