# SportTrack - Projet Spring Boot

Ce projet est une application Spring Boot qui expose des APIs REST pour la gestion d'activités sportives. Il repose sur trois principales fonctionnalités : la récupération de la liste des activités, la recherche par mot-clé, et l'ajout de nouvelles activités via une requête POST.

## Fonctionnalités Développées

### Endpoints :

1. **Récupérer toutes les activités :**
   ```
   GET /activities/
   ```
    - Retourne une liste de toutes les activités contenues dans le fichier JSON (`data.json`).

2. **Rechercher des activités par mot-clé :**
   ```
   GET /activities/{keyword}
   ```
    - Filtre les activités contenant le mot-clé passé en paramètre dans leur description.

3. **Ajouter une activité :**
   ```
   POST /activities/
   ```
    - Ajoute une nouvelle activité au fichier JSON. L'activité doit être envoyée au format JSON.

---

## Tests effectués

### Commandes cURL pour tester les APIs

#### 1. Récupérer toutes les activités

 bash curl -X GET [http://localhost:8080/activities/](http://localhost:8080/activities/)


#### 2. Rechercher par mot-clé

 bash curl -X GET [http://localhost:8080/activities/{keyword}](http://localhost:8080/activities/%7Bkeyword%7D)
 - Remplacez `{keyword}` par le mot-clé que vous souhaitez rechercher.

#### 3. Ajouter une activité

bash curl -X POST [http://localhost:8080/activities/](http://localhost:8080/activities/)
-H "Content-Type: application/json"
-d '{ "name": "Nouvelle activité", "description": "Description de l'activité", "date": "2023-10-20" }'


---

## Instructions pour exécuter le projet

1. **Nettoyer le projet :**
   Exécutez la commande suivante pour nettoyer les fichiers temporaires et la compilation :
   ```bash
   mvn clean
   ```

2. **Construire l'archive :**
   La construction de l'archive de déploiement peut se faire avec Maven :
   ```bash
   mvn package
   ```

3. **Exécuter l'application :**
   Lancez le projet Spring Boot avec la commande :
   ```bash
   java -jar target/sporttrack-1.0.jar
   ```

---

## Notes

- Les données principales sont stockées dans un fichier JSON (`data.json`) qui est soit extrait dynamiquement depuis le JAR, soit lu depuis un fichier temporaire nommé `temp-data.json`.
- Lors de l'ajout d'activités, le fichier temporaire est automatiquement créé / mis à jour dans le répertoire `~/.m2/repository/fr/ubs/sporttrack/model/1.0/`.
- Veuillez vérifier que le fichier `model-1.0.jar` est disponible dans votre dépôt Maven local avant de lancer le projet.

---

## Structure Projet

Le projet suit une architecture classique Spring Boot :
- **Controller :** `ActivityController.java` contient les points d'entrée REST.
- **Model :** `Activity.java` et `JSONFileReader.java` gèrent les données et leur extraction depuis le fichier JSON.

---

*Projet développé à titre éducatif dans le cadre d'un module de développement Back-End avec Spring Boot.*