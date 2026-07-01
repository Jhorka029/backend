package com.iespp.app.controllers;

import com.iespp.app.models.ProgramaEstudio;
import com.iespp.app.repositories.CursoRepository;
import com.iespp.app.repositories.ProgramaEstudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/programas-estudio")
public class ProgramaEstudioController {

    @Autowired
    private ProgramaEstudioRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping
    public List<ProgramaEstudio> getAll() {
        return repository.findByEstado("Activo");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramaEstudio> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProgramaEstudio p) {
        if (p.getCodigo() != null && repository.findByCodigo(p.getCodigo()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "El código del programa ya existe."));
        }
        return ResponseEntity.ok(repository.save(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProgramaEstudio datos) {
        return repository.findById(id).map(p -> {
            if (datos.getCodigo() != null) p.setCodigo(datos.getCodigo());
            if (datos.getNombre() != null) p.setNombre(datos.getNombre());
            if (datos.getDescripcion() != null) p.setDescripcion(datos.getDescripcion());
            if (datos.getEstado() != null) p.setEstado(datos.getEstado());
            return ResponseEntity.ok(repository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repository.findById(id).map(p -> {
            List<com.iespp.app.models.Curso> cursos = cursoRepository.findByProgramaIdAndEstado(id, "Activo");
            if (!cursos.isEmpty()) {
                return ResponseEntity.status(409).body(
                    Map.of("message", "No se puede eliminar el programa porque tiene " + cursos.size() + " curso(s) activo(s) asociados. Elimine o inhabilite los cursos primero.")
                );
            }
            p.setEstado("Inactivo");
            repository.save(p);
            return ResponseEntity.ok(Map.of("message", "Programa eliminado correctamente"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
