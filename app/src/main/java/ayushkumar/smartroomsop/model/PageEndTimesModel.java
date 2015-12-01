package ayushkumar.smartroomsop.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by Ayush Kumar on 07/10/15.
 *
 * @author Ayush Kumar
 *
 * Model class to support Serialisation & De-Serialisation.
 * Serialisation uses the GSON library (https://github.com/google/gson)\
 *
 * This model stores information about the end times (for audio) of all pages/slides of the Project
 */
public class PageEndTimesModel {

    /*
     * Mapping of pages to their audio end times
     */
    @SerializedName("eT") private HashMap<Integer, Long> endTimesForPage;

    /**
     * Constructor
     * @param endTimesForPage Mapping of pages to their audio end times
     */
    public PageEndTimesModel(HashMap<Integer, Long> endTimesForPage) {
        this.endTimesForPage = endTimesForPage;
    }

    public HashMap<Integer, Long> getEndTimesForPage() {
        return endTimesForPage;
    }

    public void setEndTimesForPage(HashMap<Integer, Long> endTimesForPage) {
        this.endTimesForPage = endTimesForPage;
    }
}
