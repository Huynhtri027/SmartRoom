package ayushkumar.smartroomsop.events;

/**
 * Created by ayush on 16/10/15.
 */
public class LoadProjectBackgroundEvent {
    private String filePath;

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
