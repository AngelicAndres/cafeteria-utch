# Cafetería UTCh: sistema de pedidos (Evidencia 6, Equipo 4)

Prototipo funcional mínimo del **Caso 4: Sistema de pedidos para cafetería universitaria**.
Arquitectura **cliente-servidor con backend monolítico en capas** que expone una API REST.

```
Angular (SPA) --HTTP/JSON + JWT--> Spring Boot (REST)
                                     |- Controller  (presentación / API)
                                     |- Service     (lógica de negocio: RN1-RN5)
                                     |- Repository  (Spring Data JPA)
                                     |- Security    (Spring Security + filtro JWT)
                                     '- H2 (archivo ./data/cafeteria)
```

Flujo obligatorio: **Consultar menú → seleccionar → registrar pedido → calcular total → actualizar estado → consultar.**

## Estructura

```
cafeteria-utch/
├── backend/    Spring Boot 3.5 · Java 21 · JPA · Spring Security + JWT · H2
├── frontend/   Angular 22 (standalone + signals)
└── postman/    Colección con 30 peticiones y 53 aserciones automáticas
```

## 1. Backend

Requisitos: **JDK 21** y Maven (IntelliJ ya trae Maven integrado).

```bash
cd backend
mvn spring-boot:run          # o ejecutar CafeteriaApplication desde IntelliJ
mvn test                     # pruebas unitarias + prueba de integración del flujo completo
```

- API: `http://localhost:8080/api`
- Consola H2: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/cafeteria`, usuario `sa`, sin contraseña)
- Al primer arranque se cargan los datos semilla (usuarios y 8 productos; *Jugo de naranja* queda **no disponible** para probar RN3).
- Para reiniciar la BD: detener el backend y borrar la carpeta `backend/data/`.

### Usuarios de prueba

| Rol      | Email              | Contraseña   |
|----------|--------------------|--------------|
| Cliente  | cliente@utch.mx    | cliente123   |
| Cliente  | cliente2@utch.mx   | cliente123   |
| Personal | personal@utch.mx   | personal123  |
| Admin    | admin@utch.mx      | admin123     |

### Endpoints

| Método | Ruta                                  | Rol               | Función                        |
|--------|---------------------------------------|-------------------|--------------------------------|
| POST   | /api/auth/login                       | Público           | Obtener JWT                    |
| GET    | /api/productos                        | Autenticados      | Consultar menú                 |
| POST   | /api/productos                        | ADMIN             | Crear producto                 |
| PUT    | /api/productos/{id}                   | ADMIN             | Editar producto                |
| PATCH  | /api/productos/{id}/disponibilidad    | ADMIN             | Cambiar disponibilidad         |
| POST   | /api/pedidos                          | CLIENTE           | Registrar pedido (calcula total) |
| GET    | /api/pedidos/mis-pedidos              | CLIENTE           | Ver mis pedidos y estado       |
| GET    | /api/pedidos/{id}                     | CLIENTE / PERSONAL| Consultar un pedido            |
| GET    | /api/pedidos?estado=                  | PERSONAL          | Ver pedidos por estado         |
| PATCH  | /api/pedidos/{id}/estado              | PERSONAL          | Avanzar estado                 |

Códigos de error: `400` regla de negocio o validación (RN1, RN2, RN3) · `401` sin token o credenciales incorrectas · `403` rol sin permiso · `404` no existe · `409` transición de estado inválida (RN4).

## 2. Frontend

Requisitos: Node 20+ y Angular CLI (`npm i -g @angular/cli`).

```bash
cd frontend
npm install
ng serve                     # http://localhost:4200
ng test                      # pruebas unitarias (Vitest)
```

Pantallas: Login · Menú + Carrito (Cliente) · Mis pedidos (Cliente) · Panel del personal · Gestión de productos (Admin).

## 3. Postman

1. Con el backend corriendo, en Postman: **Import** → `postman/Cafeteria-UTCh-Equipo4.postman_collection.json`.
2. Clic derecho en la colección → **Run collection** → **Run** (en orden).
3. Las carpetas son: `0. Autenticación`, `1. Flujo obligatorio (E2E)`, `2. Reglas de negocio`, `3. Seguridad por rol`, `4. Administrador`.
4. Tomar captura del resumen del Runner (todas las pruebas en verde) como evidencia.

Desde la terminal también se puede correr con Newman:

```bash
npx newman run postman/Cafeteria-UTCh-Equipo4.postman_collection.json
```

## 4. Git / GitHub: evolución por fases

Cada integrante debe subir sus propios commits. Reparto sugerido:

| Fase | Archivos que se agregan | Mensaje de commit |
|------|--------------------------|-------------------|
| 1 | `README.md`, `.gitignore`, `backend/pom.xml`, `CafeteriaApplication.java`, `application.properties` | `chore: estructura inicial` |
| 2 | `model/*`, `repository/*`, `config/DataInitializer.java` | `feat: entidades y repositorios` |
| 3 | `dto/ProductoDtos.java`, `service/ProductoService.java`, `controller/ProductoController.java`, `exception/*`, `dto/ErrorResponse.java` | `feat: CRUD de productos` |
| 4 | `dto/PedidoDtos.java`, `service/PedidoService.java`, `controller/PedidoController.java` | `feat: registro de pedidos` |
| 5 | `src/test/**/EstadoPedidoTest.java` (la regla RN4 ya vive en `EstadoPedido` y `Pedido.cambiarEstado`) | `feat: transicion de estados` |
| 6 | `security/*`, `dto/AuthDtos.java`, `service/AuthService.java`, `controller/AuthController.java`, `FlujoPedidoIntegrationTest.java` | `feat: seguridad por roles` |
| 7 | `postman/*` | `test: coleccion Postman` |
| 8 | `frontend/**` | `feat: frontend` |
| 9 | Documento de la evidencia y diagramas actualizados | `docs: matriz final` |

```bash
git init
git branch -M main
git remote add origin https://github.com/<usuario>/cafeteria-utch.git
git add README.md .gitignore backend/pom.xml backend/src/main/resources backend/src/main/java/mx/utch/cafeteria/CafeteriaApplication.java
git commit -m "chore: estructura inicial"
git push -u origin main
# ...siguientes fases con su propio git add / commit / push
```
