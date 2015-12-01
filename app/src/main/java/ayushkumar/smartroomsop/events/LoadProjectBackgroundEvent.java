package ayushkumar.smartroomsop.events;

/**
 * Created by Ayush Kumar on 16/10/15.
 *
 * @author Ayush Kumar
 *
 * This class is used to send an event to EventBus (https://github.com/greenrobot/EventBus)
 * This event should be received in the Background Thread. (Enforced by programmer)
 * This contains information about how to load a project in the background. (Copying relevant files to proper locations)
 * The files in the Project file are separated, and are then copied to the appropriate locations
 */
public class LoadProjectBackgroundEvent {

    /*
     * The path of the Project file to load
     */
    private String filePath;

    /**
     * Constructor
     * @param filePath The path of the Project file to load
     */
    public LoadProjectBackgroundEvent(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
