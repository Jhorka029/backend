package com.iespp.app.repositories;

import com.iespp.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Método crucial para la autenticación: buscar un usuario por su correo electrónico
    Optional<User> findByEmail(String email);
    
    // Para validar si un correo ya existe antes de registrar un nuevo usuario
    Boolean existsByEmail(String email);

    // Verifica si al menos un usuario tiene asignado un rol específico
    boolean existsByRolesId(Long roleId);
}