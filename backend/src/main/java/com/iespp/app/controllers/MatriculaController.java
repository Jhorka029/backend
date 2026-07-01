package com.iespp.app.controllers;

import com.iespp.app.models.Curso;
import com.iespp.app.models.Estudiante;
import com.iespp.app.models.Matricula;
import com.iespp.app.repositories.CursoRepository;
import com.iespp.app.repositories.EstudianteRepository;
import com.iespp.app.repositories.MatriculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @PostMapping
    public ResponseEntity<?> matricular(@RequestBody Map<String, Long> body) {
        Long estudianteId = body.get("estudianteId");
        Long cursoId = body.get("cursoId");

        if (estudianteId == null || cursoId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Debe enviar estudianteId y cursoId"));
        }

        Optional<Estudiante> estOpt = estudianteRepository.findById(estudianteId);
        if (estOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Estudiante no encontrado"));
        }

        Estudiante est = estOpt.get();
        if (!"Activo".equals(est.getEstado())) {
            return ResponseEntity.badRequest().body(Map.of("message", "El estudiante no está activo"));
        }

        Optional<Curso> curOpt = cursoRepository.findById(cursoId);
        if (curOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Curso no encontrado"));
        }

        Curso curso = curOpt.get();

        if (matriculaRepository.existsByEstudianteIdAndCursoId(estudianteId, cursoId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "El estudiante ya está matriculado en este curso"));
        }

        int creditosActuales = matriculaRepository.findByEstudianteId(estudianteId).stream()
                .mapToInt(m -> m.getCurso().getCreditos())
                .sum();

        if (creditosActuales + curso.getCreditos() > 28) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "El estudiante excede el límite de 28 créditos (tiene " + creditosActuales +
                           ", intenta agregar " + curso.getCreditos() + ")"
            ));
        }

        Matricula mat = new Matricula();
        mat.setEstudiante(est);
        mat.setCurso(curso);
        matriculaRepository.save(mat);

        return ResponseEntity.ok(Map.of(
            "message", "Matrícula exitosa",
            "estudiante", est.getNombreCompleto(),
            "curso", curso.getNombre(),
            "creditosTotales", creditosActuales + curso.getCreditos()
        ));
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Map<String, Object>>> getMatriculadosPorCurso(@PathVariable Long cursoId) {
        List<Matricula> matriculas = matriculaRepository.findByCursoId(cursoId);
        List<Map<String, Object>> result = matriculas.stream().map(m -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", m.getId());
            item.put("estudianteId", m.getEstudiante().getId());
            item.put("estudiante", m.getEstudiante().getNombreCompleto());
            item.put("codigoEstudiante", m.getEstudiante().getCodigoEstudiante());
            item.put("dni", m.getEstudiante().getDni());
            item.put("fechaMatricula", m.getFechaMatricula().toString());
            return item;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/creditos/{estudianteId}")
    public ResponseEntity<Map<String, Object>> getCreditos(@PathVariable Long estudianteId) {
        List<Matricula> matriculas = matriculaRepository.findByEstudianteId(estudianteId);
        int total = matriculas.stream().mapToInt(m -> m.getCurso().getCreditos()).sum();
        Map<String, Object> resp = new HashMap<>();
        resp.put("estudianteId", estudianteId);
        resp.put("creditos", total);
        resp.put("cursos", matriculas.size());
        return ResponseEntity.ok(resp);
    }
}
