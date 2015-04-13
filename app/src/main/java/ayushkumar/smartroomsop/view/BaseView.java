package ayushkumar.smartroomsop.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ayushkumar.smartroomsop.model.InfoModel;
import ayushkumar.smartroomsop.model.InputModel;
import ayushkumar.smartroomsop.util.Constants;

/**
 * Created by Ayush on 22-01-15.
 */
public class BaseView extends View {

    private static final String TAG = "BaseView";

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private File file;
    private File infoFile;
    private FileOutputStream fileOutputStream;
    private FileOutputStream infoFileOutputStream;
    private Long startTime;
    private boolean createMode;
    private int totalPages = 1;
    private int currentPage = 1;
    private ArrayList<InputModel> buffer;
    Gson gson;

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context) {
        super(context);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        buffer = new ArrayList<>();
        gson = new Gson();
    }

    public BaseView(Context context, Paint mPaint){
        this(context);
        this.mPaint = mPaint;
    }

    /**
     * Constructor to be used
     * @param context context
     * @param mPaint The paint object
     * @param createMode Whether in Create Mode or Open Mode
     */
    public BaseView(Context context, Paint mPaint, boolean createMode){
        this(context,mPaint);
        this.createMode = createMode;
        if(createMode){
            initFile(context);
        }
    }

    /**
     * Initialize File object
     * @param context  Context
     */
    public void initFile(Context context) {
        String state = Environment.getExternalStorageState();
        boolean check;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            check = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            check = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            check = false;
        }
        if (check) {
            file = new File(context.getExternalFilesDir(null)
                    + File.separator + (Constants.filename));
            infoFile = new File(context.getExternalFilesDir(null) + File.separator + (Constants.infofile));
            try {
                fileOutputStream = new FileOutputStream(file);
                infoFileOutputStream = new FileOutputStream((infoFile));
            } catch (FileNotFoundException e) {
                Log.d(TAG,"File not found");
                e.printStackTrace();
            }
        }else{
            Toast.makeText(context,"Can't access SD Card.",Toast.LENGTH_LONG).show();
            Log.i(TAG,"Mem card not available?");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFAAAAAA);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;
    /**
     * Tolerance value for touch events
     */
    private static final float TOUCH_TOLERANCE = 4;


    /**
     * Store coordinates in file
     * @param x x-coordinate
     * @param y y-coordinate
     * @param type 's' for Start, 'm' for Move, 'e' for End
     */
    private void storeValues(float x, float y, char type) {
        try {
            fileOutputStream = new FileOutputStream(file,true);
        } catch (FileNotFoundException e) {
            Log.d(TAG,"File not found");
            e.printStackTrace();
        }

        //Subtract startTime from current time to reduce size of data
        String data = (System.currentTimeMillis() - startTime) + ":" + x + "," + y + ":" + type + ":" + currentPage + "\n" ;
        try {
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Store coordinates in file using JSON format
     * @param x x-coordinate
     * @param y y-coordinate
     * @param type 's' for Start, 'm' for Move, 'e' for End
     */
    private void storeValuesAsJSON(float x, float y, char type) {

        //Subtract startTime from current time to reduce size of data
        InputModel inputModel = new InputModel(currentPage,type,x,y,System.currentTimeMillis() - startTime);
        buffer.add(inputModel);

    }

    /**
     * Start of drawing upon touch. Function reused in both Craete & Open Mode
     * @param x x-coordinate
     * @param y y-coordinate
     */
    private void touch_start(float x, float y) {
        /*//Store initial coordinates
        Log.d(TAG, "Time: " + System.currentTimeMillis() + ", Coordinates: (" + x + "," + y + ")" );
        storeValues(x,y);*/

        //Set start time if not already set
        //TODO Find better way to approach this(As this has to be taken care of in other functions manually, eg clearCanvas)
        if(startTime == null){
            startTime = System.currentTimeMillis();
        }

        //Store these coordinates
        if(createMode){
            storeValuesAsJSON(x, y, 's');
        }

        //Log.d(TAG,"Touch_start : " + x + ","+ y + "  " + mX + "," + mY);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    /**
     * Continue drawing. Function reused for both Create & Open Mode
     * @param x x-coordinate
     * @param y y-coordinate
     */
    private void touch_move(float x, float y) {

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            //Make bezier curve through the points
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
            //Log.d(TAG,"Touch_move : " + x + ","+ y + "  " + mX + "," + mY);

            //Store these coordinates
            if(createMode){
                storeValuesAsJSON(x, y, 'm');
            }
        }
    }

    /**
     * Stop drawing once finger lifted
     */
    private void touch_up() {
        mPath.lineTo(mX, mY);

        //Store coordinates
        if(createMode){
            storeValuesAsJSON(mX, mY, 'e');
        }

        Log.d(TAG,"Touch_end : "+ mX + "," + mY);

        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }


    public void setPaintColor(int color){
        mPaint.setColor(color);
    }

    /**
     * Clear the canvas
     */
    public void clearCanvas(){
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();

        if(createMode){
            //Reset everything
            initFile(getContext());
            startTime = null;
        }
        /*
        //Alternate way to clear canvas
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight(), clearPaint);*/
    }

    public void clearCanvasForNextPage(){
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();

        startTime = null;

    }


    /*public void saveToText() {
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Intermediates between Open Mode & BaseView
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void startDrawing(float x, float y) {
        touch_start(x,y);
        invalidate();
    }

    /**
     * Intermediates between Open Mode & BaseView
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void continueDrawing(float x,float y) {
        touch_move(x,y);
        invalidate();
    }

    /**
     * Intermediates between Open Mode & BaseView
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void stopDrawing(float x,float y) {
        touch_up();
        invalidate();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void saveData() {

        //TODO: Display ProgressBar for saving data. (Start an IntentService maybe?)

        try {
            fileOutputStream = new FileOutputStream(file,true);
            infoFileOutputStream = new FileOutputStream(infoFile, false);
        } catch (FileNotFoundException e) {
            Log.d(TAG,"File not found");
            e.printStackTrace();
        }

        InfoModel infoModel = new InfoModel(getTotalPages());
        String info = gson.toJson(infoModel);
        try {
            infoFileOutputStream.write(info.getBytes());
            infoFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(InputModel model: buffer){
            String data = gson.toJson(model) + "\n";
            try {

                fileOutputStream.write(data.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileOutputStream.close();
            infoFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        buffer.clear();
    }
}
