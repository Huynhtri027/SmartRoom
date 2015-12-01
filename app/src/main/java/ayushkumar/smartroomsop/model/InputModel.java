package ayushkumar.smartroomsop.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ayush Kumar on 13-04-15.
 *
 * @author Ayush Kumar
 *
 * Model class to support Serialisation & De-Serialisation.
 * Serialisation uses the GSON library (https://github.com/google/gson)
 *
 * This model contains information about a single touch in a page
 */
public class InputModel {

    /*
     * Page number of the touch in the Project
     */
    @SerializedName("pg") private int pageNumber;

    /*
     * The type of touch (Can be start ('s'), move ('m'), or the end('e')
     */
    private char type;

    /*
     * The x-coordinate of the touch
     */
    private float x;

    /*
     * The y-coordinate of the touch
     */
    private float y;

    /*
     * The time delay of the touch from the previous touch
     */
    @SerializedName("t") private Long time;

    /**
     * Constructor
     * @param pageNumber Page number of the touch in the Project
     * @param type The type of touch (Can be start ('s'), middle ('m'), or the end('e')
     * @param x The x-coordinate of the touch
     * @param y The y-coordinate of the touch
     * @param time The time delay of the touch from the previous touch
     */
    public InputModel(int pageNumber, char type, float x, float y, Long time) {
        this.pageNumber = pageNumber;
        this.type = type;
        this.x = x;
        this.y = y;
        this.time = time;
    }

    // Getters & Setters

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
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
