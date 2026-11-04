# Servigo — Fase 1 (Profile + ServiceCategory)

API RESTful en Spring Boot con **arquitectura hexagonal** (`domain` / `application` / `infrastructure`), **autenticación JWT** y **cache con Redis**, construida como primera fase del proyecto final de Programación V (Servigo: marketplace de servicios tipo Rappi).

Esta fase cubre las dos únicas entidades sin dependencias cruzadas necesarias para levantar la arquitectura completa:

- **Profile** — perfiles de usuario (`client`/`provider`), con registro y login JWT (RF01, RF02 del documento de requerimientos).
- **ServiceCategory** — catálogo de categorías de servicio, CRUD con cache en Redis (`@Cacheable`/`@CacheEvict`).

## Arquitectura

```
com.example.servigohex
├── domain
│   ├── models        # Profile, ServiceCategory, enums (sin anotaciones de Spring/JPA)
│   └── exceptions    # ResourceNotFoundException, DuplicateEmailException, ...
├── application
│   ├── ports
│   │   ├── in         # AuthUseCase, ServiceCategoryUseCase
│   │   └── out        # ProfileRepositoryPort, ServiceCategoryRepositoryPort,
│   │                   # PasswordEncoderPort, TokenProviderPort
│   └── services       # AuthService, ServiceCategoryService (implementan los casos de uso)
└── infrastructure
    ├── adapters
    │   ├── in/rest     # controllers, dto, mappers (MapStruct), security (filtro JWT), exceptions
    │   └── out         # persistence (entities, repositories JPA, mappers, *PersistenceAdapter)
    │                   # security (BCryptPasswordAdapter, JwtTokenAdapter)
    └── config          # SecurityConfig, RedisConfig, RedisCacheConfig, OpenApiConfig, main class
```

Regla: `domain` y `application` no dependen de Spring ni de JPA (salvo la anotación `@Service` en los servicios de aplicación, para inyección de dependencias); solo `infrastructure` conoce el framework.

## Requisitos

- Java 17
- Maven (o el wrapper `mvnw`)
- PostgreSQL corriendo localmente
- Redis corriendo localmente (por ejemplo, un contenedor Docker)

## Configuración

Variables de entorno (todas con valores por defecto para desarrollo local en `application.yaml`):

| Variable | Default | Descripción |
|---|---|---|
| `DB_USERNAME` | `postgres` | Usuario de PostgreSQL |
| `DB_PASSWORD` | `root` | Password de PostgreSQL |
| `REDIS_HOST` | `localhost` | Host de Redis |
| `REDIS_PORT` | `6379` | Puerto de Redis |
| `REDIS_PASSWORD` | `admin` | Password de Redis |
| `JWT_SECRET` | (clave de desarrollo) | Secreto para firmar los JWT — **cambiar en producción** |

La base de datos se llama `servigo` (créala antes de arrancar: `CREATE DATABASE servigo;`). Las tablas las crea Hibernate solas (`ddl-auto: update`).

## Cómo correrla

```bash
mvn spring-boot:run
```

La app queda en `http://localhost:8083`.

- Swagger UI: `http://localhost:8083/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8083/v3/api-docs`

## Endpoints

### Auth (`/api/v1/auth`)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/register` | Público | Registra un perfil (`CLIENT` o `PROVIDER`) |
| POST | `/login` | Público | Login, retorna un JWT |

### Categorías (`/api/v1/categorias`)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/` | Público | Lista categorías activas (cacheada en Redis, TTL 60 min) |
| GET | `/{id}` | Público | Busca una categoría por id |
| POST | `/` | Autenticado | Crea una categoría (invalida el cache) |
| PUT | `/{id}` | Autenticado | Actualiza una categoría (invalida el cache) |
| DELETE | `/{id}` | Autenticado | Desactiva una categoría — borrado lógico, nunca físico (invalida el cache) |

Códigos de respuesta: `200/201` éxito · `400` validación · `401` credenciales inválidas · `403` sin permiso/token · `404` no existe · `409` conflicto (correo o nombre duplicado).

## Probarla con Postman

En `postman/` hay una colección y un entorno listos:

1. Importa `postman/servigo.postman_collection.json` y `postman/servigo-local.postman_environment.json` en Postman.
2. Selecciona el entorno **Servigo - Local**.
3. Corre la carpeta **Auth** primero (genera el perfil y guarda el JWT en el entorno), y después **Categorias** (usa ese JWT para los endpoints protegidos).

Cada request trae su propio test de éxito o de error (409, 401, 403, 404, 400), y genera datos dummy únicos (email/nombre con timestamp) para poder correrse varias veces sin chocar con datos previos.

## Referencia

Estructura y patrones de JWT/cache basados en el repo de referencia del curso (`registro-academico`, rama `641B`), adaptados a los puertos (`PasswordEncoderPort`, `TokenProviderPort`) definidos en el diseño hexagonal de Servigo.
