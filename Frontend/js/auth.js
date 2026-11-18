// Verificar autenticación al cargar una página protegida
function requireAuth() {
    const token = localStorage.getItem(CONFIG.TOKEN_KEY);
    if (!token) {
        window.location.href = 'login.html';
    }
}

// Función para decodificar el payload del JWT y obtener el rol
function decodeJWT(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
}

// Manejar el envío del formulario de login
async function handleLogin(event) {
    event.preventDefault();
    
    const correo = document.getElementById('correo').value;
    const password = document.getElementById('password').value;
    const errorMsg = document.getElementById('login-error');
    const submitBtn = document.getElementById('btn-login');

    // Estado de carga
    submitBtn.disabled = true;
    submitBtn.textContent = 'Verificando...';
    errorMsg.textContent = '';

    try {
        // Llamada directa al endpoint de login (sin token aún)
        const response = await fetch(`${CONFIG.API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ correo, password })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Credenciales incorrectas');
        }

        const data = await response.json();
        const token = data.token;

        // Decodificar para obtener el rol y el nombre de usuario
        const payload = decodeJWT(token);
        const rol = payload ? (payload.rol || payload.role || 'EMPLEADO') : 'EMPLEADO';
        const username = payload ? (payload.sub || correo) : correo;

        // Guardar en localStorage
        localStorage.setItem(CONFIG.TOKEN_KEY, token);
        localStorage.setItem(CONFIG.ROLE_KEY, rol);
        localStorage.setItem(CONFIG.USER_KEY, username);

        // Redirigir al dashboard
        window.location.href = 'index.html';

    } catch (error) {
        errorMsg.textContent = error.message;
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Iniciar Sesión';
    }
}

// Manejar el cierre de sesión
function handleLogout() {
    localStorage.clear();
    window.location.href = 'login.html';
}

// Inicializar eventos si estamos en la página de login
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});