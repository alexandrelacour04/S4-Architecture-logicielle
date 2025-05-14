package fr.ubs.sporttrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import fr.ubs.sporttrack.model.JSONFileReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fr.ubs.sporttrack.model.Activity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    // Chemin relatif du fichier JSON dans le module `model`
    private static final String DATA_FILE = "data.json";

    /**
     * Retourne une liste des activités en récupérant le fichier JSON depuis le JAR du module `model`.
     *
     * @return Liste des activités
     */
    @GetMapping("/")
    public List<Activity> findAll() {
        try {
            String userHome = System.getProperty("user.home");
            String tempDataPath = userHome + "/.m2/repository/fr/ubs/sporttrack/model/1.0/temp-data.json";
            File tempDataFile = new File(tempDataPath);

            if (tempDataFile.exists()) {
                // Si temp-data.json existe, on l'utilise directement
                return new JSONFileReader(tempDataFile).getActivities();
            }

            // Sinon on lit depuis le JAR
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
     *
     */
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
     * Ajout d'une nouvelle activité au fichier JSON
     */
    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> addActivity(@RequestBody Activity activity) {
        try {
            List<Activity> activities;
            String userHome = System.getProperty("user.home");
            String jsonFilePath = userHome + "/.m2/repository/fr/ubs/sporttrack/model/1.0/temp-data.json";
            File jsonFile = new File(jsonFilePath);

            // Configurer ObjectMapper pour gérer les noms de champs au format snake_case
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

            if (jsonFile.exists()) {
                System.out.println("Ajout dans le fichier existant");
                activities = objectMapper.readValue(
                        jsonFile,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Activity.class)
                );
            } else {
                System.out.println("Création dans le fichier temp");
                activities = findAll();
            }

            activities.add(activity);
            objectMapper.writeValue(jsonFile, activities);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("L'activité a été créée avec succès");
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'activité", e);
        }
    }

}
