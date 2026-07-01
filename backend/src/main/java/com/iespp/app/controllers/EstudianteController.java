package com.iespp.app.controllers;

import com.iespp.app.models.Estudiante;
import com.iespp.app.repositories.DocenteRepository;
import com.iespp.app.repositories.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @GetMapping
    public List<Estudiante> getAll() {
        return estudianteRepository.findByEstado("Activo");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> getById(@PathVariable Long id) {
        return estudianteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private String dniEnUso(String dni, Long excludeId) {
        if (dni == null) return null;
        if (estudianteRepository.findByDni(dni)
                .filter(e -> excludeId == null || !e.getId().equals(excludeId))
                .isPresent()) {
            return "El DNI ya está registrado como estudiante.";
        }
        if (docenteRepository.findByDni(dni).isPresent()) {
            return "El DNI ya está registrado como docente.";
        }
        return null;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Estudiante estudiante) {
        String dniError = dniEnUso(estudiante.getDni(), null);
        if (dniError != null) {
            return ResponseEntity.badRequest().body(Map.of("message", dniError));
        }
        if (estudiante.getCodigoEstudiante() != null &&
                estudianteRepository.findByCodigoEstudiante(estudiante.getCodigoEstudiante()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "El código de estudiante ya existe."));
        }
        return ResponseEntity.ok(estudianteRepository.save(estudiante));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Estudiante datos) {
        return estudianteRepository.findById(id).map(est -> {
            if (datos.getDni() != null && !datos.getDni().equals(est.getDni())) {
                String dniError = dniEnUso(datos.getDni(), id);
                if (dniError != null) {
                    return ResponseEntity.badRequest().body(Map.of("message", dniError));
                }
            }
            if (datos.getNombreCompleto() != null) est.setNombreCompleto(datos.getNombreCompleto());
            if (datos.getEmail() != null) est.setEmail(datos.getEmail());
            if (datos.getTelefono() != null) est.setTelefono(datos.getTelefono());
            if (datos.getDireccion() != null) est.setDireccion(datos.getDireccion());
            if (datos.getFechaNacimiento() != null) est.setFechaNacimiento(datos.getFechaNacimiento());
            if (datos.getEstado() != null) est.setEstado(datos.getEstado());
            if (datos.getDni() != null) est.setDni(datos.getDni());
            if (datos.getCodigoEstudiante() != null) est.setCodigoEstudiante(datos.getCodigoEstudiante());
            return ResponseEntity.ok(estudianteRepository.save(est));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return estudianteRepository.findById(id).map(est -> {
            est.setEstado("Inactivo");
            estudianteRepository.save(est);
            return ResponseEntity.ok(Map.of("message", "Estudiante eliminado correctamente"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
