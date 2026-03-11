# Shake Art Back

API backoffice du festival Shake Art.

## Objectif

Cette application Spring Boot expose les endpoints de gestion (artistes, equipe, programmation, reservations, etc.) avec:

- persistence MySQL (JPA) et migrations Flyway
- authentification JWT avec controle d'acces par role (ROLE_ADMIN, ROLE_USER)
- validation des donnees entrantes (Jakarta Validation)
- documentation interactive OpenAPI / Swagger

## Stack technique

- Java 21
- Spring Boot 3.5.x
- Spring Web, Spring Data JPA, Spring Security, Validation
- MySQL · Flyway · Gradle
- springdoc-openapi (Swagger UI)

## Prerequis

- JDK 21
- MySQL (port local configure: `3307`)
- Git

## Configuration

La configuration principale est dans `src/main/resources/application.yaml`.

### Variables d'environnement obligatoires

- `JWT_SECRET`: cle de signature JWT (minimum 32 caracteres)

### Variables optionnelles

- `JWT_EXPIRATION_MS`: duree de vie du JWT en millisecondes (defaut: `3600000` — 1h)
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Demarrage local

```powershell
$env:JWT_SECRET="votre-cle-longue-et-aleatoire"
./gradlew bootRun
```

Application accessible sur `http://localhost:8080`.

## Base de donnees

- Flyway est active au demarrage et applique automatiquement les migrations.
- Les scripts SQL sont dans `src/main/resources/db/migration`.
- `spring.jpa.hibernate.ddl-auto=none` : le schema est entierement pilote par Flyway.

## Documentation API (Swagger)

La documentation interactive est disponible sur :

- **Swagger UI** : `http://localhost:8080/swagger-ui.html`
- **JSON OpenAPI** : `http://localhost:8080/v3/api-docs`

### Tester les endpoints proteges dans Swagger

1. Ouvrir `http://localhost:8080/swagger-ui.html`
2. Appeler `POST /auth/login` avec vos identifiants :
   ```json
   { "email": "admin@exemple.com", "password": "votre-mot-de-passe" }
   ```
3. Copier la valeur du champ `token` dans la reponse.
4. Cliquer sur le bouton **Authorize** (icone cadenas) en haut de la page.
5. Coller le token dans le champ `bearerAuth` (sans prefixe "Bearer").
6. Tous les endpoints proteges sont maintenant accessibles.

Note : les endpoints d'ecriture affichent explicitement dans Swagger la mention
"Necessite ROLE_ADMIN" dans leur description metier, pour faciliter la lecture
fonctionnelle et securitaire.

### Roles et acces

| Role         | Acces                                                                             |
| ------------ | --------------------------------------------------------------------------------- |
| _(public)_   | `GET /programmation`, `GET /murs`, `POST /reservation`, intervenants              |
| `ROLE_USER`  | `GET /programmation/{id}`, `GET /programmation/annee/{annee}`, `GET /reservation` |
| `ROLE_ADMIN` | Tous les endpoints d'ecriture + `/admin/**`                                       |

### Endpoints documentes

| Tag            | Description                                           |
| -------------- | ----------------------------------------------------- |
| Authentication | Login et obtention du token JWT                       |
| Admin          | Gestion de l'email de reservation (ROLE_ADMIN)        |
| Programmation  | Jours de festival, activites, intervenants            |
| Reservations   | Gestion des inscriptions visiteurs                    |
| Murs Peints    | Carte geoloc des fresques avec upload photo           |
| Partenaires    | CRUD partenaires + upload logo                        |
| Accueil        | Contenu editable page d'accueil + upload video/images |
| Intervenants   | Recherche artistes/equipes pour autocompletion        |

## Securite

- Authentification stateless par token JWT (Bearer)
- Endpoint public : `POST /auth/login`
- Endpoints proteges `ROLE_ADMIN` : `/admin/**` et toutes les mutations (POST/PUT/DELETE) metier
- Endpoints proteges `ROLE_USER` : lectures necessitant une authentification
- Controle d'acces par `@PreAuthorize` au niveau methode
- Reponses d'erreur uniformes (401/403) au format JSON

## Validation des donnees

- Validation avec `@Valid` + annotations Jakarta Validation (`@NotBlank`, `@Email`, etc.)
- Requetes invalides : retour `400` avec JSON explicite (champ `details` par champ)
- Details de la strategie : `docs/validation-api.md`

## Tests

```bash
./gradlew test
```

## Notes de projet

- Le package Java utilise `com.shake_art.back` (underscore), car `com.shake-art.back` n'est pas valide.
