package ayushkumar.smartroomsop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ayushkumar.smartroomsop.events.ContinueDrawingBackgroundEvent;
import ayushkumar.smartroomsop.events.ContinueDrawingEvent;
import ayushkumar.smartroomsop.events.StartDrawingBackgroundEvent;
import ayushkumar.smartroomsop.events.StartDrawingEvent;
import ayushkumar.smartroomsop.events.StopDrawingBackgroundEvent;
import ayushkumar.smartroomsop.events.StopDrawingEvent;
import ayushkumar.smartroomsop.view.BaseView;
import de.greenrobot.event.EventBus;

/**
 * Created by Ayush on 22-01-15.
 */
public class OpenActivity extends BaseActivity {

    private static final String TAG = "OpenActvity";
    Paint mPaint;
    BaseView baseView;
    File file;
    FileInputStream fileInputStream;
    Long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        baseView = new BaseView(this, mPaint, false);
        setContentView(baseView);
        //setContentView(R.layout.activity_open);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        initFile(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_play:
                Log.d(TAG, "Play");
                playFromTextFile();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open, menu);
        return true;
    }


    private void playFromTextFile() {
        initFile(this);
        /*Log.d(TAG,file.getAbsolutePath());
        Log.d(TAG,file.toString());
        Log.d(TAG,"Length : "+file.length()+"");*/

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            //Log.d(TAG,"In try of playFromTextFile");
            //Log.d(TAG,"Line : " + br.readLine());

            while ((line = br.readLine()) != null) {
                // process the line.
                Log.d(TAG, line + " being processed");
                String type = line.split(":")[2];
                Log.d(TAG, "Type " + type);
                if (type != null) {
                    String parts[] = line.split(":");
                    float x = Float.parseFloat(parts[1].split(",")[0]);
                    float y = Float.parseFloat(parts[1].split(",")[1]);
                    Long time = Long.parseLong(parts[0]);
                    if (type.equals("s")) {
                        EventBus.getDefault().post(new StartDrawingBackgroundEvent(time,y,x));
                        //startDrawing(line);
                    } else if (type.equals("m")) {
                        EventBus.getDefault().post(new ContinueDrawingBackgroundEvent(time,y,x));
                        //continueDrawing(line);
                    } else {
                        EventBus.getDefault().post(new StopDrawingBackgroundEvent(time,y,x));
                        //stopDrawing(line);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* private void stopDrawing(String line) {
        String parts[] = line.split(":");
        float x = Float.parseFloat(parts[1].split(",")[0]);
        float y = Float.parseFloat(parts[1].split(",")[1]);

        baseView.stopDrawing(x, y);
        Log.d(TAG, "Stop drawing at " + x + "," + y);

    }*/

    public void onEventBackgroundThread(StopDrawingBackgroundEvent stopDrawingBackgroundEvent){
        //Sleep
        //Sleep for a specific period
        try {
            Thread.sleep(stopDrawingBackgroundEvent.getTime() - lastTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new StopDrawingEvent(stopDrawingBackgroundEvent));
    }

    public void onEventMainThread(StopDrawingEvent stopDrawingEvent){
        baseView.stopDrawing(stopDrawingEvent.getX(),stopDrawingEvent.getY());
    }

    /*private void continueDrawing(String line) {
        String parts[] = line.split(":");
        float x = Float.parseFloat(parts[1].split(",")[0]);
        float y = Float.parseFloat(parts[1].split(",")[1]);

        Long time = Long.parseLong(parts[0]);
        try {
            Thread.sleep(time - lastTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lastTime = time;
        baseView.continueDrawing(x, y);
        Log.d(TAG, "Continue drawing at " + x + "," + y);

    }*/

    public void onEventBackgroundThread(ContinueDrawingBackgroundEvent continueDrawingBackgroundEvent){

        //Sleep for a specific period
        try {
            Thread.sleep(continueDrawingBackgroundEvent.getTime() - lastTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lastTime = continueDrawingBackgroundEvent.getTime();
        EventBus.getDefault().post(new ContinueDrawingEvent(continueDrawingBackgroundEvent));
    }

    public void onEventMainThread(ContinueDrawingEvent continueDrawingEvent){
        baseView.continueDrawing(continueDrawingEvent.getX(),continueDrawingEvent.getY());
    }

    /*private void startDrawing(String line) {
        String parts[] = line.split(":");
        float x = Float.parseFloat(parts[1].split(",")[0]);
        float y = Float.parseFloat(parts[1].split(",")[1]);
        if (lastTime == null) {
            lastTime = 0L;
        }
        Long time = Long.parseLong(parts[0]);
        try {
            Thread.sleep(time - lastTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lastTime = time;
        baseView.startDrawing(x, y);
        Log.d(TAG, "Start drawing at " + x + "," + y);
    }*/

    public void onEventBackgroundThread(StartDrawingBackgroundEvent startDrawingBackgroundEvent) {
        if (lastTime == null) {
            lastTime = 0L;
        }
        //Pause for certain period of time
        //Sleep for a specific period
        try {
            Thread.sleep(startDrawingBackgroundEvent.getTime() - lastTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lastTime = startDrawingBackgroundEvent.getTime();
        EventBus.getDefault().post(new StartDrawingEvent(startDrawingBackgroundEvent));

    }

    public void onEventMainThread(StartDrawingEvent startDrawingEvent) {
        baseView.startDrawing(startDrawingEvent.getX(), startDrawingEvent.getY());
        Log.d(TAG, "Start drawing at " + startDrawingEvent.getX() + "," + startDrawingEvent.getY());
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
        Log.d(TAG, "CHECK value " + check);

        if (check) {
            file = new File(context.getExternalFilesDir(null)
                    + File.separator + (Constants.filename));
            if (file.exists()) {
                Log.d(TAG, "File exists");
            } else {
                Log.d(TAG, "File doesn't exist");
            }
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found");
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Can't access SD Card.", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Mem card not available?");
        }
    }
}
