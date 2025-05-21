package fr.ubs.sporttrack;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ubs.sporttrack.model.Activity;
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
class FullCoverageActivityControllerTest {

    private static final String USERNAME = "sporttrack";
    private static final String PASSWORD = "sporttrack";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Activity validActivity1;
    private Activity validActivity2;
    private Activity invalidActivity;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(delete("/activities/clearDB")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk());

        validActivity1 = new Activity(
                "01/01/2025",
                "Running in the park",
                10,
                60,
                180,
                new ArrayList<>()
        );

        validActivity2 = new Activity(
                "02/01/2025",
                "Morning cycling",
                15,
                70,
                160,
                new ArrayList<>()
        );

        invalidActivity = new Activity(
                "04/05/2025",
                "Invalid activity",
                0,
                0,
                0,
                null
        );
    }

    @Test
    void testFindAllInitialEmpty() throws Exception {
        mockMvc.perform(get("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testAddValidActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isCreated())
                .andExpect(content().string("L'activité a été créée avec succès"));
    }

    @Test
    void testAddDuplicateActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Une activité avec la date '01/01/2025' existe déjà."));
    }

    @Test
    void testAddInvalidActivityValidation() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidActivity)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Données non valides")));
    }

    @Test
    void testFindByKeywordSuccess() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/activities/park")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Running in the park"));
    }

    @Test
    void testFindByKeywordNoResults() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/activities/cycling")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testDeleteActivitySuccess() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/activities/Running in the park")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testDeleteActivityNotFound() throws Exception {
        mockMvc.perform(delete("/activities/NonExistentActivity")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Activité avec la description 'NonExistentActivity' non trouvée."));
    }

    @Test
    void testClearDatabase() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/activities/clearDB")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string("Base de données réinitialisée avec succès"));

        mockMvc.perform(get("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testAddActivityIOException() throws Exception {
        // Simuler une IOException en ajoutant une activité à un fichier inaccessible
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Invalid JSON format"))
                .andExpect(status().isBadRequest());
    }
}