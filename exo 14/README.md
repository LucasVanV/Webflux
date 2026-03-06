# Exercice 13 - API de gestion des produits avec stock

## Description

Cette API réactive permet de gérer un catalogue de produits avec Spring WebFlux et R2DBC.

## Fonctionnalités

- Récupérer tous les produits
- Récupérer un produit par ID
- Créer un produit
- Mettre à jour un produit
- Supprimer un produit
- Rechercher des produits par nom
- Réduire le stock après un achat
- Retourner une erreur si le stock est insuffisant

## Endpoints

### GET /api/products

Retourne tous les produits

### GET /api/products/{id}

Retourne un produit par ID

### POST /api/products

Crée un produit

Exemple JSON :

```json
{
  "name": "iPhone 25",
  "price": 9999.99,
  "stock": 2
}
```
