package com.iespp.app.repositories;

import com.iespp.app.models.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    List<Matricula> findByCursoId(Long cursoId);
    List<Matricula> findByEstudianteId(Long estudianteId);
    boolean existsByEstudianteIdAndCursoId(Long estudianteId, Long cursoId);
}
