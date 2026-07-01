package com.iespp.app.repositories;

import com.iespp.app.models.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    Optional<Curso> findByCodigo(String codigo);
    List<Curso> findByNombreContainingIgnoreCase(String nombre);
    List<Curso> findByEstado(String estado);
    List<Curso> findByProgramaIdAndEstado(Long programaId, String estado);
    List<Curso> findByProgramaIdAndCicloAndEstado(Long programaId, Integer ciclo, String estado);
    @Query("SELECT c FROM Curso c JOIN c.docentes d WHERE d.id = :docenteId AND c.estado = 'Activo'")
    List<Curso> findByDocenteIdAndEstadoActivo(@Param("docenteId") Long docenteId);
}
