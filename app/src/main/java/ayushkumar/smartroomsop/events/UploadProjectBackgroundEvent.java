package ayushkumar.smartroomsop.events;

import java.io.File;

/**
 * Created by ayush on 11/11/15.
 */
public class UploadProjectBackgroundEvent {
    private String title;
    private String description;
    private File file;

    public UploadProjectBackgroundEvent(String title, String description, File file) {
        this.title = title;
        this.description = description;
        this.file = file;
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
