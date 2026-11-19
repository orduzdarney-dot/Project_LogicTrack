// js/auditoria.js

async function cargarAuditoria() {
    const tbody = document.getElementById('auditoria-table-body');
    if (!tbody) return;

    try {
        // Ajusta '/auditorias' si tu endpoint tiene otro nombre (ej: '/logs')
        const auditorias = await apiRequest('/auditorias', 'GET');
        
        tbody.innerHTML = '';
        if (!auditorias || auditorias.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 2rem; color: var(--text-muted);">No hay registros de auditoría</td></tr>';
            return;
        }

        auditorias.forEach(aud => {
            // Determinar color según la acción
            let badgeClass = 'badge-info';
            if (aud.accion === 'INSERT' || aud.accion === 'CREATE') badgeClass = 'badge-success';
            if (aud.accion === 'DELETE') badgeClass = 'badge-danger';
            if (aud.accion === 'UPDATE') badgeClass = 'badge-warning'; // Asegúrate de tener .badge-warning en CSS

            tbody.innerHTML += `
                <tr>
                    <td style="font-size: 0.85rem;">${aud.fecha ? aud.fecha.replace('T', ' ').substring(0, 16) : 'N/A'}</td>
                    <td><strong>${aud.usuarioNombre || aud.usuario || 'Sistema'}</strong></td>
                    <td><span class="badge ${badgeClass}">${aud.accion || aud.tipoAccion}</span></td>
                    <td>${aud.entidad || 'Entidad'}</td>
                    <td style="font-size: 0.8rem; color: var(--text-muted); max-width: 200px; word-break: break-all;">
                        ${aud.valoresAnteriores ? 'Antes: ' + aud.valoresAnteriores : '-'} <br>
                        ${aud.valoresNuevos ? 'Después: ' + aud.valoresNuevos : '-'}
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.warn('No se pudo cargar la auditoría:', error.message);
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color: var(--danger);">Error al cargar auditoría (verifica el endpoint)</td></tr>';
    }
}

if (document.getElementById('auditoria-table-body')) {
    cargarAuditoria();
}