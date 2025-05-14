package fr.ubs.sporttrack.model;

import jakarta.validation.Valid;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;
import java.util.ArrayList;
import jakarta.validation.constraints.*;

public class Activity {
    private static final String DATE_FIELD = "date";
    private static final String DESC_FIELD = "description";
    private static final String DISTANCE_FIELD = "distance";
    private static final String FREQ_MIN_FIELD = "freq_min";
    private static final String FREQ_MAX_FIELD = "freq_max";
    private static final String DATA_FIELD = "data";

    @NotNull(message = "La date doit être renseignée.")
    private String date;

    @NotNull(message = "La description doit être renseignée.")
    @Size(min = 5, max = 100, message = "La description doit contenir entre 5 et 100 caractères.")
    private String description;

    @Min(value = 0, message = "La distance doit être au moins 0.")
    private int distance;

    @Min(value = 40, message = "La fréquence minimale doit être d'au moins 40.")
    @Max(value = 200, message = "La fréquence minimale ne peut pas dépasser 200.")
    private int freqMin;

    @Min(value = 40, message = "La fréquence maximale doit être d'au moins 40.")
    @Max(value = 220, message = "La fréquence maximale ne peut pas dépasser 220.")
    private int freqMax;

    @NotNull(message = "Les données de l'activité sont obligatoires.")
    private List<@Valid Data> data;

    public Activity() {
    }

    public Activity(String date, String desc, int dist, int fmin, int fmax, List<Data> data) {
        this.date = date;
        this.description = desc;
        this.distance = dist;
        this.freqMin = fmin;
        this.freqMax = fmax;
        this.data = data;
    }

 
    public String getDate(){
        return this.date;
    }

    public String getDescription(){
        return this.description;
    }

    public int getFreqMin(){
        return this.freqMin;
    }

    public int getFreqMax(){
        return this.freqMax;
    }

    public int getDistance(){
        return this.distance;
    }

    public List<Data> getData(){
        return this.data;
    }

    public static Activity fromJSON(JSONObject obj){
        List<Data> data = new ArrayList<>();
        Activity act = new Activity(obj.getString(DATE_FIELD), obj.getString(DESC_FIELD), obj.getInt(DISTANCE_FIELD), obj.getInt(FREQ_MIN_FIELD), obj.getInt(FREQ_MAX_FIELD), data);
        JSONArray dataArray = obj.getJSONArray(DATA_FIELD);
        if(dataArray != null){
            for (int i = 0; i < dataArray.length() ; i++){
                JSONObject o = dataArray.getJSONObject(i);
                data.add(Data.fromJSON(o));
            }
        }
        return act;
    }

    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put(DATE_FIELD, this.date);
        obj.put(DESC_FIELD, this.description);
        obj.put(DISTANCE_FIELD, this.distance);
        obj.put(FREQ_MIN_FIELD, this.freqMin);
        obj.put(FREQ_MAX_FIELD, this.freqMax);
        JSONArray dataArray = new JSONArray();
        obj.put(DATA_FIELD, dataArray);
        if(this.data != null){
            for(Data d : this.data){
                dataArray.put(d.toJSON());
            }    
        }
        return obj;
    }

    @Override
    public String toString(){
        return this.toJSON().toString();
    }
}
