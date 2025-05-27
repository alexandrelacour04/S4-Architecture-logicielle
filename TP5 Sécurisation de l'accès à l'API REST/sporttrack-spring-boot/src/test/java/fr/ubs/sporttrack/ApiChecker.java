package fr.ubs.sporttrack;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ubs.sporttrack.model.Activity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiChecker {

    private static final String USERNAME = "r401";
    private static final String PASSWORD = "But2R041";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Activity validActivity1;
    private Activity validActivity2;
    private Activity invalidFrequencyActivity;
    private Activity invalidLogicActivity;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(delete("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk());

        validActivity1 = new Activity(
                "01/01/2025",
                "Running in the park",
                60,
                180,
                10,
                new ArrayList<>()
        );

        validActivity2 = new Activity(
                "02/01/2025",
                "Cycling on the road",
                65,
                175,
                20,
                new ArrayList<>()
        );

        invalidFrequencyActivity = new Activity(
                "03/01/2025",
                "Invalid heart rate",
                10,
                300,
                5,
                new ArrayList<>()
        );

        invalidLogicActivity = new Activity(
                "04/01/2025",
                "",
                0,
                0,
                0,
                null
        );
    }

    @Test
    @Operation(summary = "Vérifie que la base de données est vide initialement")
    @ApiResponse(responseCode = "200", description = "Base de données vide vérifiée")
    void test_0_DatabaseInitiallyEmpty() throws Exception {
        mockMvc.perform(get("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Operation(summary = "Ajoute une première activité valide")
    @ApiResponse(responseCode = "200", description = "Activité créée avec succès")
    void test_1_AddFirstValidActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }

    @Test
    @Operation(summary = "Ajoute une seconde activité valide")
    @ApiResponse(responseCode = "200", description = "Activité créée avec succès")
    void test_2_AddSecondValidActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity2)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }

    @Test
    @Operation(summary = "Tente d'ajouter une activité en doublon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Première activité créée avec succès"),
            @ApiResponse(responseCode = "409", description = "La deuxième activité est un doublon")
    })
    void test_3_AddDuplicateActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity2)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity2)))
                .andExpect(status().isConflict())
                .andExpect(content().string("failure"));
    }

    @Test
    @Operation(summary = "Vérifie que deux activités ont été ajoutées")
    @ApiResponse(responseCode = "200", description = "Les deux activités sont présentes")
    void test_4_VerifyTwoActivitiesAdded() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity2)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        mockMvc.perform(get("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @Operation(summary = "Tente d'ajouter une activité avec une fréquence invalide")
    @ApiResponse(responseCode = "400", description = "Données non valides")
    void test_5_AddInvalidFrequencyActivity() throws Exception {
        invalidFrequencyActivity = new Activity(
                "03/01/2025",
                "Invalid heart rate",
                10,
                5, // freq_min
                300, // freq_max
                new ArrayList<>()
        );

        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFrequencyActivity)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));
    }

    @Test
    @Operation(summary = "Tente d'ajouter une activité avec une logique invalide")
    @ApiResponse(responseCode = "400", description = "Données non valides")
    void test_6_AddInvalidLogicActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLogicActivity)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));
    }

    @Test
    @Operation(summary = "Tente d'effectuer un POST sur une URL interdite")
    @ApiResponse(responseCode = "405", description = "Méthode non autorisée")
    void test_7_PostToForbiddenUrl() throws Exception {
        mockMvc.perform(post("/activities/act1")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string("unauthorized operation"));
    }

    @Operation(summary = "Supprime une activité existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activité supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Activité non trouvée")
    })
    @Test
    void test_8_DeleteExistingActivity() throws Exception {
        // Ajouter une activité pour garantir qu'elle existe
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        // Supprimer l'activité ajoutée
        mockMvc.perform(delete("/activities/Running in the park")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        // Vérifier que l'activité a été supprimée
        mockMvc.perform(get("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Operation(summary = "Tente de supprimer une activité déjà supprimée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Première suppression réussie"),
            @ApiResponse(responseCode = "404", description = "Activité non trouvée lors de la seconde suppression")
    })
    void test_9_DeleteAlreadyDeletedActivity() throws Exception {
        // Ajouter d'abord une activité valide
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        // Première suppression
        mockMvc.perform(delete("/activities/Running in the park")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        // Deuxième suppression de la même activité
        mockMvc.perform(delete("/activities/Running in the park")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("data does not exist"));
    }

    @Test
    @Operation(summary = "Réinitialise la base de données")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Base de données réinitialisée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la réinitialisation de la base de données")
    })
    void test_10_ClearDatabase() throws Exception {
        // Ajout d'une activité pour vérifier la réinitialisation
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        // Réinitialisation de la base de données
        mockMvc.perform(delete("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        // Vérification que la base est vide
        mockMvc.perform(get("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}