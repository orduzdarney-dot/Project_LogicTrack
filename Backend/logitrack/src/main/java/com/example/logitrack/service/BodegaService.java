package com.example.logitrack.service;

import com.example.logitrack.modelo.Bodega;
import com.example.logitrack.repository.BodegaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BodegaService {

    @Autowired
    private BodegaRepository repository;

    public List<Bodega> obtenerTodos() {
        return repository.findAll();
    }

    public Bodega obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Bodega guardar(Bodega bodega) {
        return repository.save(bodega);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}