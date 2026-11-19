# 📦 LogiTrack — Sistema de Gestión y Auditoría de Bodegas
 
LogiTrack es un sistema para administrar bodegas, productos y movimientos de inventario (entradas, salidas y transferencias), con **trazabilidad completa** de los cambios realizados por cada usuario. Está compuesto por un **backend REST en Spring Boot** protegido con **JWT** y un **frontend web** en HTML, CSS y JavaScript puro.
 
---
 
## Tabla de contenido
 
1. [Descripción del proyecto](#descripción-del-proyecto)
2. [Tecnologías](#tecnologías)
3. [Estructura del proyecto](#estructura-del-proyecto)
4. [Requisitos previos](#requisitos-previos)
5. [Instalación y ejecución](#instalación-y-ejecución)
6. [Usuarios de prueba](#usuarios-de-prueba)
7. [Autenticación y roles](#autenticación-y-roles)
8. [Endpoints de la API](#endpoints-de-la-api)
9. [Ejemplos de uso](#ejemplos-de-uso)
10. [Manejo de errores](#manejo-de-errores)
11. [Auditoría automática](#auditoría-automática)
12. [Documentación Swagger](#documentación-swagger)
13. [Frontend](#frontend)
14. [Base de datos](#base-de-datos)
---
 
## Descripción del proyecto
 
LogiTrack S.A. administra varias bodegas distribuidas en distintas ciudades. Este sistema centraliza el control del inventario y reemplaza el manejo manual en hojas de cálculo, ofreciendo:
 
- **Gestión de bodegas**: registro, consulta, actualización y eliminación.
- **Gestión de productos**: CRUD completo, con consulta de productos de stock bajo.
- **Movimientos de inventario**: registro de `ENTRADA`, `SALIDA` y `TRANSFERENCIA`, asociados automáticamente al usuario autenticado.
- **Auditoría**: registro automático de operaciones `INSERT`, `UPDATE` y `DELETE`, con fecha, usuario y entidad afectada.
- **Seguridad**: autenticación con Spring Security + JWT y control de acceso por rol (`ADMIN` / `EMPLEADO`).
- **API documentada** con Swagger / OpenAPI 3.
- **Panel web** para operar el sistema desde el navegador.
---
 
## Tecnologías
 
| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.4.1 |
| Seguridad | Spring Security + JWT (jjwt 0.12.3) |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | MySQL 8 |
| Validación | Jakarta Bean Validation |
| Documentación | springdoc-openapi 2.8.4 (Swagger UI) |
| Utilidades | Lombok |
| Servidor | Tomcat embebido (puerto 8080) |
| Frontend | HTML5, CSS3, JavaScript (Fetch API) |
| Build | Maven (incluye Maven Wrapper) |
 
---
 
## Estructura del proyecto
 
```
Backend/logitrack/
├── pom.xml
└── src/main/
    ├── java/com/example/logitrack/
    │   ├── LogitrackApplication.java
    │   ├── config/          → OpenApiConfig, SpringContextHolder
    │   ├── controller/      → Auth, Bodega, Producto, Movimiento, Auditoria, Usuario
    │   ├── dto/             → LoginRequest
    │   ├── exception/       → ManejadorGlobalDeExcepciones, ErrorResponse, RecursoNoEncontradoException
    │   ├── modelo/          → Bodega, Producto, Movimiento, Usuario, Auditoria + enums
    │   ├── repository/      → Repositorios JPA
    │   ├── security/        → SecurityConfig, JwtUtils, JwtAuthenticationFilter,
    │   │                      CustomUserDetailsService, AuditoriaListener
    │   └── service/         → Lógica de negocio
    └── resources/
        ├── application.properties
        ├── schema.sql
        └── data.sql
 
Frontend/
├── login.html
├── index.html
├── css/   → login.css, styles.css
└── js/    → config.js, api.js, auth.js, dashboard.js,
             productos.js, bodegas.js, movimientos.js, auditoria.js
```
 
---
 
## Requisitos previos
 
- **JDK 21**
- **MySQL 8** en ejecución en `localhost:3306`
- **Maven** (opcional, el proyecto incluye `mvnw` / `mvnw.cmd`)
- Un navegador web moderno
- (Opcional) Un servidor estático para el frontend, por ejemplo la extensión *Live Server* de VS Code o `python -m http.server`
---
 
## Instalación y ejecución
 
### 1. Configurar la base de datos
 
Edita `src/main/resources/application.properties` con tus credenciales de MySQL:
 
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/logitrack_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=1234
server.port=8080
```
 
La base `logitrack_db` se crea automáticamente. Al arrancar, Spring ejecuta `schema.sql` (estructura de tablas) y `data.sql` (datos iniciales).
 
> ⚠️ `schema.sql` elimina y recrea las tablas en cada arranque, por lo que los datos se reinician cada vez que se inicia el backend.
 
### 2. Ejecutar el backend
 
Desde la carpeta `Backend/logitrack`:
 
```bash
# Linux / macOS
./mvnw spring-boot:run
 
# Windows
mvnw.cmd spring-boot:run
```
 
También puedes generar el JAR y ejecutarlo:
 
```bash
./mvnw clean package
java -jar target/logitrack-0.0.1-SNAPSHOT.jar
```
 
El backend queda disponible en **http://localhost:8080**.
 
### 3. Ejecutar el frontend
 
El frontend consume la API en `http://localhost:8080` (configurable en `Frontend/js/config.js`):
 
```js
const CONFIG = {
    API_URL: "http://localhost:8080",
    ...
};
```
 
Sirve la carpeta `Frontend/` con cualquier servidor estático:
 
```bash
cd Frontend
python -m http.server 5500
```
 
Luego abre **http://localhost:5500/login.html**.
 
---
 
## Usuarios de prueba
 
Cargados desde `data.sql`. La contraseña de todos es **`12345`**.
 
| Rol | Correo | Nombre de usuario |
|-----|--------|-------------------|
| ADMIN | `admin@logitrack.com` | `admin1` |
| EMPLEADO | `empleado@logitrack.com` | `juan_perez` |
| EMPLEADO | `maria@logitrack.com` | `maria_gomez` |
 
---
 
## Autenticación y roles
 
El inicio de sesión se hace con **correo y contraseña** en `POST /auth/login`. La respuesta contiene un token JWT que debe enviarse en cada petición protegida:
 
```
Authorization: Bearer <token>
```
 
- El token se firma con **HMAC-SHA** y tiene una vigencia de **24 horas**.
- El payload incluye el nombre de usuario (`sub`), el rol (`role`), la fecha de emisión (`iat`) y la de expiración (`exp`).
- La sesión es *stateless*: el servidor no guarda estado entre peticiones.
### Permisos por rol
 
| Recurso | ADMIN | EMPLEADO |
|---------|:-----:|:--------:|
| `/auth/**`, Swagger | Público | Público |
| `GET / POST / PUT` en `/bodegas`, `/productos`, `/usuarios` | ✅ | ✅ |
| `GET / POST` en `/movimientos` | ✅ | ✅ |
| `DELETE` en `/bodegas`, `/productos`, `/usuarios` | ✅ | ❌ |
| `/auditorias/**` | ✅ | ❌ |
 
---
 
## Endpoints de la API
 
### Autenticación
 
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/login` | Inicia sesión y devuelve el token JWT |
 
### Bodegas
 
| Método | Ruta | Descripción | Rol |
|--------|------|-------------|-----|
| GET | `/bodegas` | Lista todas las bodegas | ADMIN, EMPLEADO |
| GET | `/bodegas/{id}` | Consulta una bodega | ADMIN, EMPLEADO |
| POST | `/bodegas` | Crea una bodega | ADMIN, EMPLEADO |
| PUT | `/bodegas/{id}` | Actualiza una bodega | ADMIN, EMPLEADO |
| DELETE | `/bodegas/{id}` | Elimina una bodega | ADMIN |
 
### Productos
 
| Método | Ruta | Descripción | Rol |
|--------|------|-------------|-----|
| GET | `/productos` | Lista todos los productos | ADMIN, EMPLEADO |
| GET | `/productos/{id}` | Consulta un producto | ADMIN, EMPLEADO |
| GET | `/productos/stock-bajo` | Productos con stock menor a 10 | ADMIN, EMPLEADO |
| POST | `/productos` | Crea un producto | ADMIN, EMPLEADO |
| PUT | `/productos/{id}` | Actualiza un producto | ADMIN, EMPLEADO |
| DELETE | `/productos/{id}` | Elimina un producto | ADMIN |
 
### Movimientos
 
| Método | Ruta | Descripción | Rol |
|--------|------|-------------|-----|
| GET | `/movimientos` | Lista el historial de movimientos | ADMIN, EMPLEADO |
| GET | `/movimientos/{id}` | Consulta un movimiento | ADMIN, EMPLEADO |
| POST | `/movimientos` | Registra una entrada, salida o transferencia | ADMIN, EMPLEADO |
 
### Usuarios
 
| Método | Ruta | Descripción | Rol |
|--------|------|-------------|-----|
| GET | `/usuarios` | Lista los usuarios | ADMIN, EMPLEADO |
| GET | `/usuarios/{id}` | Consulta un usuario | ADMIN, EMPLEADO |
| POST | `/usuarios` | Crea un usuario (la contraseña se guarda cifrada con BCrypt) | ADMIN, EMPLEADO |
| PUT | `/usuarios/{id}` | Actualiza un usuario | ADMIN, EMPLEADO |
| DELETE | `/usuarios/{id}` | Elimina un usuario | ADMIN |
 
### Auditoría
 
| Método | Ruta | Descripción | Rol |
|--------|------|-------------|-----|
| GET | `/auditorias` | Lista todos los registros de auditoría | ADMIN |
| GET | `/auditorias/{id}` | Consulta un registro de auditoría | ADMIN |
 
---
 
## Ejemplos de uso
 
### Iniciar sesión
 
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo": "admin@logitrack.com", "password": "12345"}'
```
 
Respuesta:
 
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbjEiLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3NjMwMDAwMDAsImV4cCI6MTc2MzA4NjQwMH0.firma"
}
```
 
Payload decodificado del token:
 
```json
{
  "sub": "admin1",
  "role": "ADMIN",
  "iat": 1763000000,
  "exp": 1763086400
}
```
 
### Usar el token
 
```bash
curl http://localhost:8080/productos \
  -H "Authorization: Bearer <token>"
```
 
### Crear un producto
 
```bash
curl -X POST http://localhost:8080/productos \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
        "nombre": "Webcam HD",
        "categoria": "Tecnología",
        "stock": 20,
        "precio": 49.90
      }'
```
 
### Crear una bodega
 
```bash
curl -X POST http://localhost:8080/bodegas \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
        "nombre": "Bodega Norte",
        "ubicacion": "Av. Principal 100, Quito",
        "capacidad": 2000,
        "encargado": "Pedro Salazar"
      }'
```
 
### Registrar un movimiento
 
El **usuario responsable** y la **fecha** se asignan automáticamente a partir del token; no es necesario enviarlos.
 
**Entrada** (llega mercancía a una bodega):
 
```json
{
  "tipo": "ENTRADA",
  "cantidad": 30,
  "producto": { "id": 1 },
  "bodegaDestino": { "id": 1 }
}
```
 
**Salida** (sale mercancía de una bodega):
 
```json
{
  "tipo": "SALIDA",
  "cantidad": 5,
  "producto": { "id": 1 },
  "bodegaOrigen": { "id": 1 }
}
```
 
**Transferencia** (entre dos bodegas):
 
```json
{
  "tipo": "TRANSFERENCIA",
  "cantidad": 10,
  "producto": { "id": 1 },
  "bodegaOrigen": { "id": 1 },
  "bodegaDestino": { "id": 2 }
}
```
 
Reglas de stock:
- `ENTRADA` suma la cantidad al stock del producto.
- `SALIDA` resta la cantidad; si no hay stock suficiente, se rechaza con error 400.
- `TRANSFERENCIA` no altera el stock total del producto.
### Consultar productos con stock bajo
 
```bash
curl http://localhost:8080/productos/stock-bajo \
  -H "Authorization: Bearer <token>"
```
 
### Consultar auditorías (solo ADMIN)
 
```bash
curl http://localhost:8080/auditorias \
  -H "Authorization: Bearer <token>"
```
 
Ejemplo de registro:
 
```json
{
  "id": 1,
  "tipoOperacion": "UPDATE",
  "fecha": "2026-11-19T10:25:43",
  "entidadAfectada": "Producto",
  "idRegistroAfectado": 3,
  "valoresAnteriores": null,
  "valoresNuevos": null,
  "nombreUsuario": "admin1"
}
```
 
---
 
## Manejo de errores
 
El backend centraliza los errores en `ManejadorGlobalDeExcepciones` (`@RestControllerAdvice`) y responde siempre en JSON.
 
| Código | Situación | Ejemplo de respuesta |
|--------|-----------|----------------------|
| 400 | Error de negocio, validación o JSON mal formado | `{"status":400,"error":"Error de Negocio","message":"No hay suficiente stock. Stock actual: 5", ...}` |
| 401 | Token ausente, inválido o expirado / credenciales incorrectas | `{"error":"Contraseña incorrecta"}` |
| 403 | El rol no tiene permiso para el recurso | — |
| 404 | Recurso no encontrado | `{"status":404,"error":"Recurso No Encontrado","message":"Producto no encontrado con ID: 99", ...}` |
| 500 | Error interno inesperado | `{"status":500,"error":"Error Interno del Servidor", ...}` |
 
Estructura de `ErrorResponse`:
 
```json
{
  "timestamp": "2026-11-19T10:30:00",
  "status": 404,
  "error": "Recurso No Encontrado",
  "message": "Producto no encontrado con ID: 99",
  "path": "/productos/99"
}
```
 
Validaciones principales de las entidades:
 
| Entidad | Campo | Regla |
|---------|-------|-------|
| Producto | `stock` | Mínimo 0 |
| Producto | `precio` | Mínimo 0.0 |
| Producto | `nombre` | Único |
| Movimiento | `cantidad` | Obligatoria, mínimo 1 |
| Movimiento | `tipo` | Obligatorio (`ENTRADA`, `SALIDA`, `TRANSFERENCIA`) |
| Usuario | `nombre` | Obligatorio, entre 3 y 50 caracteres |
| Usuario | `correo` | Obligatorio, formato de correo válido |
| Usuario | `password` | Obligatoria, entre 5 y 100 caracteres |
 
---
 
## Auditoría automática
 
La auditoría se implementa con un **JPA Entity Listener** (`AuditoriaListener`) que intercepta los eventos del ciclo de vida de la entidad:
 
| Evento JPA | Operación registrada |
|------------|----------------------|
| `@PostPersist` | `INSERT` |
| `@PostUpdate` | `UPDATE` |
| `@PreRemove` | `DELETE` |
 
Por cada evento se guarda en la tabla `auditoria`: tipo de operación, fecha y hora, nombre del usuario (tomado del token JWT; `SYSTEM` si no hay sesión), entidad afectada e ID del registro. El nombre de usuario se guarda como texto para conservar el historial aunque el usuario sea eliminado después.
 
Actualmente el listener está activo sobre la entidad **`Producto`**.
 
---
 
## Documentación Swagger
 
Con el backend en ejecución:
 
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
### Probar endpoints protegidos desde Swagger
 
1. Ejecuta `POST /auth/login` y copia el valor de `token` de la respuesta.
2. Pulsa el botón **Authorize** 🔓 (arriba a la derecha).
3. Pega el token (sin la palabra `Bearer`) y confirma con **Authorize**.
4. Ya puedes ejecutar cualquier endpoint protegido con **Try it out**.
---
 
## Frontend
 
Aplicación web en `Frontend/` que consume la API REST.
 
| Archivo | Función |
|---------|---------|
| `login.html` | Pantalla de inicio de sesión |
| `index.html` | Aplicación principal (dashboard, productos, bodegas, movimientos, auditoría) |
| `js/config.js` | URL de la API y claves de `localStorage` |
| `js/api.js` | Función `apiRequest` que agrega el token JWT y gestiona los errores 401 / 403 |
| `js/auth.js` | Login, logout, decodificación del JWT y protección de rutas |
| `js/dashboard.js` | Indicadores y últimos movimientos |
| `js/productos.js` | CRUD de productos |
| `js/bodegas.js` | CRUD de bodegas |
| `js/movimientos.js` | Historial y registro de movimientos |
| `js/auditoria.js` | Consulta del registro de auditoría |
 
Comportamiento destacado:
 
- El token, el rol y el nombre de usuario se guardan en `localStorage`.
- Si el token expira (401), la sesión se cierra y se redirige al login.
- Los botones de editar y eliminar solo se muestran a usuarios **ADMIN**; los empleados ven los listados en modo lectura.
- El formulario de movimientos muestra los campos de bodega de origen y destino según el tipo de movimiento seleccionado.
---
 
## Base de datos
 
Motor: **MySQL 8** · Base: `logitrack_db`
 
| Tabla | Descripción |
|-------|-------------|
| `usuario` | Usuarios del sistema (nombre, correo, contraseña cifrada, rol) |
| `producto` | Catálogo de productos (nombre, categoría, stock, precio) |
| `bodega` | Bodegas (nombre, ubicación, capacidad, encargado) |
| `movimiento` | Movimientos de inventario (fecha, tipo, cantidad, usuario, producto, bodega origen y destino) |
| `auditoria` | Registro de operaciones (tipo, fecha, entidad, ID del registro, valores, usuario) |
 
Relaciones principales:
 
- `movimiento.usuario_id` → `usuario.id`
- `movimiento.producto_id` → `producto.id`
- `movimiento.bodega_origen_id` → `bodega.id`
- `movimiento.bodega_destino_id` → `bodega.id`
Datos iniciales (`data.sql`): 3 usuarios, 3 bodegas (Quito, Medellín y Loja) y 8 productos de ejemplo.
 
---
 
## Licencia
 
Proyecto académico — uso educativo.
 
