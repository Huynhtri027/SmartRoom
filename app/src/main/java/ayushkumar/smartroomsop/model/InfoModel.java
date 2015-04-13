package ayushkumar.smartroomsop.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ayush on 13-04-15.
 */
public class InfoModel {
    @SerializedName("tpg") private int totalPages;
    @SerializedName("tt") private Long totalTime;

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

    public InfoModel(int totalPages, Long totalTime) {

        this.totalPages = totalPages;
        this.totalTime = totalTime;
    }
}
