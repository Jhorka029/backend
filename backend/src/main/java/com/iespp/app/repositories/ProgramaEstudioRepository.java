package com.iespp.app.repositories;

import com.iespp.app.models.ProgramaEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramaEstudioRepository extends JpaRepository<ProgramaEstudio, Long> {
    Optional<ProgramaEstudio> findByCodigo(String codigo);
    List<ProgramaEstudio> findByEstado(String estado);
}
