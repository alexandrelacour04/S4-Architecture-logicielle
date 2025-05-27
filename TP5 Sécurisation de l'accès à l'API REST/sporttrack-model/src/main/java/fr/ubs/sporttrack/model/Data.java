package fr.ubs.sporttrack.model;

import org.json.JSONObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class Data {
    private static final String TIME_FIELD = "time";
    private static final String CARDIO_FREQ_FIELD = "cardio_frequency";
    private static final String LATITUDE_FIELD = "latitude";
    private static final String LONGITUDE_FIELD = "longitude";
    private static final String ALTITUDE_FIELD = "altitude";

    private String time;

    @Min(value = 15, message = "La fréquence cardiaque minimale est 15")
    @Max(value = 220, message = "La fréquence cardiaque maximale est 220")
    private int cardioFrequency;

    @Min(value = -90, message = "La latitude minimale est -90")
    @Max(value = 90, message = "La latitude maximale est 90")
    private float latitude;

    @Min(value = -180, message = "La longitude minimale est -180")
    @Max(value = 180, message = "La longitude maximale est 180")
    private float longitude;

    private double altitude;

    public Data() {
    }

    public Data(String time, int cf, float lat, float lon, int alt) {
        this.time = time;
        this.cardioFrequency = cf;
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
    }

    public String getTime() {
        return this.time;
    }

    public int getCardioFrequency() {
        return this.cardioFrequency;
    }

    public float getLatitude() {
        return this.latitude;
    }

    public float getLongitude() {
        return this.longitude;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public static Data fromJSON(JSONObject obj) {
        return new Data(obj.getString(TIME_FIELD), obj.getInt(CARDIO_FREQ_FIELD), obj.getFloat(LATITUDE_FIELD), obj.getFloat(LONGITUDE_FIELD), obj.getInt(ALTITUDE_FIELD));
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put(TIME_FIELD, this.time);
        obj.put(CARDIO_FREQ_FIELD, this.cardioFrequency);
        obj.put(LATITUDE_FIELD, this.latitude);
        obj.put(LONGITUDE_FIELD, this.longitude);
        obj.put(ALTITUDE_FIELD, this.altitude);
        return obj;
    }

    @Override
    public String toString() {
        return this.toJSON().toString();
    }
}