package ayushkumar.smartroomsop.events;

/**
 * Created by Ayush Kumar on 11/11/15.
 *
 * @author Ayush Kumar
 *
 *  This class is used to send an event to EventBus (https://github.com/greenrobot/EventBus)
 * This event should be received in the UI Thread (Main Thread of Android). (Enforced by programmer)
 * This will be fired when the uploading of the Project has ended
 */
public class UploadProjectResultEvent {

    /*
     * The result of the upload
     */
    private String result;

    /**
     * Constructor
     * @param result The result of the upload
     */
    public UploadProjectResultEvent(String result) {
        this.result = result;
    }

    // Getters & Setters

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
