package com.iespp.app.controllers;

import com.iespp.app.models.Role;
import com.iespp.app.models.User;
import com.iespp.app.repositories.RoleRepository;
import com.iespp.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/test/publico")
    public ResponseEntity<Map<String, String>> testPublico() {
        return ResponseEntity.ok(Map.of("mensaje", "Endpoint público funcionando correctamente"));
    }

    @GetMapping("/usuarios")
    public List<User> obtenerUsuarios() {
        return userRepository.findAll();
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (datos.containsKey("nombreCompleto")) {
                user.setNombreCompleto((String) datos.get("nombreCompleto"));
            }
            if (datos.containsKey("estado")) {
                user.setEstado((String) datos.get("estado"));
            }
            if (datos.containsKey("rolId")) {
                Object rolIdObj = datos.get("rolId");
                if (rolIdObj != null) {
                    Long rolId = Long.valueOf(rolIdObj.toString());
                    Optional<Role> roleOpt = roleRepository.findById(rolId);
                    if (roleOpt.isPresent()) {
                        user.getRoles().clear();
                        user.getRoles().add(roleOpt.get());
                    }
                }
            }

            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Usuario actualizado correctamente"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuarioLogico(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEstado("Eliminado"); 
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado lógicamente del sistema."));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado."));
    }
}
