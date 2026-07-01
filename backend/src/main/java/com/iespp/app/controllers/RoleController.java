package com.iespp.app.controllers;

import com.iespp.app.models.Role;
import com.iespp.app.repositories.RoleRepository;
import com.iespp.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Obtener todos los roles activos (no eliminados lógicamente)
    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findByEstadoRolTrue();
    }

    // 2. Crear un nuevo rol
    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return roleRepository.save(role);
    }

    // 3. Actualizar un rol existente
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role roleDetails) {
        return roleRepository.findById(id).map(role -> {
            role.setNombre(roleDetails.getNombre());
            role.setDescripcion(roleDetails.getDescripcion());
            if (roleDetails.getPermisos() != null) {
                role.setPermisos(roleDetails.getPermisos());
            }
            return ResponseEntity.ok(roleRepository.save(role));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. Eliminar un rol (borrado lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        return roleRepository.findById(id).map(role -> {
            // Validar si el rol tiene permisos asignados
            if (role.getPermisos() != null && !role.getPermisos().isEmpty()) {
                return ResponseEntity.status(409).body(Map.of(
                    "error", "No se puede eliminar el rol porque tiene " + role.getPermisos().size() + " permiso(s) asignado(s). Retire los permisos antes de eliminar."
                ));
            }
            // Validar si el rol está vinculado a algún usuario
            if (userRepository.existsByRolesId(id)) {
                return ResponseEntity.status(409).body(Map.of(
                    "error", "No se puede eliminar el rol porque está vinculado a uno o más usuarios. Desvincule el rol de los usuarios antes de eliminar."
                ));
            }
            // Borrado lógico: marcar como inactivo
            role.setEstadoRol(false);
            roleRepository.save(role);
            return ResponseEntity.ok(Map.of("message", "Rol eliminado correctamente"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Obtener solo los permisos de un rol específico
    @GetMapping("/{id}/permisos")
    public ResponseEntity<Set<String>> getPermisosByRole(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(role -> ResponseEntity.ok(role.getPermisos()))
                .orElse(ResponseEntity.notFound().build());
    }

    // 6. Guardar la lista de permisos seleccionados desde el Frontend
    @PostMapping("/{id}/permisos")
    public ResponseEntity<?> updatePermisos(@PathVariable Long id, @RequestBody Set<String> permisos) {
        return roleRepository.findById(id).map(role -> {
            role.setPermisos(permisos); // Actualiza la lista de permisos
            roleRepository.save(role);   // Guarda en la base de datos
            return ResponseEntity.ok(Map.of("message", "Permisos actualizados correctamente"));
        }).orElse(ResponseEntity.notFound().build());
    }
}