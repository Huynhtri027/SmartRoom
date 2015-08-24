package ayushkumar.smartroomsop;

import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import ayushkumar.smartroomsop.events.StartDrawingEvent;
import ayushkumar.smartroomsop.interfaces.AudioRecordListener;
import ayushkumar.smartroomsop.util.BaseActivity;
import ayushkumar.smartroomsop.util.Constants;
import ayushkumar.smartroomsop.view.BaseView;


public class CreateActivity extends BaseActivity implements AudioRecordListener {

    Paint mPaint;
    BaseView baseView;
    int totalPages = 1;
    int currentPage = 1;

    private static final String TAG = "CreateActivity";
    MediaRecorder mRecorder;

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

        baseView = new BaseView(this, mPaint, true);
        baseView.setAudioRecordListener(this);
        setContentView(baseView);

    }

    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_clear:
                baseView.clearCanvas();
                return true;
            case R.id.action_size:
                return true;
            case R.id.action_color:
                baseView.setPaintColor(Color.BLUE);
                return true;
            case R.id.action_nextpage:
                incrementTotalPages();
                incrementCurrentPage();
                baseView.clearCanvasForNextPage();
                return true;
            case R.id.action_prevpage:
                if(currentPage > 1){
                    decrementCurrentPage();
                }
                return true;
            /*case R.id.action_savetotext:
                baseView.saveToText();
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEvent(StartDrawingEvent startDrawingEvent) {
        Toast.makeText(this, "start drawing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem nextPageMenuItem = menu.findItem(R.id.action_nextpage);
        MenuItem prevPageMenuItem = menu.findItem(R.id.action_prevpage);
        /*

        //Enable this if we need previous mode availability in Create mode too.
        //Changes will have to be made in the rest of the code to bring the desired results too.
        if(totalPages == 1){
            prevPageMenuItem.setEnabled(false);
        }else{
            prevPageMenuItem.setEnabled(true);
        }*/
        prevPageMenuItem.setEnabled(false);
        prevPageMenuItem.setVisible(false);

        return true;
    }

    public void incrementTotalPages(){
        totalPages++;
        baseView.setTotalPages(baseView.getTotalPages() + 1);
    }

    public void decrementTotalPages(){
        totalPages--;
        baseView.setTotalPages(baseView.getTotalPages() - 1);
    }

    public void incrementCurrentPage(){
        currentPage++;
        baseView.setCurrentPage(baseView.getCurrentPage() + 1);
    }

    public void decrementCurrentPage(){
        currentPage--;
        baseView.setCurrentPage(baseView.getCurrentPage() - 1);
    }

    @Override
    public void onBackPressed() {
        saveData();
        stopRecording();

        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        saveData();
        stopRecording();
        super.onPause();
    }

    @Override
    public void startRecording() {
        Log.d(TAG, "Received signal to start rec");
        if(mRecorder == null){
            Log.d(TAG, "mRecorder null");
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            String audioFile = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + (Constants.audioFile);
            mRecorder.setOutputFile(audioFile);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(TAG, "prepare() failed");
            }

            mRecorder.start();
        }

    }

    @Override
    public void stopRecording() {
        Log.d(TAG,"Stopping recording if mRecorder is not null");
        if(mRecorder != null){
            Log.d(TAG, "mRecorder not null so stop rec.");
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }


    }

    private void saveData() {
        baseView.saveData();
    }
}
