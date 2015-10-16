package ayushkumar.smartroomsop.events;

/**
 * Created by ayush on 16/10/15.
 */
public class ExportProjectBackgroundEvent {

    private String name;

    public ExportProjectBackgroundEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
