# Shake Art Back

API backoffice du festival Shake Art.

## Objectif
Cette application Spring Boot expose les endpoints de gestion (artistes, equipe, programmation, reservations, etc.) avec:
- persistence MySQL (JPA)
- migrations Flyway
- authentification JWT
- validation des donnees entrantes

## Stack technique
- Java 21
- Spring Boot 3.5.x
- Spring Web, Spring Data JPA, Spring Security, Validation
- MySQL
- Flyway
- Gradle

## Prerequis
- JDK 21
- MySQL (port local configure: `3307`)
- Git

## Configuration
La configuration principale est dans `src/main/resources/application.yaml`.

### Variables d'environnement obligatoires
- `JWT_SECRET`: cle de signature JWT (obligatoire)

### Variables optionnelles
- `JWT_EXPIRATION_MS`: duree de vie du JWT en millisecondes (defaut: `3600000`, soit 1h)

### Variables utiles pour la base (si besoin d'override)
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Demarrage local
### PowerShell (Windows)
```powershell
$env:JWT_SECRET="votre-cle-longue-et-aleatoire"
./gradlew bootRun
```

Application par defaut sur `http://localhost:8080`.

## Base de donnees
- Flyway est active au demarrage.
- Les scripts SQL sont dans `src/main/resources/db/migration`.
- `spring.jpa.hibernate.ddl-auto=none`: le schema est pilote par Flyway.

## Documentation API
Si activee, documentation disponible sur:
- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

## Securite
- Authentification par JWT
- Endpoint login: `POST /auth/login`
- Les routes `/admin/**` necessitent un token valide
- Controle d'acces par role avec `@PreAuthorize`

## Validation des donnees
- Validation avec `@Valid` + annotations Jakarta Validation
- Requetes invalides: retour `400` avec JSON explicite
- Details de la strategie: `docs/validation-api.md`

## Tests
```bash
./gradlew test
```

## Notes de projet
- Le package Java utilise `com.shake_art.back` (underscore), car `com.shake-art.back` n'est pas valide.
