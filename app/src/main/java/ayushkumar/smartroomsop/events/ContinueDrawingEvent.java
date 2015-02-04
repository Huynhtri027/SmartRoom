package ayushkumar.smartroomsop.events;

/**
 * Created by Ayush on 04-02-15.
 */
public class ContinueDrawingEvent {
    private float x;
    private float y;
    private Long time;

    public ContinueDrawingEvent(Long time, float y, float x) {
        this.time = time;
        this.y = y;
        this.x = x;
    }

    public ContinueDrawingEvent(ContinueDrawingBackgroundEvent continueDrawingBackgroundEvent) {
        this.time = continueDrawingBackgroundEvent.getTime();
        this.x = continueDrawingBackgroundEvent.getX();
        this.y = continueDrawingBackgroundEvent.getY();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
