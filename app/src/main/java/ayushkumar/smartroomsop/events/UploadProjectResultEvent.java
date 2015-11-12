package ayushkumar.smartroomsop.events;

/**
 * Created by ayush on 11/11/15.
 */
public class UploadProjectResultEvent {

    private String result;

    public UploadProjectResultEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
