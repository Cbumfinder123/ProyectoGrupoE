package com.reptilg.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "tbl_producto")
@Getter @Setter
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "id_plataforma", nullable = false)
    private Plataforma plataforma;

    private Integer stock;

    @Column(nullable = false)
    private Double precio;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<DetalleVenta> detallesVenta; 

    private String descripcion;
}
