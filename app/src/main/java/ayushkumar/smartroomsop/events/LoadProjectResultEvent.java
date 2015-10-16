package ayushkumar.smartroomsop.events;

/**
 * Created by ayush on 16/10/15.
 */
public class LoadProjectResultEvent {
    private boolean loaded;

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
