package com.iespp.app.controllers;

import com.iespp.app.models.Role;
import com.iespp.app.models.User;
import com.iespp.app.repositories.UserRepository;
import com.iespp.app.security.jwt.JwtUtils;
import com.iespp.app.services.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.iespp.app.repositories.RoleRepository;



@CrossOrigin(origins = "https://cevicherias.spring.informaticapp.com/seguridad%20EESSP/frontend", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    EmailService emailService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;



    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.email());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 1. Bloqueo permanente por admin (estado = "Bloqueado" sin fechaBloqueo)
            if ("Bloqueado".equals(user.getEstado()) && user.getFechaBloqueo() == null) {
                return ResponseEntity.status(423).body("Cuenta bloqueada permanentemente. Contacta al administrador.");
            }

            // 2. Bloqueo temporal por intentos fallidos (fechaBloqueo en el futuro)
            if (user.getFechaBloqueo() != null) {
                java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
                if (ahora.isBefore(user.getFechaBloqueo())) {
                    long segundosRestantes = java.time.Duration.between(ahora, user.getFechaBloqueo()).toSeconds();
                    return ResponseEntity.status(423).body(java.util.Map.of(
                        "segundos", segundosRestantes,
                        "mensaje", "Cuenta bloqueada temporalmente. Intenta de nuevo en " + segundosRestantes + " segundos."
                    ));
                } else {
                    // El bloqueo expiró, lo limpiamos
                    user.setFechaBloqueo(null);
                    user.setIntentosFallidos(0);
                    userRepository.save(user);
                }
            }

            // 3. Cuenta Pendiente (no ha verificado email)
            if ("Pendiente".equals(user.getEstado()) && "LOCAL".equals(user.getProveedorAuth())) {
                return ResponseEntity.badRequest().body("Error: Debes verificar tu correo electrónico antes de iniciar sesión.");
            }

            try {
                // 4. Intentar la autenticación de Spring Security
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                // Autenticación exitosa: Reiniciamos el contador de fallos
                user.setIntentosFallidos(0);
                user.setFechaBloqueo(null);

                // Sesión única: si ya hay session_token activo, bloquear nuevo login
                if (user.getSessionToken() != null) {
                    return ResponseEntity.status(403).body("Ya existe una sesión activa en otro dispositivo. Cierre sesión primero.");
                }

                user.setTokenActivo(jwt);
                userRepository.save(user);

                String rol = user.getRoles().isEmpty() ? "Visitante" : user.getRoles().iterator().next().getNombre();
                Set<String> permisos = user.getRoles().stream()
                    .flatMap(r -> r.getPermisos().stream())
                    .collect(Collectors.toSet());
                return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getEmail(), user.getNombreCompleto(), rol, permisos));

            } catch (org.springframework.security.authentication.BadCredentialsException e) {
                // 5. Manejo seguro de nulos y registro de fallos
                int fallosActuales = (user.getIntentosFallidos() != null) ? user.getIntentosFallidos() : 0;
                int nuevosIntentos = fallosActuales + 1;
                user.setIntentosFallidos(nuevosIntentos);

                // Cada 3 intentos fallidos (3, 6, 9...) → bloqueo temporal de 90 segundos
                if (nuevosIntentos % 3 == 0) {
                    user.setFechaBloqueo(java.time.LocalDateTime.now().plusSeconds(90));
                    userRepository.save(user);
                    return ResponseEntity.status(423).body(java.util.Map.of(
                        "segundos", 90,
                        "mensaje", "Demasiados intentos fallidos. Cuenta bloqueada por 90 segundos."
                    ));
                }

                userRepository.save(user);

                int intentoActual = nuevosIntentos % 3;
                return ResponseEntity.badRequest().body("Credenciales incorrectas. Intento " + intentoActual + " de 3 antes del bloqueo.");
            }
        }

        return ResponseEntity.badRequest().body("Credenciales incorrectas.");
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.email())) {
            return ResponseEntity.badRequest().body("Error: ¡El correo electrónico ya está en uso!");
        }

        User user = new User();
        user.setNombreCompleto(signUpRequest.nombreCompleto());
        user.setEmail(signUpRequest.email());
        user.setPassword(encoder.encode(signUpRequest.password()));
        user.setProveedorAuth("LOCAL");
        user.setCuentaVerificada(false);
        user.setEstado("Pendiente"); // Pendiente hasta que verifique su correo
        
        String codigoAleatorio = java.util.UUID.randomUUID().toString();
        user.setCodigoVerificacion(codigoAleatorio);

        // ========================================================
        // ASIGNACIÓN AUTOMÁTICA DEL ROL "VISITANTE"
        // ========================================================
        Role rolVisitante = roleRepository.findByNombre("Visitante")
            .orElseGet(() -> {
                Role nuevoRol = new Role();
                nuevoRol.setNombre("Visitante");
                nuevoRol.setDescripcion("Acceso restringido solo al apartado de Inicio.");
                nuevoRol.setEstadoRol(true);
                nuevoRol.getPermisos().add("MOD_INICIO"); // Le damos el permiso clave
                return roleRepository.save(nuevoRol);
            });
            
        user.getRoles().add(rolVisitante);
        // ========================================================

        userRepository.save(user);
        emailService.enviarCorreoVerificacion(user.getEmail(), user.getNombreCompleto(), codigoAleatorio);

        return ResponseEntity.ok("¡Usuario registrado! Por favor revisa tu correo para verificar la cuenta.");
    }   

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("code") String code) {
        // Buscamos si existe un usuario con ese código exacto
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> code.equals(u.getCodigoVerificacion()))
                .findFirst();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setCuentaVerificada(true); // ¡Cuenta activada!
            user.setEstado("Activo"); // Cambiamos a Activo
            user.setCodigoVerificacion(null); // Borramos el código para que no se re-use
            userRepository.save(user);

            // Redirigimos al frontend con un mensaje de éxito
            return ResponseEntity.status(org.springframework.http.HttpStatus.FOUND)
                    .header("Location", "https://cevicherias.spring.informaticapp.com/seguridad%20EESSP/frontend/login?verified=true")
                    .build();
        }

        return ResponseEntity.badRequest().body("Código de verificación inválido o ya utilizado.");
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody TokenDto tokenDto) {
        try {
            // 1. Configurar el verificador de Google con tu Client ID
            // ¡REEMPLAZA ESTO CON TU CLIENT ID REAL DE GOOGLE!
            // 1. Configurar el verificador de Google con tu Client ID
            String clientId = "593968468136-ekk6g5qh6c5fu3tp0j5256nu0fj93lka.apps.googleusercontent.com";
            
            // Usamos GsonFactory que es el estándar actual recomendado por Google
            com.google.api.client.json.gson.GsonFactory jsonFactory = com.google.api.client.json.gson.GsonFactory.getDefaultInstance();
            com.google.api.client.http.javanet.NetHttpTransport transport = new com.google.api.client.http.javanet.NetHttpTransport();
            
            com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier verifier = 
                new com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(java.util.Collections.singletonList(clientId))
                .build();

            // 2. Verificar el token recibido
            com.google.api.client.googleapis.auth.oauth2.GoogleIdToken idToken = verifier.verify(tokenDto.token());
            
            if (idToken != null) {
                com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // 3. Buscar si el usuario ya existe en nuestra BD
                Optional<User> userOpt = userRepository.findByEmail(email);
                User user;

                if (userOpt.isPresent()) {
                    user = userOpt.get();

                    // Bloqueo permanente por admin
                    if ("Bloqueado".equals(user.getEstado()) && user.getFechaBloqueo() == null) {
                        return ResponseEntity.status(423).body("Cuenta bloqueada permanentemente. Contacta al administrador.");
                    }

                    // Bloqueo temporal por intentos fallidos
                    if (user.getFechaBloqueo() != null) {
                        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
                        if (ahora.isBefore(user.getFechaBloqueo())) {
                            long segundosRestantes = java.time.Duration.between(ahora, user.getFechaBloqueo()).toSeconds();
                            return ResponseEntity.status(423).body(java.util.Map.of(
                                "segundos", segundosRestantes,
                                "mensaje", "Cuenta bloqueada temporalmente. Intenta de nuevo en " + segundosRestantes + " segundos."
                            ));
                        } else {
                            user.setFechaBloqueo(null);
                            user.setIntentosFallidos(0);
                        }
                    }

                    if (user.isCuentaVerificada() == null) {
                        user.setCuentaVerificada(true);
                    }
                } else {
                    user = new User();
                    user.setEmail(email);
                    user.setNombreCompleto(name);
                    user.setProveedorAuth("GOOGLE");
                    user.setCuentaVerificada(true);
                    user.setEstado("Activo");
                    user.setPassword(encoder.encode("OAUTH_USER_" + java.util.UUID.randomUUID().toString()));
                    
                    // ========================================================
                    // ASIGNACIÓN AUTOMÁTICA DEL ROL "VISITANTE" PARA GOOGLE
                    // ========================================================
                    Role rolVisitante = roleRepository.findByNombre("Visitante")
                        .orElseGet(() -> {
                            Role nuevoRol = new Role();
                            nuevoRol.setNombre("Visitante");
                            nuevoRol.setDescripcion("Acceso restringido solo al apartado de Inicio.");
                            nuevoRol.setEstadoRol(true);
                            nuevoRol.getPermisos().add("MOD_INICIO");
                            return roleRepository.save(nuevoRol);
                        });
                    user.getRoles().add(rolVisitante);
                    // ========================================================
                    
                    userRepository.save(user);
                }

                // Sesión única: si ya hay session_token activo, bloquear nuevo login con Google
                if (user.getSessionToken() != null) {
                    return ResponseEntity.status(403).body("Ya existe una sesión activa en otro dispositivo. Cierre sesión primero.");
                }

                // 4. Generar el contexto de seguridad y nuestro propio JWT
                org.springframework.security.core.userdetails.UserDetails userDetails = 
                    new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), java.util.List.of());
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                // 5. Guardar el token para la restricción de un solo dispositivo
                user.setTokenActivo(jwt);
                userRepository.save(user);

                String rol = user.getRoles().isEmpty() ? "Visitante" : user.getRoles().iterator().next().getNombre();
                Set<String> permisos = user.getRoles().stream()
                    .flatMap(r -> r.getPermisos().stream())
                    .collect(Collectors.toSet());
                return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getEmail(), user.getNombreCompleto(), rol, permisos));
            } else {
                return ResponseEntity.badRequest().body("Token de Google inválido");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al autenticar con Google: " + e.getMessage());
        }
    }

    

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.ok(java.util.Map.of("mensaje", "Sesión cerrada."));
            }
            String jwt = authHeader.substring(7);
            if (jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getUserNameFromJwtToken(jwt);
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    user.setSessionToken(null);
                    userRepository.save(user);
                }
            }
            return ResponseEntity.ok(java.util.Map.of("mensaje", "Sesión cerrada correctamente."));
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Map.of("mensaje", "Sesión cerrada."));
        }
    }

    @PostMapping("/register-session")
    public ResponseEntity<?> registerSession(@RequestBody java.util.Map<String, String> body,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Token JWT requerido.");
            }
            String jwt = authHeader.substring(7);
            if (!jwtUtils.validateJwtToken(jwt)) {
                return ResponseEntity.status(401).body("Token JWT inválido.");
            }
            String email = jwtUtils.getUserNameFromJwtToken(jwt);
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado.");
            }
            String sessionToken = body.get("sessionToken");
            if (sessionToken == null || sessionToken.isBlank()) {
                return ResponseEntity.badRequest().body("sessionToken requerido.");
            }
            User user = userOpt.get();
            user.setSessionToken(sessionToken);
            userRepository.save(user);
            return ResponseEntity.ok(java.util.Map.of("mensaje", "Sesión registrada correctamente."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al registrar sesión: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            String codigo = String.format("%06d", new java.util.Random().nextInt(999999));
            
            user.setTokenPassword(codigo);
            // Definimos la expiración exactamente a 120 segundos a partir de ahora
            user.setExpiracionTokenPassword(java.time.LocalDateTime.now().plusSeconds(120));
            userRepository.save(user);
            
            emailService.enviarCorreoRecuperacion(user.getEmail(), codigo);
            return ResponseEntity.ok("Código de seguridad enviado.");
        }
        return ResponseEntity.badRequest().body("El correo no está registrado en el sistema.");
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Validar si el código ya expiró por tiempo
            if (user.getExpiracionTokenPassword() == null || java.time.LocalDateTime.now().isAfter(user.getExpiracionTokenPassword())) {
                return ResponseEntity.badRequest().body("El código de seguridad ha expirado (Límite 120s). Solicita uno nuevo.");
            }
            
            if (code.equals(user.getTokenPassword())) {
                return ResponseEntity.ok("Código verificado correctamente.");
            }
        }
        return ResponseEntity.badRequest().body("El código es incorrecto.");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");

        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if(userOpt.isPresent() && code.equals(userOpt.get().getTokenPassword())) {
            User user = userOpt.get();
            user.setPassword(encoder.encode(newPassword));
            user.setTokenPassword(null); // Invalidamos el código usado
            userRepository.save(user);
            return ResponseEntity.ok("Contraseña actualizada exitosamente.");
        }
        return ResponseEntity.badRequest().body("Petición inválida o código alterado.");
    }
}

// --- DTOs (Data Transfer Objects) usando Records de Java 17 ---
record LoginRequest(String email, String password) {}
record SignupRequest(String nombreCompleto, String email, String password) {}
record JwtResponse(String token, Long id, String email, String nombreCompleto, String rol, Set<String> permisos) {}


record TokenDto(String token) {}