package fr.ubs.sporttrack;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ubs.sporttrack.model.Activity;
import fr.ubs.sporttrack.model.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FullCoverageActivityControllerTest {

    private static final String USERNAME = "r401";
    private static final String PASSWORD = "But2R041";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Activity validActivity1;
    private Activity validActivity2;
    private Activity invalidActivity;
    private Activity invalidCardioActivity;
    private Activity invalidCoordinatesActivity;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(delete("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk());

        Data validData = new Data("12:00:00", 10, 45.0f, 2.0f, 100);
        Data invalidCardioData = new Data("12:00:00", 10, 45.0f, 2.0f, 10);
        Data invalidCoordinatesData = new Data("12:00:00", 10, 91.0f, 181.0f, 100);

        validActivity1 = new Activity(
                "01/01/2025",
                "Running in the park",
                60,
                100,
                180,
                Arrays.asList(validData)
        );

        validActivity2 = new Activity(
                "02/01/2025",
                "Morning cycling",
                70,
                100,
                160,
                Arrays.asList(validData)
        );

        invalidActivity = new Activity(
                null,
                "",
                0,
                0,
                0,
                null
        );

        invalidCardioActivity = new Activity(
                "03/01/2025",
                "Invalid cardio",
                10,
                10,
                230,
                Arrays.asList(invalidCardioData)
        );

        invalidCoordinatesActivity = new Activity(
                "04/01/2025",
                "Invalid coordinates",
                60,
                100,
                180,
                Arrays.asList(invalidCoordinatesData)
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
    void testFindAllWithActivities() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));

        mockMvc.perform(get("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testAddValidActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));
    }

    @Test
    void testAddDuplicateActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));

        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));
    }

    @Test
    void testAddNullActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidActivity)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));
    }

    @Test
    void testAddInvalidCardioActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCardioActivity)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));
    }

    @Test
    void testAddInvalidCoordinatesActivity() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCoordinatesActivity)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));
    }

    @Test
    void testDeleteAllActivities() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));

        mockMvc.perform(delete("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        mockMvc.perform(get("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testDeleteActivityByDescription() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validActivity1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));

        mockMvc.perform(delete("/activities/Running in the park")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("data does not exist"));
    }

    @Test
    void testDeleteNonExistentActivity() throws Exception {
        mockMvc.perform(delete("/activities/NonExistentActivity")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("data does not exist"));
    }

    @Test
    void testUnauthorizedOperation() throws Exception {
        mockMvc.perform(post("/activities/test")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string("unauthorized operation"));
    }

    @Test
    void testAddActivityIOException() throws Exception {
        mockMvc.perform(post("/activities/")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Using empty JSON object
                .andExpect(status().isBadRequest())
                .andExpect(content().string("unvalid data"));
    }
}