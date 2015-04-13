package ayushkumar.smartroomsop.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ayush on 13-04-15.
 */
public class InputModel {
    @SerializedName("pg") private int pageNumber;
    private char type;
    private float x;
    private float y;
    @SerializedName("t") private Long time;

    public InputModel(int pageNumber, char type, float x, float y, Long time) {
        this.pageNumber = pageNumber;
        this.type = type;
        this.x = x;
        this.y = y;
        this.time = time;
    }

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
