package com.example.logitrack.controller;


import com.example.logitrack.modelo.TipoMovimiento;
import com.example.logitrack.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private MovimientoRepository repository;

    @GetMapping("/movimientos")
    public ResponseEntity<Map<String,Object>> resumenMovimiento(){
        Map<String,Long> porTipo = new LinkedHashMap<>();
        for (TipoMovimiento tipo : TipoMovimiento.values()){
            porTipo.put(tipo.name(),repository.countByTipo(tipo));

        }

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("totalMovimientos", repository.count());
        resumen.put("movimientosPorTipo", porTipo);
        return ResponseEntity.ok(resumen);
    }
}
