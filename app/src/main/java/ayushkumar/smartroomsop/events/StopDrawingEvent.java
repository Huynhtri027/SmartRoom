package ayushkumar.smartroomsop.events;

/**
 * Created by Ayush Kumar on 04-02-15.
 *
 * @author Ayush Kumar
 *
 * This class is used to send an event to EventBus (https://github.com/greenrobot/EventBus)
 * This event should be received in the Main Thread(UI Thread of Android). (Enforced by programmer)
 * The UI elements can only be updated by the UI thread in Android. So this event is necessary.
 * Will have a corresponding BackgroundEvent event class too.
 * This contains information about how to stop the drawing event in Open Mode.
 */
public class StopDrawingEvent {

    /*
     * The x-coordinate of the last touch
     */
    private float x;

    /*
     * The y-coordinate of the last touch
     */
    private float y;

    /*
     * The time delay after which the last touch occurs
     */
    private Long time;

    /**
     * Constructor
     * @param time The time delay after which the last touch occurs
     * @param y The y-coordinate of the last touch
     * @param x The x-coordinate of the last touch
     */
    public StopDrawingEvent(Long time, float y, float x) {
        this.time = time;
        this.y = y;
        this.x = x;
    }

    /**
     * Create this event from the corresponding BackgroundEvent event object
     * @param stopDrawingBackgroundEvent Background Event containing same information
     */
    public StopDrawingEvent(StopDrawingBackgroundEvent stopDrawingBackgroundEvent) {
        this.time = stopDrawingBackgroundEvent.getTime();
        this.x = stopDrawingBackgroundEvent.getX();
        this.y = stopDrawingBackgroundEvent.getY();
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
