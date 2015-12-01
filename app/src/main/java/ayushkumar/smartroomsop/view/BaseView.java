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
import java.util.HashMap;

import ayushkumar.smartroomsop.interfaces.AudioRecordListener;
import ayushkumar.smartroomsop.model.InfoModel;
import ayushkumar.smartroomsop.model.InputModel;
import ayushkumar.smartroomsop.model.PageEndTimesModel;
import ayushkumar.smartroomsop.model.ProjectInfoModel;
import ayushkumar.smartroomsop.util.Constants;

/**
 * Created by Ayush Kumar on 22-01-15.
 *
 * @author Ayush Kumar
 *
 * The view (canvas) handling all the drawings by the user
 *
 * The Canvas class holds the "draw" calls.
 * To draw something, you need 4 basic components: A Bitmap to hold the pixels,
 * a Canvas to host the draw calls (writing into the bitmap),
 * a drawing primitive (e.g. Rect, Path, text, Bitmap),
 * and a paint (to describe the colors and styles for the drawing).
 *
 */
public class BaseView extends View {

    /*
     * Tag to be used for logging in Android Monitor (Logcat)
     */
    private static final String TAG = "BaseView";

    /*
     * Bitmap to hold the pixels
     */
    private Bitmap mBitmap;

    /*
     * Canvas class holds the "draw" calls.
     */
    private Canvas mCanvas;

    /*
     * Path to draw
     */
    private Path mPath;

    /*
     * Paint (to describe the colors and styles for the drawing) for Bitmap
     */
    private Paint mBitmapPaint;

    /*
     * Paint (to describe the colors and styles for the drawing) for Path
     */
    private Paint mPaint;

    /*
     * File to store all InputModels in. This corresponds to all touches in the Project
     */
    private File file;

    /*
     * File to store information of the Project
     */
    private File infoFile;

    /*
     * Output Streams for the files
     */
    private FileOutputStream fileOutputStream;
    private FileOutputStream infoFileOutputStream;

    /*
     * Start time for a page, to be used to reduce the size of touch information in the file
     * All the times in the file are offset by this time, thus reducing the size
     */
    private Long startTime;
    private Long startTimeForAudio;

    /*
     * Boolean to check if startTime for Audio has already been set or not
     */
    private boolean startTimeForAudioSet = false;

    /*
     * If we're working in Create Mode or Open Mode
     */
    private boolean createMode;

    /*
     * Count of total pages in the Project
     * Initialized to 1 initially
     * Will be incremented as user adds new pages
     */
    private int totalPages = 1;

    /*
     * Current active page in the Project
     */
    private int currentPage = 1;

    /*
     * Buffer to store all touch information.
     * Upon pause or stop, this buffer has to be used to save all the touch information to the appropriate file
     */
    private ArrayList<InputModel> buffer;

    /*
     * Mapping of Pages to audio end times
     */
    private HashMap<Integer, Long> endTimesForPages;

    /*
     * GSON library instance for serialisation & de-serialisation
     */
    private Gson gson;

    /*
     * Interface to manage audio interactions
     */
    private AudioRecordListener audioRecordListener;

    /*
     * Hold coordinates of previous touch
     */
    private float mX, mY;

    /*
     * Tolerance value for touch events (in pixels)
     */
    private static final float TOUCH_TOLERANCE = 4;


    /**
     * Constructor
     * @param context Context
     * @param attrs Attribute set
     */
    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor
     * @param context Context
     */
    public BaseView(Context context) {
        super(context);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        buffer = new ArrayList<>();
        endTimesForPages = new HashMap<>();
        gson = new Gson();
    }

    /**
     * Constructor
     * @param context Context
     * @param mPaint Paint object for path
     */
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
     * @param context Context
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

        /*
         * If you need to create a new Canvas,
         * then you must define the Bitmap upon which drawing will actually be performed.
         * The Bitmap is always required for a Canvas.
         */

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        /*
         * The initial background color of the canvas
         */
        canvas.drawColor(0xFFAAAAAA);

