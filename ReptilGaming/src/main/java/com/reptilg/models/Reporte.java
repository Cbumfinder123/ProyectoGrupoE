package com.reptilg.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_reporte")
@Getter @Setter
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 20)
    private String tipo; 
    
    @Column(nullable = false, length = 20)
    private String accion; 
    
    @Column(nullable = false, length = 255)
    private String detalle;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(name = "entidad_id")
    private Integer entidadId;
    
    @Column(name = "codigo_entidad", length = 20)
    private String codigoEntidad;
    
    @Column(length = 100)
    private String usuario;
}