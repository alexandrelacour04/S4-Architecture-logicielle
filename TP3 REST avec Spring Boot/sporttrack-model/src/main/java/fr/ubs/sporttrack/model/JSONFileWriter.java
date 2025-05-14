package fr.ubs.sporttrack.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JSONFileWriter{


    private File file;

    /**
     * Creates a JSONFileWriter object that opens the file specified
     * as parameter in order to write objects of type
     * <code>Activity</code> into it.
     *
     * @param f The file that must be opened.
     * @throws IOException if the file cannot be open in write mode.
     */
    public JSONFileWriter(File f) throws IOException {
        this.file = f;
        if (!f.canWrite() && f.exists()) {
            throw new IOException("File cannot be opened in write mode");
        }
    }


    /**
     * Writes a list of objects of type <code>Activity</code> into
     * the file.
     *
     * @param activities The activities that must be written into the file.
     * @throws IOException if an error occurs while writting data.
     */
    public void writeData(List<Activity> activities) throws IOException {
        if (activities == null) {
            throw new IOException("Activities list cannot be null");
        }
        org.json.JSONArray jsonArray = new org.json.JSONArray();
        for (Activity activity : activities) {
            if (activity == null) {
                throw new IOException("Activity cannot be null");
            }
            jsonArray.put(activity.toJSON());
        }
        java.nio.file.Files.writeString(this.file.toPath(), jsonArray.toString(2));
    }


    /**
     * Closes the file.
     *
     * @throws IOException if an error occurs while closing the file.
     */
    public void close() throws IOException {

    }

}
