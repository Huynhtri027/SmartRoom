package ayushkumar.smartroomsop.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ayush Kumar on 13-04-15.
 *
 * @author Ayush Kumar
 *
 * Model class to support Serialisation & De-Serialisation.
 * Serialisation uses the GSON library (https://github.com/google/gson)
 *
 * This model contains overall information about the Project
 */
public class InfoModel {

    /*
     * The total number of pages/slides in the Project
     */
    @SerializedName("tpg") private int totalPages;

    /*
     * The total time required for the Project
     */
    @SerializedName("tt") private Long totalTime;

    /**
     * Constructor
     * @param totalPages The total number of pages/slides in the Project
     */
    public InfoModel(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * Constructor
     * @deprecated TODO: Not used anymore. Remove?
     * @param totalPages The total number of pages/slides in the Project
     * @param totalTime The total time required for the Project
     */
    public InfoModel(int totalPages, Long totalTime) {

        this.totalPages = totalPages;
        this.totalTime = totalTime;
    }

    // Getters & Setters

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }


}
