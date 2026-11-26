package com.example.logitrack.repository;

import com.example.logitrack.modelo.Movimiento;
import com.example.logitrack.modelo.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findTop10ByOrderByFechaDescIdDesc();
    long countByTipo(TipoMovimiento tipo);
}