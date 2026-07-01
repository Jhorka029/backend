package com.iespp.app.controllers;

import com.iespp.app.models.Curso;
import com.iespp.app.models.Docente;
import com.iespp.app.models.ProgramaEstudio;
import com.iespp.app.repositories.CursoRepository;
import com.iespp.app.repositories.DocenteRepository;
import com.iespp.app.repositories.ProgramaEstudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private ProgramaEstudioRepository programaRepository;

    @GetMapping
    public List<Curso> getAll() {
        return cursoRepository.findByEstado("Activo");
    }

    @GetMapping("/programa/{programaId}")
    public List<Curso> getByPrograma(@PathVariable Long programaId) {
        return cursoRepository.findByProgramaIdAndEstado(programaId, "Activo");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> getById(@PathVariable Long id) {
        return cursoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Curso curso) {
        if (curso.getCodigo() != null && cursoRepository.findByCodigo(curso.getCodigo()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "El código del curso ya existe."));
        }
        if (curso.getPrograma() != null && curso.getPrograma().getId() != null) {
            programaRepository.findById(curso.getPrograma().getId()).ifPresent(curso::setPrograma);
        }
        return ResponseEntity.ok(cursoRepository.save(curso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Curso datos) {
        return cursoRepository.findById(id).map(cur -> {
            if (datos.getCodigo() != null) cur.setCodigo(datos.getCodigo());
            if (datos.getNombre() != null) cur.setNombre(datos.getNombre());
            if (datos.getDescripcion() != null) cur.setDescripcion(datos.getDescripcion());
            if (datos.getCreditos() != null) cur.setCreditos(datos.getCreditos());
            if (datos.getHoras() != null) cur.setHoras(datos.getHoras());
            if (datos.getCiclo() != null) cur.setCiclo(datos.getCiclo());
            if (datos.getPrograma() != null && datos.getPrograma().getId() != null) {
                programaRepository.findById(datos.getPrograma().getId()).ifPresent(cur::setPrograma);
            }
            if (datos.getEstado() != null) cur.setEstado(datos.getEstado());
            return ResponseEntity.ok(cursoRepository.save(cur));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return cursoRepository.findById(id).map(cur -> {
            if (cur.getDocentes() != null && !cur.getDocentes().isEmpty()) {
                return ResponseEntity.status(409).body(
                    Map.of("message", "No se puede eliminar el curso porque tiene docentes asignados. Remueva las asignaciones primero.")
                );
            }
            cur.setEstado("Inactivo");
            cursoRepository.save(cur);
            return ResponseEntity.ok(Map.of("message", "Curso eliminado correctamente"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/docentes")
    public ResponseEntity<List<Docente>> getDocentes(@PathVariable Long id) {
        return cursoRepository.findById(id)
                .map(cur -> ResponseEntity.ok(cur.getDocentes()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/asignar-docentes")
    public ResponseEntity<?> asignarDocentes(@PathVariable Long id, @RequestBody List<Long> docenteIds) {
        return cursoRepository.findById(id).map(cur -> {
            List<Docente> docentes = docenteRepository.findAllById(docenteIds);
            cur.setDocentes(docentes);
            cursoRepository.save(cur);
            return ResponseEntity.ok(Map.of("message", "Docentes asignados correctamente"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
