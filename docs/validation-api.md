# Validation des entrees API

Ce document decrit la bonne pratique appliquee sur l'API pour valider les donnees entrantes.

## Regles appliquees

- Ajouter `@Valid` sur les corps de requete (`@RequestBody`) dans les controllers.
- Utiliser des annotations Jakarta Validation sur les DTO/modeles d'entree:
  - `@NotBlank` pour les chaines obligatoires.
  - `@Email` pour les emails.
  - `@NotNull` pour les booleens/objets obligatoires.
  - `@Min` pour les bornes numeriques.
  - `@NotEmpty` pour les listes qui ne doivent pas etre vides.
- Centraliser la reponse d'erreur avec `@RestControllerAdvice` pour obtenir un JSON stable.

## Format de reponse en cas de validation invalide

Code HTTP: `400 Bad Request`

Exemple:

```json
{
  "timestamp": "2026-03-10T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Requete invalide",
  "path": "/auth/login",
  "details": {
    "email": "must be a well-formed email address",
    "password": "must not be blank"
  }
}
```

## Bonnes pratiques de maintenance

- Ne pas dupliquer les validations manuelles dans les controllers quand `@Valid` couvre le besoin.
- Conserver les messages de validation orientes utilisateur.
- Ajouter un test MockMvc pour chaque nouveau endpoint d'ecriture (POST/PUT/PATCH) avec au moins un cas invalide.
