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
.\gradlew.bat bootRun
```

Si MySQL n'est pas disponible localement, vous pouvez lancer l'application avec le profil test (H2 en memoire):

```powershell
.\gradlew.bat bootRun --args="--spring.profiles.active=test"
```

Application accessible sur `http://localhost:8080`.

## Base de donnees

- Flyway est active au demarrage et applique automatiquement les migrations.
- Les scripts SQL sont dans `src/main/resources/db/migration`.
- `spring.jpa.hibernate.ddl-auto=none` : le schema est entierement pilote par Flyway.

## Documentation API (Swagger)

### 🔗 Accès à la documentation interactive

Après avoir démarré l'application (`./gradlew bootRun`), ouvrez votre navigateur à:

**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Cette page expose:

- ✅ Tous les endpoints disponibles (GET, POST, PUT, DELETE)
- ✅ Les codes HTTP retournés (201 pour création, 204 pour suppression, etc.)
- ✅ Les rôles/permissions requis (ROLE_ADMIN, ROLE_USER)
- ✅ Les DTOs avec leurs champs et validations
- ✅ La possibilité de tester les endpoints directement

### Endpoints d'accès Swagger

| URL                                     | Description                               |
| --------------------------------------- | ----------------------------------------- |
| `http://localhost:8080/swagger-ui.html` | **Interface Swagger UI** (interactive)    |
| `http://localhost:8080/v3/api-docs`     | Spec OpenAPI JSON (pour clients externes) |

### 🔐 Authentification dans Swagger

1. **Ouvrir** `http://localhost:8080/swagger-ui.html`
2. **Appeler** `POST /auth/login` pour obtenir un token:
   ```json
   {
     "email": "admin@exemple.com",
     "password": "votre-mot-de-passe"
   }
   ```
