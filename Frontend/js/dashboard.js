// js/dashboard.js

async function cargarDashboard() {
    try {
        // Hacemos las peticiones en paralelo para mayor velocidad
        const [productos, bodegas, movimientos] = await Promise.all([
            apiRequest('/productos', 'GET').catch(() => []),
            apiRequest('/bodegas', 'GET').catch(() => []),
            apiRequest('/movimientos', 'GET').catch(() => [])
        ]);

        // 1. Calcular indicadores
        const totalProductos = productos.length;
        const totalBodegas = bodegas.length;
        const totalMovimientos = movimientos.length;
        
        // Productos con stock bajo (menos de 10 unidades)
        const stockBajo = productos.filter(p => p.stock < 10).length;

        // 2. Actualizar el DOM
        document.getElementById('stat-productos').textContent = totalProductos;
        document.getElementById('stat-bodegas').textContent = totalBodegas;
        document.getElementById('stat-movimientos').textContent = totalMovimientos;
        document.getElementById('stat-stock-bajo').textContent = stockBajo;

        // 3. Renderizar últimos 5 movimientos
        const ultimosMovimientos = movimientos.slice(-5).reverse();
        const tbody = document.getElementById('dashboard-movimientos-body');
        tbody.innerHTML = '';

        if (ultimosMovimientos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; color: var(--text-muted);">Sin movimientos recientes</td></tr>';
        } else {
            ultimosMovimientos.forEach(mov => {
                let badgeClass = mov.tipo === 'ENTRADA' ? 'badge-success' : (mov.tipo === 'SALIDA' ? 'badge-danger' : 'badge-info');
                tbody.innerHTML += `
                    <tr>
                        <td><span class="badge ${badgeClass}">${mov.tipo}</span></td>
                        <td>${mov.producto?.nombre || 'Producto #' + mov.producto?.id}</td>
                        <td><strong>${mov.cantidad}</strong> und.</td>
                        <td style="font-size: 0.85rem; color: var(--text-muted);">${mov.fecha ? mov.fecha.replace('T', ' ').substring(0, 16) : 'Reciente'}</td>
                    </tr>
                `;
            });
        }

    } catch (error) {
        console.error('Error al cargar dashboard:', error);
    }
}

// Inicializar si estamos en el dashboard
if (document.getElementById('stat-productos')) {
    cargarDashboard();
}