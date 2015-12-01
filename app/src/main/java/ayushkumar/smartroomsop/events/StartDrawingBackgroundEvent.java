package ayushkumar.smartroomsop.events;

/**
 * Created by Ayush Kumar on 04-02-15.
 *
 * @author Ayush Kumar
 *
 * This class is used to send an event to EventBus (https://github.com/greenrobot/EventBus)
 * This event should be received in the Background Thread. (Enforced by programmer)
 * This contains information about how to start drawing in Open Mode
 */
public class StartDrawingBackgroundEvent {
    /*
     * The x-coordinate of the touch
     */
    private float x;

    /*
     * The y-coordinate of the touch
     */
    private float y;

    /*
     * The time delay after which this touch occurs
     */
    private Long time;

    /**
     * Constructor accepting all values
     * @param time The time delay after which this touch occurs
     * @param y The y-coordinate of the touch
     * @param x The x-coordinate of the touch
     */
    public StartDrawingBackgroundEvent(Long time, float y, float x) {
        this.time = time;
        this.y = y;
        this.x = x;
    }

    // Getters & Setters

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