        /*
         * Carry bitmap to this canvas
         */
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        /*
         * Draw path on the canvas
         */
        canvas.drawPath(mPath, mPaint);
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

        //Set start time if not already set
        //TODO Find better way to approach this(As this has to be taken care of in other functions manually, eg clearCanvas)
        if(startTime == null){
            startTime = System.currentTimeMillis();
            if(!startTimeForAudioSet){
                startTimeForAudio = startTime;
                startTimeForAudioSet = true;
            }


            //Start recording audio
            //Send this signal to the activity
            if(createMode){
                audioRecordListener.startRecording();
            }
            Log.d(TAG, "Sending signal to start recording audio");

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

        Log.d(TAG, "Touch_end : " + mX + "," + mY);

        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        /*
         * Get coordinates of touch
         */
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*
                 * When finger is put down on the touch screen
                 * Start the recording of touches for this path
                 */
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                /*
                 * When finger is moved on the touch screen
                 * Continue the recording of touches
                 */
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                /*
                 * When finger is lifted from the touch screen
                 * Stop the recording of touches for this path
                 */
                touch_up();
                invalidate();
                break;
        }
        return true;
    }


    /**
     * Set color of Paint object of Path
     * @param color Color
     */
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

    /**
     * Clear canvas (When user clicks on Next Page)
     */
    public void clearCanvasForNextPage(){
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();

        startTime = null;

    }

    /**
     * Intermediates between Open Mode & BaseView
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void startDrawing(float x, float y) {
        touch_start(x, y);
        invalidate();
    }

    /**
     * Intermediates between Open Mode & BaseView
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void continueDrawing(float x,float y) {
        touch_move(x, y);
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

    /**
     * Save data from buffer to file
     * The file is written in JSON blocks
     * Each line of the info file has JSON serialized information of the Project
     * Each line of the data file has JSON serialized values of the touch's information (coordinates & time delay)
     * @param projectName Name of the project
     * @param projectDescription Description of project
     * @param author Author of project
     */
    public void saveData(String projectName, String projectDescription, String author) {

        //TODO: Display ProgressBar for saving data. (Start an IntentService maybe?)

        try {
            fileOutputStream = new FileOutputStream(file,true);
            infoFileOutputStream = new FileOutputStream(infoFile, false);
        } catch (FileNotFoundException e) {
            Log.d(TAG,"File not found");
            e.printStackTrace();
        }

        ////// INFO FILE START //////
        InfoModel infoModel = new InfoModel(getTotalPages());
        String info = gson.toJson(infoModel) + "\n";
        try {
            infoFileOutputStream.write(info.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        PageEndTimesModel pageEndTimesModel = new PageEndTimesModel(endTimesForPages);
        String endTimes = gson.toJson(pageEndTimesModel) + "\n";
        try {
            infoFileOutputStream.write(endTimes.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProjectInfoModel projectInfoModel = new ProjectInfoModel(projectName, projectDescription, author);
        String projectInfo = gson.toJson(projectInfoModel) + "\n";
        try {
            infoFileOutputStream.write(projectInfo.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ////// INFO FILE END //////

        ////// DATA FILE START //////
        for(InputModel model: buffer){
            String data = gson.toJson(model) + "\n";
            try {

                fileOutputStream.write(data.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ////// DATA FILE END ///////


        // Close streams
        try {
            fileOutputStream.close();
            infoFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Clear the buffer as all data written
        buffer.clear();
    }


    // Getters & Setters

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

    public HashMap<Integer, Long> getEndTimesForPages() {
        return endTimesForPages;
    }

    public void setEndTimesForPages(HashMap<Integer, Long> endTimesForPages) {
        this.endTimesForPages = endTimesForPages;
    }

    public void saveEndTimeForCurrentPage() {
        endTimesForPages.put(currentPage, System.currentTimeMillis() - startTimeForAudio);
    }

    public AudioRecordListener getAudioRecordListener() {
        return audioRecordListener;
    }

    public void setAudioRecordListener(AudioRecordListener audioRecordListener) {
        this.audioRecordListener = audioRecordListener;
    }
}
