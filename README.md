# AnimeAPI

API REST en **Spring Boot** para un catálogo de anime: series, personajes, géneros, estudios y
actores de voz, con autenticación de usuarios, reseñas, favoritos y listas de seguimiento.

## Stack

- Java · Spring Boot (Web, Security, Data JPA, Validation)
- PostgreSQL
- Autenticación JWT (`jjwt`)
- Documentación de la API con springdoc-openapi (Swagger UI)
- Despliegue en contenedor (`Dockerfile` incluido)

## Qué hace

- **Catálogo**: anime, personajes, géneros, estudios, actores de voz, con sus relaciones.
- **Usuarios**: registro/login con JWT, roles.
- **Interacción del usuario**: reseñas por anime, favoritos y listas de seguimiento personales.

## Estructura

```
src/main/java/com/animeapi/
├── controller/     endpoints REST (Anime, Auth, Favorite, Review, User, Watchlist)
├── dto/            request/response, separados del modelo de persistencia
├── repository/     Spring Data JPA por entidad
└── security/       autenticación y detalles de usuario para Spring Security
```

## Correr en local

```bash
./mvnw spring-boot:run
```

Requiere una base PostgreSQL alcanzable con la configuración de `src/main/resources/application.properties`.
La documentación interactiva de la API queda disponible en `/swagger-ui.html` una vez levantado.
