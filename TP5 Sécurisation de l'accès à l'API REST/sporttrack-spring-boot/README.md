# SportTrack - Projet Spring Boot

SportTrack est une application développée avec Spring Boot, dédiée à la gestion des activités sportives via des APIs REST sécurisées. Ce projet met l'accent sur la validation des données, la sécurité, et une documentation claire grâce à Swagger. Les fonctionnalités incluent la création, la lecture, et la suppression d'activités sportives.

---

## Fonctionnalités du Projet

### **Endpoints REST Implémentés**
1. **Récupérer toutes les activités :**
   ```
   GET /activities/
   ```
    - Fournit la liste de toutes les activités sportives stockées.
    - **Authentification requise :** via HTTP Basic.

2. **Ajouter une nouvelle activité :**
   ```
   POST /activities/
   ```
    - Permet d'ajouter une nouvelle activité (au format JSON).
    - Inclus une validation stricte des données reçues.
    - **Authentification requise :** via HTTP Basic.

3. **Supprimer une activité spécifique via la description :**
   ```
   DELETE /activities/{desc}
   ```
    - Supprime une activité identifiée par sa description.
    - Retourne `404 Not Found` si l'activité cible n'existe pas.
    - **Authentification requise :** via HTTP Basic.

4. **Supprimer toutes les activités :**
   ```
   DELETE /activities/
   ```
    - Efface toutes les activités stockées.
    - **Authentification requise :** via HTTP Basic.

---

## Validation des Données

Le projet utilise **Hibernate Validator** pour garantir l'intégrité des données en appliquant des contraintes sur les champs d'activités. Les contraintes incluent :
- **`description` :**
    - Champ obligatoire.
    - Longueur comprise entre 10 et 200 caractères.
- **`freqMin` et `freqMax` :**
    - Fréquences cardiaques devant rester entre 15 et 220.
- **`latitude` et `longitude` :**
    - Latitude entre **-90** et **90**.
    - Longitude entre **-180** et **180**.
- **`data` :**
    - Ensemble structuré contenant les caractéristiques détaillées d'une activité (comme le cardio, les coordonnées et l'altitude).

En cas de validation échouée, l'API retourne :
- Le statut **400 Bad Request**.
- Un message d'erreur explicatif détaillant la source du problème.

---

## Sécurité

L'accès à l'API est sécurisé grâce à Spring Security et HTTP Basic Authentication.
- Les identifiants sont configurés dans le fichier `application.properties`.
- Swagger est partiellement sécurisé pour limiter les accès non autorisés aux documents API.

Exemple d'authentification configurée :
```properties
# Identifiants HTTP Basic
security.user.username=r401
security.user.password=$2b$10$password_encoded_with_bcrypt
```

Configuration d'accès :
- Les requêtes `GET`, `POST`, et `DELETE` nécessitent une authentification.
- Désactivation du CSRF simplifiant les tests côté client.

---

## Documentation et Tests

1. **Swagger UI :**
    - Accessible via `/swagger-ui.html`.
    - Permet de tester les endpoints directement dans un environnement visuel interactif.

2. **Tests d'intégration :**
    - Couverture complète des fonctionnalités avec la classe `FullCoverageActivityControllerTest`.
    - Vérifie les comportements attendus sous différents scénarios :
        - Ajout d'activités invalides ou déjà existantes.
        - Suppression réussie ou échouée.
        - Retour des erreurs HTTP correctes (404, 400, 409).

---

## Démonstration : Étapes Clés

1. **Accéder à Swagger UI** :
    - Connectez-vous à `http://[host]:8081/swagger-ui.html` avec vos identifiants administratifs.

2. **Créer une activité (exemple `POST`)** :
    - Fournir un JSON valide pour tester la validation des données.

   Exemple :
   ```json
   {
       "description": "Running in the park",
       "freqMin": 60,
       "freqMax": 180,
       "data": [
           {
               "time": "12:00:00",
               "altitude": 45,
               "latitude": 47.6448,
               "longitude": -2.7766,
               "cardioFrequency": 100
           }
       ]
   }
   ```

3. **Consulter la liste des activités** :
    - Testez le `GET /activities/` pour voir les détails des activités.

---

## Structure Maven et Dépendances Clés

Le fichier `pom.xml` définit une architecture modulaire et des dépendances essentielles, telles que :
- **Spring Boot Web Starter** : Construction des endpoints REST.
- **Hibernate Validator** : Validation des données d'entrée.
- **Spring Security** : Authentification et autorisation HTTP Basic.
- **Springdoc OpenAPI** : Génération de la documentation Swagger.
- **JUnit 5 et MockMvc** : Test unitaire et intégration.

---

## Objectifs et Perspectives

- **Points Forts Réalisés :**
    - Interface utilisateur Swagger pour simplifier les tests API.
    - Validation des données robuste avec Hibernate Validator.
    - Sécurité intégrée via Spring Security.
    - Tests exhaustifs garantissant le bon fonctionnement des endpoints implémentés.

- **Perspectives Futures :**
    - Ajouter des rôles utilisateurs et permissions avancées.
    - Étendre le projet avec une interface graphique utilisateur complète.
    - Intégrer un système de base de données relationnelle pour gérer les données au lieu d'un fichier JSON.

---

*Développé dans le cadre d'un projet éducatif sur Spring Boot - Université de Bretagne Sud.* 😊