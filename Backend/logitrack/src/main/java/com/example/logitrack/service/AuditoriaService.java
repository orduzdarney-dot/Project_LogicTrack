package com.example.logitrack.service;

import com.example.logitrack.modelo.Auditoria;
import com.example.logitrack.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository repository;

    public List<Auditoria> obtenerTodos() {
        return repository.findAll();
    }

    public Auditoria obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Auditoria guardar(Auditoria auditoria) {
        return repository.save(auditoria);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}