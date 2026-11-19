/**
 * Función centralizada para realizar peticiones a la API
 * @param {string} endpoint - La ruta del endpoint (ej: '/productos')
 * @param {string} method - Método HTTP (GET, POST, PUT, DELETE)
 * @param {object|null} body - Datos a enviar (se convierte a JSON automáticamente)
 * @returns {Promise<any>} - La respuesta parseada o lanza un error
 */
async function apiRequest(endpoint, method = 'GET', body = null) {
    const token = localStorage.getItem(CONFIG.TOKEN_KEY);
    
    const headers = {
        'Content-Type': 'application/json'
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const options = {
        method,
        headers
    };

    if (body && method !== 'GET') {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${CONFIG.API_URL}${endpoint}`, options);

        // Manejo global de errores HTTP
        if (response.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            throw new Error('Sesión expirada. Por favor, inicia sesión nuevamente.');
        }

        if (response.status === 403) {
            throw new Error('Acceso denegado: No tienes permisos para realizar esta acción.');
        }

        if (!response.ok) {
            // Intentamos leer el JSON de error de nuestro ManejadorGlobalDeExcepciones
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Error del servidor: ${response.status}`);
        }

        // Si la respuesta es 204 (No Content, ej: DELETE exitoso), retornamos null
        if (response.status === 204) {
            return null;
        }

        return await response.json();

    } catch (error) {
        console.error(`[API Error] ${method} ${endpoint}:`, error);
        throw error;
    }
}