// js/movimientos.js

// Referencias al DOM
const movimientosTableBody = document.getElementById('movimientos-table-body');
const movimientoForm = document.getElementById('movimiento-form');
const modalMovimiento = document.getElementById('modal-movimiento');
const closeModalMovBtn = document.getElementById('close-modal-mov');

// Dropdowns
const selectProducto = document.getElementById('mov-producto');
const selectBodegaOrigen = document.getElementById('mov-bodega-origen');
const selectBodegaDestino = document.getElementById('mov-bodega-destino');
const selectTipo = document.getElementById('mov-tipo');

// 1. Cargar movimientos (Historial)
async function cargarMovimientos() {
    try {
        // Asumimos que existe GET /movimientos. Si tu backend usa otro nombre, ajústalo aquí.
        const movimientos = await apiRequest('/movimientos', 'GET');
        renderizarTablaMovimientos(movimientos);
    } catch (error) {
        console.warn('No se pudieron cargar los movimientos:', error.message);
        movimientosTableBody.innerHTML = '<tr><td colspan="6" style="text-align:center; color: var(--text-muted);">No hay movimientos registrados o el endpoint no está disponible.</td></tr>';
    }
}

// 2. Cargar datos para los dropdowns (Productos y Bodegas)
async function cargarDatosDropdowns() {
    try {
        const [productos, bodegas] = await Promise.all([
            apiRequest('/productos', 'GET'),
            apiRequest('/bodegas', 'GET')
        ]);

        // Llenar Productos
        selectProducto.innerHTML = '<option value="">Seleccione un producto...</option>';
        productos.forEach(p => {
            selectProducto.innerHTML += `<option value="${p.id}">${p.nombre} (Stock: ${p.stock})</option>`;
        });

        // Llenar Bodegas
        const opcionVacia = '<option value="">Seleccione una bodega...</option>';
        selectBodegaOrigen.innerHTML = opcionVacia;
        selectBodegaDestino.innerHTML = opcionVacia;
        
        bodegas.forEach(b => {
            const optionHTML = `<option value="${b.id}">${b.nombre}</option>`;
            selectBodegaOrigen.innerHTML += optionHTML;
            selectBodegaDestino.innerHTML += optionHTML;
        });

    } catch (error) {
        mostrarNotificacion('Error al cargar datos para el formulario', 'error');
    }
}

// 3. Renderizar tabla de historial
function renderizarTablaMovimientos(movimientos) {
    movimientosTableBody.innerHTML = '';
    
    if (!movimientos || movimientos.length === 0) {
        movimientosTableBody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 2rem; color: var(--text-muted);">No hay movimientos registrados</td></tr>';
        return;
    }

    movimientos.forEach(mov => {
        // Determinar colores y textos según tipo
        let badgeClass = '';
        let tipoTexto = mov.tipo || mov.tipoMovimiento; // Ajusta según tu backend
        
        if (tipoTexto === 'ENTRADA') badgeClass = 'badge-success';
        else if (tipoTexto === 'SALIDA') badgeClass = 'badge-danger';
        else badgeClass = 'badge-info';

        const row = `
            <tr>
                <td>#${mov.id}</td>
                <td><span class="badge ${badgeClass}">${tipoTexto}</span></td>
                <td>${mov.productoNombre || 'Producto ID: ' + mov.productoId}</td>
                <td><strong>${mov.cantidad}</strong> und.</td>
                <td>${mov.bodegaNombre || 'Bodega ID: ' + mov.bodegaId}</td>
                <td style="font-size: 0.85rem; color: var(--text-muted);">${mov.fecha || 'Reciente'}</td>
            </tr>
        `;
        movimientosTableBody.innerHTML += row;
    });
}

// 4. Abrir modal y preparar formulario
window.abrirModalMovimiento = async () => {
    movimientoForm.reset();
    modalMovimiento.style.display = 'flex';
    await cargarDatosDropdowns(); // Cargar productos y bodegas frescos
    actualizarCamposPorTipo(); // Ajustar visibilidad de campos
};

// 5. Cerrar modal
closeModalMovBtn.addEventListener('click', () => {
    modalMovimiento.style.display = 'none';
});

// 6. Cambiar campos según el tipo de movimiento
selectTipo.addEventListener('change', actualizarCamposPorTipo);

function actualizarCamposPorTipo() {
    const tipo = selectTipo.value;
    const grupoOrigen = document.getElementById('grupo-origen');
    const grupoDestino = document.getElementById('grupo-destino');

    // Ocultar todos primero
    grupoOrigen.style.display = 'none';
    grupoDestino.style.display = 'none';
    selectBodegaOrigen.required = false;
    selectBodegaDestino.required = false;

    if (tipo === 'ENTRADA') {
        grupoDestino.style.display = 'block';
        selectBodegaDestino.required = true;
    } else if (tipo === 'SALIDA') {
        grupoOrigen.style.display = 'block';
        selectBodegaOrigen.required = true;
    } else if (tipo === 'TRANSFERENCIA') {
        grupoOrigen.style.display = 'block';
        grupoDestino.style.display = 'block';
        selectBodegaOrigen.required = true;
        selectBodegaDestino.required = true;
    }
}

// 7. Guardar Movimiento
movimientoForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const tipo = selectTipo.value;
      const datos = {
       tipo: tipo,
       producto: { id: parseInt(selectProducto.value) },
       cantidad: parseInt(document.getElementById('mov-cantidad').value)
   };

if (tipo === 'ENTRADA' || tipo === 'TRANSFERENCIA') {
    datos.bodegaDestinoId = parseInt(selectBodegaDestino.value);
}
if (tipo === 'SALIDA' || tipo === 'TRANSFERENCIA') {
    datos.bodegaOrigenId = parseInt(selectBodegaOrigen.value);
}

    try {
        await apiRequest('/movimientos', 'POST', datos);
        mostrarNotificacion('Movimiento registrado exitosamente', 'success');
        modalMovimiento.style.display = 'none';
        cargarMovimientos(); // Recargar historial
    } catch (error) {
        mostrarNotificacion(error.message, 'error');
    }
});

// Inicializar si estamos en la vista
if (movimientosTableBody) {
    cargarMovimientos();
}