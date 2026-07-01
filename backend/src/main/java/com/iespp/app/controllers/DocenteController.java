package com.iespp.app.controllers;

import com.iespp.app.models.Docente;
import com.iespp.app.repositories.CursoRepository;
import com.iespp.app.repositories.DocenteRepository;
import com.iespp.app.repositories.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docentes")
public class DocenteController {

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private CursoRepository cursoRepository;

    private String dniEnUso(String dni, Long excludeId) {
        if (dni == null) return null;
        if (docenteRepository.findByDni(dni)
                .filter(d -> excludeId == null || !d.getId().equals(excludeId))
                .isPresent()) {
            return "El DNI ya está registrado como docente.";
        }
        if (estudianteRepository.findByDni(dni).isPresent()) {
            return "El DNI ya está registrado como estudiante.";
        }
        return null;
    }

    @GetMapping
    public List<Docente> getAll() {
        return docenteRepository.findByEstado("Activo");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Docente> getById(@PathVariable Long id) {
        return docenteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Docente docente) {
        String dniError = dniEnUso(docente.getDni(), null);
        if (dniError != null) {
            return ResponseEntity.badRequest().body(Map.of("message", dniError));
        }
        if (docente.getCodigoDocente() != null &&
                docenteRepository.findByCodigoDocente(docente.getCodigoDocente()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "El código de docente ya existe."));
        }
        return ResponseEntity.ok(docenteRepository.save(docente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Docente datos) {
        return docenteRepository.findById(id).map(doc -> {
            if (datos.getDni() != null && !datos.getDni().equals(doc.getDni())) {
                String dniError = dniEnUso(datos.getDni(), id);
                if (dniError != null) {
                    return ResponseEntity.badRequest().body(Map.of("message", dniError));
                }
            }
            if (datos.getNombreCompleto() != null) doc.setNombreCompleto(datos.getNombreCompleto());
            if (datos.getEmail() != null) doc.setEmail(datos.getEmail());
            if (datos.getTelefono() != null) doc.setTelefono(datos.getTelefono());
            if (datos.getDireccion() != null) doc.setDireccion(datos.getDireccion());
            if (datos.getEspecialidad() != null) doc.setEspecialidad(datos.getEspecialidad());
            if (datos.getFechaNacimiento() != null) doc.setFechaNacimiento(datos.getFechaNacimiento());
            if (datos.getEstado() != null) doc.setEstado(datos.getEstado());
            if (datos.getDni() != null) doc.setDni(datos.getDni());
            if (datos.getCodigoDocente() != null) doc.setCodigoDocente(datos.getCodigoDocente());
            return ResponseEntity.ok(docenteRepository.save(doc));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return docenteRepository.findById(id).map(doc -> {
            List<com.iespp.app.models.Curso> cursos = cursoRepository.findByDocenteIdAndEstadoActivo(id);
            if (!cursos.isEmpty()) {
                return ResponseEntity.status(409).body(
                    Map.of("message", "No se puede eliminar el docente porque está asignado a " + cursos.size() + " curso(s). Remueva las asignaciones primero.")
                );
            }
            doc.setEstado("Inactivo");
            docenteRepository.save(doc);
            return ResponseEntity.ok(Map.of("message", "Docente eliminado correctamente"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
