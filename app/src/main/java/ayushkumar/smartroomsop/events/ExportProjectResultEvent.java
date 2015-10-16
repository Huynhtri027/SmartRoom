package ayushkumar.smartroomsop.events;

/**
 * Created by ayush on 16/10/15.
 */
public class ExportProjectResultEvent {

    private boolean fileExists;

    public ExportProjectResultEvent(boolean fileExists) {
        this.fileExists = fileExists;
    }

    public boolean isFileExists() {
        return fileExists;
    }

    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }
}
