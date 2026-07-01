package com.iespp.app.repositories;

import com.iespp.app.models.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Optional<Docente> findByDni(String dni);
    Optional<Docente> findByCodigoDocente(String codigoDocente);
    List<Docente> findByNombreCompletoContainingIgnoreCase(String nombre);
    List<Docente> findByEstado(String estado);
}
