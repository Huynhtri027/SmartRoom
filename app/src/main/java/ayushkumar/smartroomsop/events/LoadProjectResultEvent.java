package ayushkumar.smartroomsop.events;

/**
 * Created by Ayush Kumar on 16/10/15.
 *
 * @author Ayush Kumar
 *
 * This class is used to send an event to EventBus (https://github.com/greenrobot/EventBus)
 * This event should be received in the UI Thread (Main Thread of Android). (Enforced by programmer)
 * This will be fired when the load of the Project has completed.
 * After the end of the load, the files of the Project file will be separated, and put into appropriate locations
 */
public class LoadProjectResultEvent {
    /*
     * Boolean indicating whether the project was loaded or not
     */
    private boolean loaded;

    /**
     * Constructor
     * @param loaded Boolean indicating whether the project was loaded or not
     */
    public LoadProjectResultEvent(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
