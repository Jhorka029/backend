package com.iespp.app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    // Puede ser nulo si el usuario se registra exclusivamente con Google
    @Column(nullable = true)
    private String password;

    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    // Almacena el último JWT válido emitido. Si el token entrante no coincide, se deniega el acceso.
    @Column(name = "token_activa", length = 500)
    private String tokenActivo;

    // Identifica el origen de la cuenta: "LOCAL" o "GOOGLE"
    @Column(name = "proveedor_auth", nullable = false, length = 20)
    private String proveedorAuth;

    // =======================================================
    // NUEVO CAMPO AÑADIDO: Para el manejo del borrado lógico
    // =======================================================
    @Column(name = "estado", length = 20)
    private String estado = "Activo";

    // Para confirmar si el correo existe
    @Column(name = "cuenta_verificada")
    private Boolean cuentaVerificada = false;

    @Column(name = "codigo_verificacion", length = 64)
    private String codigoVerificacion;

    // Para el flujo de "Olvidé mi contraseña"
    @Column(name = "token_password", length = 100)
    private String tokenPassword;


    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;

    @Column(name = "fecha_bloqueo")
    private java.time.LocalDateTime fechaBloqueo;

    @Column(name = "expiracion_token_password")
    private java.time.LocalDateTime expiracionTokenPassword;

    @Column(name = "session_token", length = 64)
    private String sessionToken;



    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public String getTokenActivo() { return tokenActivo; }
    public void setTokenActivo(String tokenActivo) { this.tokenActivo = tokenActivo; }
    
    public String getProveedorAuth() { return proveedorAuth; }
    public void setProveedorAuth(String proveedorAuth) { this.proveedorAuth = proveedorAuth; }
    
    @JsonIgnore
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    @JsonProperty("rol")
    public String getRol() {
        if (roles != null && !roles.isEmpty()) {
            return roles.iterator().next().getNombre();
        }
        return null;
    }
    
    // =======================================================
    // NUEVOS GETTER Y SETTER PARA EL ESTADO
    // =======================================================
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }


    // Getters y Setters para las validaciones de correo y contraseña

    public Boolean isCuentaVerificada() {
        return cuentaVerificada;
    }

    public void setCuentaVerificada(Boolean cuentaVerificada) {
        this.cuentaVerificada = cuentaVerificada;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }

    public String getTokenPassword() {
        return tokenPassword;
    }

    public void setTokenPassword(String tokenPassword) {
        this.tokenPassword = tokenPassword;
    }

    public Integer getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(Integer intentosFallidos) { this.intentosFallidos = intentosFallidos; }

    public java.time.LocalDateTime getFechaBloqueo() { return fechaBloqueo; }
    public void setFechaBloqueo(java.time.LocalDateTime fechaBloqueo) { this.fechaBloqueo = fechaBloqueo; }

    public java.time.LocalDateTime getExpiracionTokenPassword() { return expiracionTokenPassword; }
    public void setExpiracionTokenPassword(java.time.LocalDateTime expiracionTokenPassword) { this.expiracionTokenPassword = expiracionTokenPassword; }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
}