package com.example.logitrack.security;

import com.example.logitrack.config.SpringContextHolder;
import com.example.logitrack.modelo.Auditoria;
import com.example.logitrack.modelo.TipoOperacion;
import com.example.logitrack.repository.AuditoriaRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class AuditoriaListener {

    @PostPersist
    public void postPersist(Object entity) {
        guardarAuditoria(entity, TipoOperacion.INSERT);
    }

    @PostUpdate
    public void postUpdate(Object entity) {
        guardarAuditoria(entity, TipoOperacion.UPDATE);
    }

    @PreRemove
    public void preRemove(Object entity) {
        guardarAuditoria(entity, TipoOperacion.DELETE);
    }

    private void guardarAuditoria(Object entity, TipoOperacion tipo) {
        try {
            // 1. Obtener el nombre del usuario desde el Token JWT (o "SYSTEM" si no hay sesión)
            String nombreUsuario = "SYSTEM";
            try {
                nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
            } catch (Exception e) {
                // Ignorar si no hay contexto de seguridad (ej. al iniciar la app)
            }

            // 2. Obtener el repositorio de auditoría
            AuditoriaRepository auditoriaRepo = SpringContextHolder.getBean(AuditoriaRepository.class);

            // 3. Obtener el ID de la entidad usando Reflexión
            Long idRegistro = null;
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idRegistro = (Long) idField.get(entity);

            // 4. Crear y guardar la auditoría (¡Sin consultas extra que causen conflicto!)
            Auditoria auditoria = new Auditoria();
            auditoria.setTipoOperacion(tipo);
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setEntidadAfectada(entity.getClass().getSimpleName());
            auditoria.setIdRegistroAfectado(idRegistro);
            auditoria.setNombreUsuario(nombreUsuario); // ✅ Guardamos el string directamente

            auditoriaRepo.save(auditoria);

            System.out.println(" Auditoría registrada: " + tipo + " en " + entity.getClass().getSimpleName() + " ID: " + idRegistro + " por " + nombreUsuario);

        } catch (Exception e) {
            System.err.println(" error al guardar auditoría: " + e.getMessage());
        }
    }
}