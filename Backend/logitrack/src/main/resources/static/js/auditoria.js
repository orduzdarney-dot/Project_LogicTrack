// js/auditoria.js

// Escapa texto para que no se interprete como HTML
function escaparHtml(texto) {
    return String(texto)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
}

async function cargarAuditoria() {
    const tbody = document.getElementById('auditoria-table-body');
    if (!tbody) return;

    try {
        const auditorias = await apiRequest('/auditorias', 'GET');

        tbody.innerHTML = '';
        if (!auditorias || auditorias.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 2rem; color: var(--text-muted);">No hay registros de auditoría</td></tr>';
            return;
        }

        // Más recientes primero
        auditorias.sort((a, b) => (b.id || 0) - (a.id || 0));

        auditorias.forEach(aud => {
            // La API devuelve: tipoOperacion, entidadAfectada, idRegistroAfectado, nombreUsuario
            const accion = aud.tipoOperacion || 'N/A';

            let badgeClass = 'badge-info';
            if (accion === 'INSERT') badgeClass = 'badge-success';
            if (accion === 'DELETE') badgeClass = 'badge-danger';
            if (accion === 'UPDATE') badgeClass = 'badge-warning';

            const fecha = aud.fecha ? aud.fecha.replace('T', ' ').substring(0, 16) : 'N/A';
            const usuario = aud.nombreUsuario || 'Sistema';
            const entidad = aud.entidadAfectada
                ? `${aud.entidadAfectada} #${aud.idRegistroAfectado ?? ''}`
                : 'N/A';

            const antes = aud.valoresAnteriores ? 'Antes: ' + escaparHtml(aud.valoresAnteriores) : '-';
            const despues = aud.valoresNuevos ? 'Después: ' + escaparHtml(aud.valoresNuevos) : '-';

            tbody.innerHTML += `
                <tr>
                    <td style="font-size: 0.85rem;">${fecha}</td>
                    <td><strong>${escaparHtml(usuario)}</strong></td>
                    <td><span class="badge ${badgeClass}">${escaparHtml(accion)}</span></td>
                    <td>${escaparHtml(entidad)}</td>
                    <td style="font-size: 0.8rem; color: var(--text-muted); max-width: 200px; word-break: break-all;">
                        ${antes} <br>
                        ${despues}
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.warn('No se pudo cargar la auditoría:', error.message);
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color: var(--danger);">No se pudo cargar la auditoría: ' + escaparHtml(error.message) + '</td></tr>';
    }
}

// Solo los ADMIN pueden consultar la auditoría (evita el 403 para EMPLEADO)
if (document.getElementById('auditoria-table-body') &&
    localStorage.getItem(CONFIG.ROLE_KEY) === 'ADMIN') {
    cargarAuditoria();
}