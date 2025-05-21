# SportTrack - Projet Spring Boot

Ce projet est une application Spring Boot qui expose des APIs REST pour la gestion d'activités sportives. Elle inclut
une sécurisation via HTTP Basic pour l'accès aux endpoints API. Swagger UI est intégré pour consulter la documentation
et effectuer des tests.

## Fonctionnalités Développées

### Endpoints :

1. **Récupérer toutes les activités :**
   ```
   GET /activities/
   ```
    - Retourne une liste de toutes les activités contenues dans le fichier JSON (`data.json`).
    - **Authentification requise :** Nécessite un identifiant/mot de passe via HTTP Basic.

2. **Rechercher des activités par mot-clé :**
   ```
   GET /activities/{keyword}
   ```
    - Filtre les activités contenant le mot-clé passé en paramètre dans leur description.
    - **Authentification requise :** Nécessite un identifiant/mot de passe via HTTP Basic.

3. **Ajouter une activité :**
   ```
   POST /activities/
   ```
    - Ajoute une nouvelle activité au fichier JSON. L'activité doit être envoyée au format JSON.
    - **Validation des données :** Avant l'ajout dans le fichier, les données de l'activité sont validées. En cas de
      violation des contraintes, des messages d'erreur détaillés sont retournés.
    - **Authentification requise :** Nécessite un identifiant/mot de passe via HTTP Basic.

---

## Validation des Données

L'ajout d'une activité utilise **Hibernate Validator** pour garantir l'intégrité des données envoyées. Les contraintes
suivantes s'appliquent aux champs dans la classe `Activity` :

- **`name` (nom de l'activité)** :
    - **Obligatoire** : Le nom ne peut pas être nul.
    - **Longueur** : Doit contenir entre 3 et 50 caractères.
- **`description` (description de l'activité)** :
    - **Obligatoire** : La description ne peut pas être nulle.
    - **Longueur** : Doit contenir entre 10 et 200 caractères.
- **`date` (date de l'activité)** :
    - **Obligatoire** : Doit être renseignée dans un format ISO-8601 (`yyyy-MM-dd`).
- **`latitude` et `longitude`** :
    - La latitude doit être comprise entre **-90** et **90**.
    - La longitude doit être comprise entre **-180** et **180**.

**Exemple de validation incorrecte :**
Si les données ne respectent pas ces contraintes, l'API retourne une réponse avec le statut `400 Bad Request` et la
liste des erreurs.

---

## Authentification HTTP Basic

### Configuration des identifiants :

Les identifiants pour accéder aux endpoints sont configurés dans `application.properties` :

---

Le projet suit une architecture classique Spring Boot :
- **Controller :** `ActivityController.java` contient les points d'entrée REST.
- **Model :** `Activity.java` et `JSONFileReader.java` gèrent les données et leur extraction depuis le fichier JSON.

---

*Projet développé à titre éducatif dans le cadre d'un module de développement Back-End avec Spring Boot.*
```