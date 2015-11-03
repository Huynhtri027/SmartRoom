package ayushkumar.smartroomsop.events;

/**
 * Created by ayush on 18/10/15.
 */
public class CopyProjectBackgroundEvent {
    private String filePath;
    private String fileName;

    public CopyProjectBackgroundEvent(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

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
