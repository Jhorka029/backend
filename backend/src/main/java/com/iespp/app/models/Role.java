package com.iespp.app.models;

import jakarta.persistence.*;



@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    // NUEVA COLUMNA: Borrado lógico
    @Column(name = "estado_rol", nullable = false)
    private Boolean estadoRol = true;

    // =========================================
    // NUEVA RELACIÓN: LISTA DE PERMISOS
    // =========================================
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permisos", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permiso")
    private java.util.Set<String> permisos = new java.util.HashSet<>();

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getEstadoRol() { return estadoRol; }
    public void setEstadoRol(Boolean estadoRol) { this.estadoRol = estadoRol; }

    // Genera el Getter y Setter para los permisos
    public java.util.Set<String> getPermisos() { return permisos; }
    public void setPermisos(java.util.Set<String> permisos) { this.permisos = permisos; }
    
}