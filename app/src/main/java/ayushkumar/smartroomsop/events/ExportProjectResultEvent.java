package ayushkumar.smartroomsop.events;

/**
 * Created by Ayush Kumar on 16/10/15.
 *
 * @author Ayush Kumar
 *
 * This class is used to send an event to EventBus (https://github.com/greenrobot/EventBus)
 * This event should be received in the UI Thread (Main Thread of Android). (Enforced by programmer)
 * This will be fired when the export of the Project has ended
 */
public class ExportProjectResultEvent {

    /*
     * Boolean indicating file exists or not
     */
    private boolean fileExists;

    /**
     * Constructor accepting default values
     * @param fileExists Boolean indicating file exists or not
     */
    public ExportProjectResultEvent(boolean fileExists) {
        this.fileExists = fileExists;
    }

    //Getters & Setters

    public boolean isFileExists() {
        return fileExists;
    }

    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }
}
