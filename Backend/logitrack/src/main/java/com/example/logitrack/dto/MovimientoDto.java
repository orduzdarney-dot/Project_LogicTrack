package com.example.logitrack.dto;

import com.example.logitrack.modelo.Movimiento;
import com.example.logitrack.modelo.TipoMovimiento;

import java.time.LocalDateTime;

public record MovimientoDto (Long id, LocalDateTime fecha, TipoMovimiento tipo , Integer cantidad, String producto,
                             String usuario){
    public MovimientoDto(Movimiento m){
        this(m.getId(), m.getFecha(), m.getTipo(), m.getCantidad(), m.getProducto().getNombre(), m.getUsuario().getNombre());
    }
}




