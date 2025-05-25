package fr.ubs.json;

import fr.ubs.sporttrack.model.Activity;
import fr.ubs.sporttrack.model.Data;
import fr.ubs.sporttrack.model.JSONFileReader;
import fr.ubs.sporttrack.model.JSONFileWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main{

    public static void main(String[] args) {
        try {
// read the file from resources
            String resourcePath = "data.json";
            File jsonFile = new File("target/classes/data.json");
            JSONFileReader reader = new JSONFileReader(jsonFile);
            List<Activity> activities = reader.getActivities();

            // print the content
            for (Activity activity : activities) {
                System.out.println(activity);
            }

            // add new data into the list
            List<Data> dataList = new ArrayList<>();
            dataList.add(new Data("12:00:00", 120, 48.5f, -2.75f, 100));
            dataList.add(new Data("12:00:30", 125, 48.51f, -2.74f, 102));
            activities.add(new Activity("2025-04-23", "test de bon fonctionnement", 1000, 110, 140, dataList));

            // store the list into the file
            JSONFileWriter writer = new JSONFileWriter(jsonFile);
            writer.writeData(activities);
            writer.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
