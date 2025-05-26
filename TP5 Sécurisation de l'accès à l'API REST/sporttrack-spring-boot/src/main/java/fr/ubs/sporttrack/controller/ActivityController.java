package fr.ubs.sporttrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ubs.sporttrack.model.Activity;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private static final String DATA_FILE = "data.json";
    private final Validator validator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ActivityController() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @GetMapping("/")
    public ResponseEntity<List<Activity>> findAll() {
        try {
            List<Activity> activities = readActivitiesFromFile();
            activities.sort(Comparator.comparing(a -> a.getDescription().toLowerCase()));
            return ResponseEntity.ok(activities);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier: " + e.getMessage());
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> addActivity(@RequestBody Activity newActivity) {
        if (newActivity == null || newActivity.getDescription() == null || newActivity.getDescription().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("unvalid data");
        }

        // Validation des données
        if (newActivity.getFreqMin() < 15 || newActivity.getFreqMax() > 220 ||
                newActivity.getData().stream().anyMatch(d -> d.getCardioFrequency() < 15 || d.getCardioFrequency() > 220) ||
                newActivity.getData().stream().anyMatch(d -> Math.abs(d.getLatitude()) > 90 || Math.abs(d.getLongitude()) > 180)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("unvalid data");
        }

        try {
            List<Activity> activities = readActivitiesFromFile();

            boolean exists = activities.stream()
                    .anyMatch(activity -> activity.getDescription().trim()
                            .equalsIgnoreCase(newActivity.getDescription().trim()));

            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("failure");
            }

            activities.add(newActivity);
            writeActivitiesToFile(activities);

            return ResponseEntity.ok("success");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("failure");
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<String> deleteAllActivities() {
        try {
            Files.writeString(new File(DATA_FILE).toPath(), "[]");
            return ResponseEntity.ok("success");
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("failure");
        }
    }

    @DeleteMapping("/{desc}")
    public ResponseEntity<String> deleteActivity(@PathVariable String desc) {
        try {
            List<Activity> activities = readActivitiesFromFile();

            boolean removed = activities.removeIf(activity -> activity.getDescription().equalsIgnoreCase(desc));

            if (removed) {
                writeActivitiesToFile(activities);
                return ResponseEntity.ok("success");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("data does not exist");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression de l'activité: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("failure");
        }
    }

    @PostMapping("/{description}")
    public ResponseEntity<String> unauthorizedOperation(@PathVariable String description) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("unauthorized operation");
    }

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

    private void writeActivitiesToFile(List<Activity> activities) throws IOException {
        objectMapper.writeValue(new File(DATA_FILE), activities);
    }
}