package ayushkumar.smartroomsop.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ayush Kumar on 16/10/15.
 *
 * @author Ayush Kumar
 *
 * Model class to support Serialisation & De-Serialisation.
 * Serialisation uses the GSON library (https://github.com/google/gson)
 *
 * This model stores information about the Project
 */
public class ProjectInfoModel {

    /*
     * Name of the Project
     */
    @SerializedName("name") private String name;

    /*
     * The description of the Project
     */
    @SerializedName("desc") private String description;

    /*
     * The author of the Project
     */
    @SerializedName("author") private String author;

    /**
     * Constructor
     * @param name Name of the Project
     * @param description The description of the Project
     * @param author The author of the Project
     */
    public ProjectInfoModel(String name, String description, String author) {
        this.name = name;
        this.description = description;
        this.author = author;
    }

    // Getters & Setters

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
