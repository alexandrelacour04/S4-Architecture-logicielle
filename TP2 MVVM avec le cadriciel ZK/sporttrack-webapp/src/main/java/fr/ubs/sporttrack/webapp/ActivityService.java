package fr.ubs.sporttrack.webapp;

import fr.ubs.sporttrack.model.Activity;
import fr.ubs.sporttrack.model.JSONFileReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class ActivityService {

    // Chemin relatif du fichier JSON dans le module `model`
    private static final String dataFile = "data.json";

    /**
     * Retourne une liste des activités en récupérant le fichier JSON depuis le JAR du module `model`.
     *
     * @return Liste des activités
     */
    public List<Activity> findAll() {
        try {

            String userHome = System.getProperty("user.home");
            String jarPath = userHome + "/.m2/repository/fr/ubs/sporttrack/model/1.0/model-1.0.jar";
            File jarFile = new File(jarPath);

            if (!jarFile.exists()) {
                throw new IllegalArgumentException("JAR du module `model` introuvable : " + jarPath);
            }

            try (JarFile jar = new JarFile(jarFile)) {
                ZipEntry entry = jar.getEntry(dataFile);
                if (entry == null) {
                    throw new IllegalArgumentException("Fichier non trouvé dans le JAR : " + dataFile);
                }

                File tempFile = File.createTempFile("data", ".json");
                tempFile.deleteOnExit();
                Files.copy(jar.getInputStream(entry), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                return new JSONFileReader(tempFile).getActivities();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier JSON : " + dataFile, e);
        }
    }

    public List<Activity> search(String keyword) {
        List<Activity> activities = findAll();
        List<Activity> searchList = new ArrayList<>();
        for (Activity activity : activities) {
            if (activity.getDescription().contains(keyword)) {
                searchList.add(activity);
            }
        }
        return searchList;
    }
}