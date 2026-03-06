# Exercice 15 — Authentification JWT pour un service de gestion de projets

## Description

Ce projet implémente une API REST développée avec **Spring WebFlux**, **Spring Security**, **JWT** et **PostgreSQL**.

L’API permet :

- aux utilisateurs de se connecter avec un **username** et un **password**
- d’obtenir un **token JWT**
- d’accéder à leurs **projets personnels** avec ce token

L’authentification est **stateless** et se fait via un **JWT envoyé dans le header Authorization**.

# Authentification

L’API utilise une authentification **JWT**.

Pour accéder aux endpoints protégés, il faut envoyer :

# Comptes de test

Les utilisateurs sont définis **en dur dans l’application**.

| Username | Password   |
| -------- | ---------- |
| Lucas    | Lucas123   |
| Nicolas  | Nicolas123 |
