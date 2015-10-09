package ayushkumar.smartroomsop.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by ayush on 07/10/15.
 */
public class PageEndTimesModel {
    @SerializedName("eT") private HashMap<Integer, Long> endTimesForPage;

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
