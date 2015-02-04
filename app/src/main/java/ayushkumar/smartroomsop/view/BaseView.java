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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ayushkumar.smartroomsop.Constants;

/**
 * Created by Ayush on 22-01-15.
 */
public class BaseView extends View {

    private static final String TAG = "BaseView";
    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;
    

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private File file;
    private FileOutputStream fileOutputStream;
    private Long startTime;
    private boolean createMode;


    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* This constructor will be used */
    public BaseView(Context context) {
        super(context);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);


    }

    public BaseView(Context context, Paint mPaint){
        this(context);

        this.mPaint = mPaint;
    }

    public BaseView(Context context, Paint mPaint, boolean createMode){
        this(context,mPaint);
        this.createMode = createMode;
        if(createMode){
            initFile(context);
        }
    }

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
            try {
                fileOutputStream = new FileOutputStream(file);
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
    private static final float TOUCH_TOLERANCE = 4;

    private void storeValues(float x, float y, char type) {
        try {
            fileOutputStream = new FileOutputStream(file,true);
        } catch (FileNotFoundException e) {
            Log.d(TAG,"File not found");
            e.printStackTrace();
        } finally {
        }

        //Subtract startTime from current time to reduce size of data
        String data = (System.currentTimeMillis() - startTime) + ":" + x + "," + y + ":" + type + "\n" ;
        try {
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }


    private void touch_start(float x, float y) {
        /*//Store initial coordinates
        Log.d(TAG, "Time: " + System.currentTimeMillis() + ", Coordinates: (" + x + "," + y + ")" );
        storeValues(x,y);*/

        //Set start time
        if(startTime == null){
            startTime = System.currentTimeMillis();
        }

        if(createMode){
            storeValues(x,y,'s');
        }
        Log.d(TAG,"Touch_start : " + x + ","+ y + "  " + mX + "," + mY);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;


            //Store this coordinate too
           // Log.d(TAG, "Time: " + System.currentTimeMillis() + ", Coordinates: (" + x + "," + y + ")" );
            Log.d(TAG,"Touch_move : " + x + ","+ y + "  " + mX + "," + mY);

            if(createMode){
                storeValues(x,y,'m');
            }
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);

       /* //End storing
        Log.d(TAG, "Time: " + System.currentTimeMillis() + ", Coordinates: (" + mX + "," + mY + ")" );
        storeValues(mX,mY);*/
        if(createMode){
            storeValues(mX,mY,'e');
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

    public void clearCanvas(){
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
        /*Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight(), clearPaint);*/
    }


    public void saveToText() {
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void startDrawing(float x, float y) {
        touch_start(x,y);
        invalidate();
    }

    public void continueDrawing(float x,float y) {
        touch_move(x,y);
        invalidate();
    }

    public void stopDrawing(float x,float y) {
        touch_up();
        invalidate();
    }
}
