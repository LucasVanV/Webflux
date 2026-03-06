# Exercice 14 — Sécurisation d'une API de réservation de salles

## Description

Ce projet est une API développée avec **Spring WebFlux**, **Spring Security** et **PostgreSQL**.

Le serveur permet de gérer des salles de réunion avec une sécurité basée sur les rôles :

- **USER** : peut consulter les salles disponibles
- **ADMIN** : peut consulter, ajouter et supprimer des salles

L’API utilise une authentification **HTTP Basic Auth**.

---

## Fonctionnalités

Le serveur expose les endpoints suivants :

### `GET /api/rooms`

Retourne la liste de toutes les salles disponibles.

**Accès :**

- utilisateur authentifié `USER`
- utilisateur authentifié `ADMIN`

---

### `POST /api/rooms`

Ajoute une nouvelle salle.

**Accès :**

- uniquement `ADMIN`

**Body JSON attendu :**

```json
{
  "name": "Salle Innovation"
}
```

## Authentification

Pour accéder à l’API, il faut utiliser une authentification **Basic Auth**.

### Utilisateur simple (USER)

login : `user`  
mot de passe : `user123`

Peut :

- consulter les salles

---

### Administrateur (ADMIN)

login : `admin`  
mot de passe : `admin123`

Peut :

- consulter les salles
- ajouter une salle
- supprimer une salle
