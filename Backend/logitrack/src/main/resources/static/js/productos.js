// js/productos.js

// Referencias al DOM
const productosContent = document.getElementById('productos-content');
const productosTableBody = document.getElementById('productos-table-body');
const productoForm = document.getElementById('producto-form');
const modalProducto = document.getElementById('modal-producto');
const closeModalBtn = document.getElementById('close-modal');

let productoEditandoId = null;

// 1. Cargar productos al iniciar
async function cargarProductos() {
    try {
        const productos = await apiRequest('/productos', 'GET');
        renderizarTabla(productos);
    } catch (error) {
        mostrarNotificacion(error.message, 'error');
    }
}

// 2. Renderizar la tabla
function renderizarTabla(productos) {
    productosTableBody.innerHTML = '';
    
    if (productos.length === 0) {
        productosTableBody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 2rem;">No hay productos registrados</td></tr>';
        return;
    }

    const rol = localStorage.getItem(CONFIG.ROLE_KEY);

    productos.forEach(prod => {
        const acciones = rol === 'ADMIN' ? `
            <button class="btn-action btn-edit" onclick="prepararEdicion(${prod.id}, '${prod.nombre}', '${prod.categoria}', ${prod.stock}, ${prod.precio})">✏️</button>
            <button class="btn-action btn-delete" onclick="confirmarEliminacion(${prod.id})">🗑️</button>
        ` : '<span style="color: #9ca3af; font-size: 0.85rem;">Solo lectura</span>';

        const row = `
            <tr>
                <td>${prod.id}</td>
                <td><strong>${prod.nombre}</strong></td>
                <td><span class="badge">${prod.categoria}</span></td>
                <td>${prod.stock}</td>
                <td>$${parseFloat(prod.precio).toFixed(2)}</td>
                <td>${acciones}</td>
            </tr>
        `;
        productosTableBody.innerHTML += row;
    });
}

// 3. Abrir modal (Crear o Editar)
window.abrirModalCrear = () => {
    productoEditandoId = null;
    document.getElementById('modal-title').textContent = 'Nuevo Producto';
    productoForm.reset();
    modalProducto.style.display = 'flex';
};

window.prepararEdicion = (id, nombre, categoria, stock, precio) => {
    productoEditandoId = id;
    document.getElementById('modal-title').textContent = 'Editar Producto';
    document.getElementById('prod-nombre').value = nombre;
    document.getElementById('prod-categoria').value = categoria;
    document.getElementById('prod-stock').value = stock;
    document.getElementById('prod-precio').value = precio;
    modalProducto.style.display = 'flex';
};

// 4. Cerrar modal
closeModalBtn.addEventListener('click', () => {
    modalProducto.style.display = 'none';
});

// 5. Guardar (Crear o Actualizar)
productoForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const datos = {
        nombre: document.getElementById('prod-nombre').value,
        categoria: document.getElementById('prod-categoria').value,
        stock: parseInt(document.getElementById('prod-stock').value),
        precio: parseFloat(document.getElementById('prod-precio').value)
    };

    try {
        if (productoEditandoId) {
            await apiRequest(`/productos/${productoEditandoId}`, 'PUT', datos);
            mostrarNotificacion('Producto actualizado exitosamente', 'success');
        } else {
            await apiRequest('/productos', 'POST', datos);
            mostrarNotificacion('Producto creado exitosamente', 'success');
        }
        modalProducto.style.display = 'none';
        cargarProductos(); // Recargar tabla
    } catch (error) {
        mostrarNotificacion(error.message, 'error');
    }
});

// 6. Eliminar
window.confirmarEliminacion = async (id) => {
    if (confirm('¿Estás seguro de eliminar este producto? Esta acción quedará registrada en la auditoría.')) {
        try {
            await apiRequest(`/productos/${id}`, 'DELETE');
            mostrarNotificacion('Producto eliminado correctamente', 'success');
            cargarProductos();
        } catch (error) {
            mostrarNotificacion(error.message, 'error');
        }
    }
};

// 7. Utilidad: Notificaciones
function mostrarNotificacion(mensaje, tipo) {
    // Creamos una notificación temporal flotante
    const notif = document.createElement('div');
    notif.className = `notificacion notificacion-${tipo}`;
    notif.textContent = mensaje;
    document.body.appendChild(notif);
    
    setTimeout(() => {
        notif.style.opacity = '0';
        setTimeout(() => notif.remove(), 300);
    }, 3000);
}

// Inicializar si estamos en la vista de productos
if (productosContent) {
    cargarProductos();
}