package fr.ubs.sporttrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ubs.sporttrack.model.Activity;
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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private static final String DATA_FILE = "data.json"; // Fichier JSON contenant les activités
    private final Validator validator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ActivityController() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * Récupérer toutes les activités.
     */
    @GetMapping("/")
    public ResponseEntity<List<Activity>> findAll() {
        try {
            List<Activity> activities = readActivitiesFromFile();
            activities.sort(Comparator.comparing(a -> a.getDescription().toLowerCase()));
            return ResponseEntity.ok(activities);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    /**
     * Ajouter une activité.
     */
    @PostMapping("/")
    public ResponseEntity<String> addActivity(@RequestBody Activity newActivity) {
        // Validation des données avec Hibernate Validator
        Set<ConstraintViolation<Activity>> violations = validator.validate(newActivity);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("\"" + errorMessage + "\"");
        }

        if (newActivity == null || newActivity.getDescription() == null || newActivity.getDescription().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("\"unvalid data\"");
        }

        // Validation des données
        if (newActivity.getFreqMin() < 0 || newActivity.getFreqMax() > 250 ||
                newActivity.getData().stream().anyMatch(d -> d.getCardioFrequency() < 0 || d.getCardioFrequency() > 250) ||
                newActivity.getData().stream().anyMatch(d -> Math.abs(d.getLatitude()) > 90 || Math.abs(d.getLongitude()) > 180)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("\"unvalid data\"");
        }

        try {
            List<Activity> activities = readActivitiesFromFile();

            // Vérification des doublons
            boolean exists = activities.stream()
                    .anyMatch(activity -> activity.getDescription().trim()
                            .equalsIgnoreCase(newActivity.getDescription().trim()));

            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("\"failure\"");
            }

            // Ajout de l'activité
            activities.add(newActivity);
            writeActivitiesToFile(activities);

            return ResponseEntity.ok("\"success\"");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("\"failure\"");
        }
    }

    /**
     * Supprimer toutes les activités.
     */
    @DeleteMapping("/")
    public ResponseEntity<String> deleteAllActivities() {
        try {
            Files.writeString(new File(DATA_FILE).toPath(), "[]");
            return ResponseEntity.ok("\"success\"");
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("\"failure\"");
        }
    }

    /**
     * Supprimer une activité spécifique par description.
     */
    @DeleteMapping("/{desc}")
    public ResponseEntity<String> deleteActivity(@PathVariable String desc) {
        try {
            List<Activity> activities = readActivitiesFromFile();

            boolean removed = activities.removeIf(activity -> activity.getDescription().equalsIgnoreCase(desc));

            if (removed) {
                writeActivitiesToFile(activities);
                return ResponseEntity.ok("\"success\"");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("\"data does not exist\"");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression de l'activité: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("\"failure\"");
        }
    }

    /**
     * Gérer les appels POST non autorisés sur /activities/{description}.
     */
    @PostMapping("/{description}")
    public ResponseEntity<String> unauthorizedOperation(@PathVariable String description) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("\"unauthorized operation\"");
    }

    /**
     * Lire les activités depuis un fichier JSON.
     */
    private List<Activity> readActivitiesFromFile() throws IOException {
        File file = new File(DATA_FILE);

        if (!file.exists() || file.length() == 0) {
            Files.writeString(file.toPath(), "[]");
            return new ArrayList<>();
        }

        return objectMapper.readValue(
                file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Activity.class)
        );
    }

    /**
     * Écrire les activités dans un fichier JSON.
     */
    private void writeActivitiesToFile(List<Activity> activities) throws IOException {
        objectMapper.writeValue(new File(DATA_FILE), activities);
    }
}