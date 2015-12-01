package ayushkumar.smartroomsop.events;

/**
 * Created by Ayush Kumar on 18/10/15.
 *
 * @author Ayush Kumar
 *
 * This class is used to send an event to EventBus (https://github.com/greenrobot/EventBus)
 * This event should be received in the Background Thread. (Enforced by programmer)
 * This contains information about how to copy a project in the background.
 * This event will be fired up when someone clicks on an .smroom file
 * This will result in the corresponding project being copied to the application's storage.
 */
public class CopyProjectBackgroundEvent {

    /*
     * The full file path of the Project
     */
    private String filePath;

    /*
     * The name of the file of the Project
     */
    private String fileName;

    /**
     * Constructor accepting default values
     * @param filePath The full file path of the Project
     * @param fileName The name of the file of the Project
     */
    public CopyProjectBackgroundEvent(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    //Getters & Setters

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
