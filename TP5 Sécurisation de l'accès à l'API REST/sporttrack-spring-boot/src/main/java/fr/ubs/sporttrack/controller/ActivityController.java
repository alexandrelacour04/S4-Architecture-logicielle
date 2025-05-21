package fr.ubs.sporttrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import fr.ubs.sporttrack.model.Activity;
import fr.ubs.sporttrack.model.JSONFileReader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Contrôleur REST pour la gestion des activités sportives.
 * Fournit les endpoints pour créer, lire, rechercher et supprimer des activités.
 */
@RestController
@RequestMapping("/activities")
public class ActivityController {

    private static final String DATA_FILE = "data.json";
    private final Validator validator;

    /**
     * Constructeur par défaut.
     * Initialise le validateur pour la validation des activités.
     */
    public ActivityController() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * Retourne une liste des activités en récupérant le fichier JSON depuis le JAR du module `model`.
     * Tente d'abord de lire depuis un fichier temporaire, puis depuis le JAR si nécessaire.
     *
     * @return Liste des activités stockées
     * @throws RuntimeException Si une erreur survient lors de la lecture du fichier
     */
    @Operation(summary = "Récupère toutes les activités")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des activités retournée",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Activity.class)))
    })
    @GetMapping("/")
    public List<Activity> findAll() {
        try {
            String userHome = System.getProperty("user.home");
            String tempDataPath = userHome + "/.m2/repository/fr/ubs/sporttrack/model/1.0/temp-data.json";
            File tempDataFile = new File(tempDataPath);

            if (tempDataFile.exists()) {
                return new JSONFileReader(tempDataFile).getActivities();
            }

            String jarPath = userHome + "/.m2/repository/fr/ubs/sporttrack/model/1.0/model-1.0.jar";
            File jarFile = new File(jarPath);

            if (!jarFile.exists()) {
                throw new IllegalArgumentException("JAR du module `model` introuvable : " + jarPath);
            }

            try (JarFile jar = new JarFile(jarFile)) {
                ZipEntry entry = jar.getEntry(DATA_FILE);
                if (entry == null) {
                    throw new IllegalArgumentException("Fichier non trouvé dans le JAR : " + DATA_FILE);
                }

                File tempFile = File.createTempFile("data", ".json");
                tempFile.deleteOnExit();
                Files.copy(jar.getInputStream(entry), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                return new JSONFileReader(tempFile).getActivities();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier JSON", e);
        }
    }

    /**
     * Recherche des activités correspondant à un mot-clé dans leur description.
     * La recherche est insensible à la casse.
     *
     * @param keyword Mot-clé pour filtrer les activités
     * @return Liste des activités dont la description contient le mot-clé
     */
    @Operation(summary = "Rechercher des activités par mot-clé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des activités retournée",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Activity.class)))
    })
    @GetMapping("/{keyword}")
    public List<Activity> findByKeyword(@PathVariable("keyword") String keyword) {
        List<Activity> activities = findAll();
        List<Activity> searchList = new ArrayList<>();
        for (Activity activity : activities) {
            if (activity.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                searchList.add(activity);
            }
        }
        return searchList;
    }

    /**
     * Ajoute une nouvelle activité au fichier JSON.
     * Vérifie la validité des données et l'unicité de la date.
     *
     * @param activity Nouvelle activité à ajouter
     * @return ResponseEntity avec le statut approprié et un message
     * @throws RuntimeException Si une erreur survient lors de l'écriture du fichier
     */
    @Operation(summary = "Ajoute une nouvelle activité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Activité créée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Activity.class))),
            @ApiResponse(responseCode = "400", description = "Données non valides",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Doublon détecté, activité déjà existante",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> addActivity(@RequestBody Activity activity) {
        Set<ConstraintViolation<Activity>> violations = validator.validate(activity);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Données non valides : ");
            for (ConstraintViolation<Activity> violation : violations) {
                errorMessage.append(violation.getMessage()).append("; ");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        try {
            List<Activity> activities;
            String userHome = System.getProperty("user.home");
            String jsonFilePath = userHome + "/.m2/repository/fr/ubs/sporttrack/model/1.0/temp-data.json";
            File jsonFile = new File(jsonFilePath);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

            if (jsonFile.exists()) {
                activities = objectMapper.readValue(
                        jsonFile,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Activity.class)
                );
            } else {
                activities = findAll();
            }

            for (Activity existingActivity : activities) {
                if (existingActivity.getDate().equals(activity.getDate())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Une activité avec la date '" + activity.getDate() + "' existe déjà.");
                }
            }

            activities.add(activity);
            objectMapper.writeValue(jsonFile, activities);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("L'activité a été créée avec succès");
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'activité", e);
        }
    }

    /**
     * Réinitialise la base de données en créant un nouveau fichier JSON vide.
     *
     * @return ResponseEntity avec le statut de l'opération et un message
     */
    @Operation(summary = "Réinitialise la base de données")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Base de données réinitialisée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la réinitialisation de la base de données")
    })
    @DeleteMapping("/clearDB")
    public ResponseEntity<String> clearDatabase() {
        try {
            String userHome = System.getProperty("user.home");
            String jsonFilePath = userHome + "/.m2/repository/fr/ubs/sporttrack/model/1.0/temp-data.json";
            File jsonFile = new File(jsonFilePath);

            if (jsonFile.exists()) {
                Files.write(jsonFile.toPath(), "[]".getBytes());
            } else {
                jsonFile.getParentFile().mkdirs();
                Files.write(jsonFile.toPath(), "[]".getBytes());
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Base de données réinitialisée avec succès");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la réinitialisation de la base de données : " + e.getMessage());
        }
    }

    /**
     * Supprime une activité existante basée sur sa description.
     *
     * @param description Description de l'activité à supprimer
     * @return ResponseEntity avec le statut de l'opération et un message si nécessaire
     */
    @Operation(summary = "Supprime une activité existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activité supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Activité non trouvée")
    })
    @DeleteMapping("/{description}")
    public ResponseEntity<String> deleteActivity(@PathVariable String description) {
        try {
            String userHome = System.getProperty("user.home");
            String jsonFilePath = userHome + "/.m2/repository/fr/ubs/sporttrack/model/1.0/temp-data.json";
            File jsonFile = new File(jsonFilePath);

            if (!jsonFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Le fichier de données n'existe pas.");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            List<Activity> activities = objectMapper.readValue(
                    jsonFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Activity.class)
            );

            boolean removed = activities.removeIf(activity -> activity.getDescription().equalsIgnoreCase(description));
            if (!removed) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Activité avec la description '" + description + "' non trouvée.");
            }

            objectMapper.writeValue(jsonFile, activities);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression de l'activité : " + e.getMessage());
        }
    }
}