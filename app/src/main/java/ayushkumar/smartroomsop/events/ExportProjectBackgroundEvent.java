package ayushkumar.smartroomsop.events;

/**
 * Created by Ayush Kumar on 16/10/15.
 *
 * @author Ayush Kumar
 *
 * This class is used to send an event to EventBus (https://github.com/greenrobot/EventBus)
 * This event should be received in the Background Thread. (Enforced by programmer)
 * This event contains information of exporting the Project into a custom file format(.smroom)
 */
public class ExportProjectBackgroundEvent {

    /*
     * The name of the Project
     */
    private String name;

    /**
     * Constructor accepting default values
     * @param name Name of project
     */
    public ExportProjectBackgroundEvent(String name) {
        this.name = name;
    }

    //Getters & Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
