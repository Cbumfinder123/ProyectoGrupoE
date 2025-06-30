package com.reptilg.repositories;

import com.reptilg.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findAllByOrderByNombreAsc();

    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.codigo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.categoria.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.proveedor.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.plataforma.nombre) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Producto> buscar(@Param("query") String query);

  
    @Query(value = "SELECT codigo FROM tbl_producto WHERE codigo LIKE 'PROD-%' ORDER BY CAST(SUBSTRING(codigo, 6) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String obtenerUltimoCodigo();
}
