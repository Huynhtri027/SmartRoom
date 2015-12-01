package ayushkumar.smartroomsop.events;

import java.io.File;

/**
 * Created by Ayush Kumar on 11/11/15.
 *
 * @author Ayush Kumar
 *
 * This class is used to send an event to EventBus (https://github.com/greenrobot/EventBus)
 * This event should be received in the Background Thread. (Enforced by programmer)
 * This contains information about how to upload a project to a server in the background.
 */
public class UploadProjectBackgroundEvent {

    /*
     * The title of the Project
     */
    private String title;

    /*
     * The author of the Project
     */
    private String author;

    /*
     * The description of the Project
     */
    private String description;

    /*
     * The Project file to be uploaded
     */
    private File file;

    /**
     * Constructor
     * @param title The title of the Project
     * @param author The author of the Project
     * @param description The description of the Project
     * @param file The Project file to be uploaded
     */
    public UploadProjectBackgroundEvent(String title, String author, String description, File file) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.file = file;
    }

    // Getters & Setters

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
