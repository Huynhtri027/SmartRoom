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
 * This contains information about how to start the drawing event in Open Mode.
 */
public class StartDrawingEvent {

    /*
     * The x-coordinate of this touch
     */
    private float x;

    /*
     * The y-coordinate of this touch
     */
    private float y;

    /*
     * The time delay after which this touch occurs
     */
    private Long time;

    /**
     * Constructor accepting all values
     * @param time The time delay after which this touch occurs
     * @param y The y-coordinate of this touch
     * @param x The x-coordinate of this touch
     */
    public StartDrawingEvent(Long time, float y, float x) {
        this.time = time;
        this.y = y;
        this.x = x;
    }

    /**
     * Create this event from the corresponding BackgroundEvent event object
     * @param startDrawingBackgroundEvent Background Event containing same information
     */
    public StartDrawingEvent(StartDrawingBackgroundEvent startDrawingBackgroundEvent) {
        this.time = startDrawingBackgroundEvent.getTime();
        this.x = startDrawingBackgroundEvent.getX();
        this.y = startDrawingBackgroundEvent.getY();
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
