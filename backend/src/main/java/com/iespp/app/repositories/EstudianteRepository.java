package com.iespp.app.repositories;

import com.iespp.app.models.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByDni(String dni);
    Optional<Estudiante> findByCodigoEstudiante(String codigoEstudiante);
    List<Estudiante> findByNombreCompletoContainingIgnoreCase(String nombre);
    List<Estudiante> findByEstado(String estado);
}
