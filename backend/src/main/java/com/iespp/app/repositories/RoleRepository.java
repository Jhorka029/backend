package com.iespp.app.repositories;

import com.iespp.app.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Trae solo los roles que no han sido borrados lógicamente
    List<Role> findByEstadoRolTrue();

    // Busca un rol específico por nombre (sin importar si está activo o inactivo)
    Optional<Role> findByNombre(String nombre);
}