3. **Copier** la valeur du champ `token` depuis la réponse
4. **Cliquer** sur le bouton **Authorize** (icône 🔒 en haut à droite)
5. **Coller** le token dans le champ `bearerAuth` (sans préfixe "Bearer" - la librairie l'ajoute automatiquement)
6. **Fermer** la modal - tous les endpoints protégés sont maintenant accessibles

### ℹ️ Informations documentées dans Swagger

- **Annotations @Operation** : Résumé + description détaillée de chaque endpoint
- **Codes HTTP explicites** :
  - `201 Created` pour les POST (création)
  - `204 No Content` pour les DELETE (suppression)
  - `200 OK` pour les GET, PUT
  - `400 Bad Request` pour les erreurs de validation
  - `401 Unauthorized` pour authentification manquante
  - `403 Forbidden` pour accès refusé
- **Rôles requis** : Chaque endpoint indique les rôles nécessaires
- **DTOs avec @Schema** : Champs documentés avec exemples et validations

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

Sous PowerShell (Windows):

```powershell
.\gradlew.bat test
```

Le test d'integration de la documentation API se trouve dans `src/test/java/com/shake_art/back/documentation/OpenApiDocumentationIntegrationTest.java`.

Le test d'integration securite se trouve dans `src/test/java/com/shake_art/back/security/SecurityErrorResponseIntegrationTest.java`.

Il valide explicitement les 3 cas suivants sur une route admin (`/admin/reservation-email`):

- sans authentification -> `401 Unauthorized`
- avec `ROLE_USER` -> `403 Forbidden`
- avec `ROLE_ADMIN` -> `200 OK`

## Couverture de code (JaCoCo)

Generation du rapport:

```powershell
.\gradlew.bat clean check
```

Rapport HTML genere dans:

- `build/reports/jacoco/test/html/index.html`

Seuils verifies automatiquement:

- Couverture globale >= `80%`
- Couche service >= `80%`

Note sur la colonne `Missed Branches`:

- `n/a` signifie qu'il n'y a pas de branche mesurable dans la classe/methode (pas de `if/else`, `switch`, ternaire, etc.).
- Ce n'est pas une erreur de couverture.

## Scan des vulnérabilités (OWASP Dependency-Check)

Detection automatique des vulnerabilites dans les dependances Maven.

Generation du rapport en local:

```powershell
.\gradlew.bat dependencyCheckAnalyze
```

Rapports generes dans:

- `build/reports/dependency-check-report.html` (rapport interactif)
- `build/reports/dependency-check-report.json` (donnees brutes)

Seuil critique configure:

- Le build echoue si une dependance presente un CVE avec un score CVSS >= `9.0`
- Les CVE de score inferieur sont reportees dans le rapport sans bloquer le build

Derniers resultats (20/03/2026):

- 8 vulnerabilites detectees, score CVSS maximum : **7.5**
- Aucune vulnerabilite critique (CVSS >= 9.0) — build non bloque

Dependances concernees:

- `angus-activation 2.0.3` — CVE-2025-7962
- `commons-lang3 3.17.0` — CVE-2025-48924
- `hibernate-validator 8.0.3` — CVE-2025-15104
- `log4j-api 2.24.3` — CVE-2025-68161
- `swagger-ui 5.31.0` (DOMPurify 3.2.6) — CVE-2025-15599, CVE-2026-0540

Intégration CI (GitHub Actions):

- Pipeline CI push / pull request: `.github/workflows/ci.yml`
- Job `tests`: build, tests, rapport JaCoCo, verification du seuil de couverture 80 %, analyse SonarCloud si configuree
- Job `security-scan`: scan OWASP separe, execute meme si le job `tests` echoue
- Workflow hebdomadaire de securite: `.github/workflows/security-dependency-scan.yml`
- Rapport OWASP HTML / JSON uploade comme artefact de build (meme en cas d'echec)

Configuration memoire:

- Taches standard (compilation, tests, JaCoCo) : `Xmx1024m` (defini dans `gradle.properties`)
- Scan OWASP uniquement : `Xmx2048m` (surcharge via `GRADLE_OPTS` dans le step CI dedie)

Prerequis SonarCloud pour la CI:

- Secret GitHub Actions: `SONAR_TOKEN`
- Variables GitHub Actions: `SONAR_ORGANIZATION` et `SONAR_PROJECT_KEY`
- Tant que ces valeurs ne sont pas configurees, le job `tests` reste vert mais l'analyse SonarCloud est ignoree avec un message explicite dans les logs

## Notes de projet

- Le package Java utilise `com.shake_art.back` (underscore), car `com.shake-art.back` n'est pas valide.

## Architecture API (Couche DTO)

Depuis Mars 2026, l'API expose des **DTOs structurés** plutôt que des entités JPA directes.
Cela améliore:

1. **Visibilité OpenAPI** : Les DTOs avec `@Schema` sont mieux décrits dans Swagger
2. **Contrat API stable** : Les changements internes d'entités n'affectent pas le client
3. **Séparation des responsabilités** : Couche API séparée de la couche persistence

### DTOs principaux disponibles

- `ReservationDto` - Éntrée pour les nouvelles réservations
- `ProgrammationDto` - Programmation festival avec activités
- `MurPeintDto` - Mur peint avec localisation GPS
- `PartenaireDto` - Partenaires avec logo
- `EquipeContentDto` - Contenu page équipe
- `ActiviteDto` - Activités de festival
- `EquipeDto` - Membres d'équipe
- `ArtisteDto` - Informations artistes
- `IntervenantDto` - Intervenants unifiés

### Mappers

Les conversions entre Entity et DTO sont déléguées à des mappers:

- `EquipeMapper` - Entity ↔ DTO pour Équipe
- `PartenaireMapper` - Partenaire ↔ DTO
- `EquipeContentMapper` - Contenu équipe ↔ DTO
- Et autres mappers pour les conversions nécessaires

### Remarques de configuration OpenAPI

L'injection automatique du code `200` dans `OpenApiConfig.java` a été rendue **intelligente**:

- Si un endpoint documente explicitement `201` ou `204`, le `200` générique n'est pas injecté
- Cela garantit une documentation précise des codes HTTP réels retournés
- GETs, PUTs et PATCHes retournent `200` hors cas speciaux
