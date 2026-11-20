// js/bodegas.js

// Referencias al DOM
const bodegasTableBody = document.getElementById('bodegas-table-body');
const bodegaForm = document.getElementById('bodega-form');
const modalBodega = document.getElementById('modal-bodega');
const closeModalBodegaBtn = document.getElementById('close-modal-bodega');

let bodegaEditandoId = null;

// 1. Cargar bodegas
async function cargarBodegas() {
    try {
        const bodegas = await apiRequest('/bodegas', 'GET');
        renderizarTablaBodegas(bodegas);
    } catch (error) {
        mostrarNotificacion(error.message, 'error');
    }
}

// 2. Renderizar tabla
function renderizarTablaBodegas(bodegas) {
    bodegasTableBody.innerHTML = '';
    
    if (!bodegas || bodegas.length === 0) {
        bodegasTableBody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 2rem; color: var(--text-muted);">No hay bodegas registradas</td></tr>';
        return;
    }

    const rol = localStorage.getItem(CONFIG.ROLE_KEY);

    bodegas.forEach(bodega => {
        const acciones = rol === 'ADMIN' ? `
            <button class="btn-action btn-edit" onclick="prepararEdicionBodega(${bodega.id}, '${bodega.nombre}', '${bodega.ubicacion || ''}', ${bodega.capacidad || 0}, '${bodega.encargado || ''}')">✏️</button>
            <button class="btn-action btn-delete" onclick="confirmarEliminacionBodega(${bodega.id})">🗑️</button>
        ` : '<span style="color: #9ca3af; font-size: 0.85rem;">Solo lectura</span>';

        const row = `
            <tr>
                <td>${bodega.id}</td>
                <td><strong>${bodega.nombre}</strong></td>
                <td> ${bodega.ubicacion || 'Sin ubicación'}</td>
                <td>${bodega.capacidad || 0} unidades</td>
                <td>${bodega.encargado || '-'}</td>
                <td>${acciones}</td>
            </tr>
        `;
        bodegasTableBody.innerHTML += row;
    });
}

// 3. Abrir modal para crear
window.abrirModalCrearBodega = () => {
    bodegaEditandoId = null;
    document.getElementById('modal-bodega-title').textContent = 'Nueva Bodega';
    bodegaForm.reset();
    modalBodega.style.display = 'flex';
};

// 4. Preparar edición
window.prepararEdicionBodega = (id, nombre, ubicacion, capacidad, encargado) => {
    bodegaEditandoId = id;
    document.getElementById('modal-bodega-title').textContent = 'Editar Bodega';
    document.getElementById('bodega-nombre').value = nombre;
    document.getElementById('bodega-ubicacion').value = ubicacion;
    document.getElementById('bodega-capacidad').value = capacidad;
    document.getElementById('bodega-encargado').value = encargado;
    modalBodega.style.display = 'flex';
};

// 5. Cerrar modal
closeModalBodegaBtn.addEventListener('click', () => {
    modalBodega.style.display = 'none';
});

// 6. Guardar (Crear o Actualizar)
bodegaForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const datos = {
        nombre: document.getElementById('bodega-nombre').value,
        ubicacion: document.getElementById('bodega-ubicacion').value,
        capacidad: parseInt(document.getElementById('bodega-capacidad').value),
        encargado: document.getElementById('bodega-encargado').value
    };

    try {
        if (bodegaEditandoId) {
            await apiRequest(`/bodegas/${bodegaEditandoId}`, 'PUT', datos);
            mostrarNotificacion('Bodega actualizada exitosamente', 'success');
        } else {
            await apiRequest('/bodegas', 'POST', datos);
            mostrarNotificacion('Bodega creada exitosamente', 'success');
        }
        modalBodega.style.display = 'none';
        cargarBodegas();
    } catch (error) {
        mostrarNotificacion(error.message, 'error');
    }
});

// 7. Eliminar
window.confirmarEliminacionBodega = async (id) => {
    if (confirm('¿Estás seguro de eliminar esta bodega? Esta acción quedará registrada en la auditoría.')) {
        try {
            await apiRequest(`/bodegas/${id}`, 'DELETE');
            mostrarNotificacion('Bodega eliminada correctamente', 'success');
            cargarBodegas();
        } catch (error) {
            mostrarNotificacion(error.message, 'error');
        }
    }
};

// Inicializar si estamos en la vista de bodegas
if (bodegasTableBody) {
    cargarBodegas();
}