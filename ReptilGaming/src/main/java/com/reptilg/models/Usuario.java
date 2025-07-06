package com.reptilg.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "tb_usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_usua")
    private Integer id;

    @Column(name = "nom_usua")
    private String nombres;

    @Column(name = "ape_usua")
    private String apellidos;

    @Column(name = "user_usua", unique = true)
    private String usuario;

    @Column(name = "pswd_usua")
    private String password;

    @Column(name = "fnac_usua")
    private LocalDate fechaNacimiento;

    @Column(name = "idtipo")
    private Integer tipo; 

    @Column(name = "est_usua")
    private Boolean estado;

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }
}
