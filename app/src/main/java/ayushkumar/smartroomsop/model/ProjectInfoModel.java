package ayushkumar.smartroomsop.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ayush on 16/10/15.
 */
public class ProjectInfoModel {

    @SerializedName("name") private String name;
    @SerializedName("desc") private String description;

    public ProjectInfoModel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
