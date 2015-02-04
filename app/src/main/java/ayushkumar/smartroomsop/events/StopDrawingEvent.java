package ayushkumar.smartroomsop.events;

/**
 * Created by Ayush on 04-02-15.
 */
public class StopDrawingEvent {
    private float x;
    private float y;
    private Long time;

    public StopDrawingEvent(Long time, float y, float x) {
        this.time = time;
        this.y = y;
        this.x = x;
    }

    public StopDrawingEvent(StopDrawingBackgroundEvent stopDrawingBackgroundEvent) {
        this.time = stopDrawingBackgroundEvent.getTime();
        this.x = stopDrawingBackgroundEvent.getX();
        this.y = stopDrawingBackgroundEvent.getY();
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
