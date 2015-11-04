package ayushkumar.smartroomsop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ayushkumar.smartroomsop.events.ExportProjectBackgroundEvent;
import ayushkumar.smartroomsop.events.ExportProjectResultEvent;
import ayushkumar.smartroomsop.events.StartDrawingEvent;
import ayushkumar.smartroomsop.interfaces.AudioRecordListener;
import ayushkumar.smartroomsop.util.BaseActivity;
import ayushkumar.smartroomsop.util.Constants;
import ayushkumar.smartroomsop.util.Util;
import ayushkumar.smartroomsop.view.BaseView;
import de.greenrobot.event.EventBus;


public class CreateActivity extends BaseActivity implements AudioRecordListener, View.OnClickListener {

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
        setContentView(R.layout.activity_create);

        baseView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((LinearLayout)findViewById(R.id.baseview_ll)).addView(baseView, 0);

        /*(findViewById(R.id.bt_prev)).setOnClickListener(this);
        (findViewById(R.id.bt_play)).setOnClickListener(this);*/
        (findViewById(R.id.bt_next)).setOnClickListener(this);

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_next:
                baseView.saveEndTimeForCurrentPage();
                incrementTotalPages();
                incrementCurrentPage();
                baseView.clearCanvasForNextPage();
                break;

            /*case R.id.bt_play:
                baseView.saveEndTimeForCurrentPage();
                incrementTotalPages();
                incrementCurrentPage();
                baseView.clearCanvasForNextPage();
                break;

            case R.id.bt_prev:
                break;*/
        }
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
           /* case R.id.action_size:
                return true;
            case R.id.action_color:
                baseView.setPaintColor(Color.BLUE);
                return true;*/
            case R.id.action_nextpage:
                baseView.saveEndTimeForCurrentPage();
                incrementTotalPages();
                incrementCurrentPage();
                baseView.clearCanvasForNextPage();
                return true;
            case R.id.action_prevpage:
                if(currentPage > 1){
                    decrementCurrentPage();
                }
                return true;
            case R.id.action_export:
                baseView.saveEndTimeForCurrentPage();
                stopRecording();
                saveData();
                return true;

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

    public void decrementTotalPages() {
        totalPages--;
        baseView.setTotalPages(baseView.getTotalPages() - 1);
    }

    public void incrementCurrentPage(){
        currentPage++;
        baseView.setCurrentPage(baseView.getCurrentPage() + 1);
    }

    public void decrementCurrentPage() {
        currentPage--;
        baseView.setCurrentPage(baseView.getCurrentPage() - 1);
    }

    @Override
    public void onBackPressed() {
        baseView.saveEndTimeForCurrentPage();
        stopRecording();
        saveData();

        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        baseView.saveEndTimeForCurrentPage();
        stopRecording();
        saveData();
        super.onPause();
    }

    @Override
    public void startRecording() {
        Log.d(TAG, "Received signal to start rec");

        if(mRecorder == null){
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            String audioFile = getApplicationContext().getExternalFilesDir(null) + File.separator + Constants.audioFile;
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

        if(mRecorder != null){
            Log.d(TAG, "mRecorder not null so stop rec.");
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public void startPlaying() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Playing audio not supported in Create Mode");
    }

    @Override
    public void stopPlaying() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Playing audio not supported in Create Mode");
    }

    private void saveData() {

        String projectName = getIntent().getStringExtra("name");
        String projectDescription = getIntent().getStringExtra("description");

        baseView.saveData(projectName, projectDescription);

        ExportProjectBackgroundEvent event = new ExportProjectBackgroundEvent(projectName);
        EventBus.getDefault().post(event);
    }

    public void onEventBackgroundThread(ExportProjectBackgroundEvent exportProjectEvent) {

        String projectName = Util.convertStringWithSpacesToOneString(exportProjectEvent.getName());

        Context c = getApplicationContext();

        // baseString takes individual files(info, data, audio etc) from app's private data directory

        String base = c.getExternalFilesDir(null) + File.separator;
        String audioFileString = base + Constants.audioFile;
        String dataFileString = base + Constants.filename;
        String infoFileString = base + Constants.infofile;

        File audioFile = new File(audioFileString);
        File dataFile = new File(dataFileString);
        File infoFile = new File(infoFileString);

        Log.d(TAG, " " + audioFile.getAbsolutePath() + ", Exists:" + audioFile.exists() );
        Log.d(TAG, " " + dataFile.getAbsolutePath() + ", Exists:" + dataFile.exists());
        Log.d(TAG, " " + infoFile.getAbsolutePath() + ", Exists:" + infoFile.exists());

        ArrayList<File> filesToBeZipped = new ArrayList<>();
        filesToBeZipped.add(audioFile);
        filesToBeZipped.add(dataFile);
        filesToBeZipped.add(infoFile);


        String baseExportDirString = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Constants.app_directory + File.separator + projectName + File.separator;
        Log.d(TAG, "baseExportDirString : " + baseExportDirString);

        try {

            File baseExportDir = new File(baseExportDirString);
            if(baseExportDir.mkdirs()){
                Log.d(TAG, "Make directories returned true");
            }else{
                Log.d(TAG, "Make directories returned false");
            }

            if(baseExportDir.isDirectory()){
                Log.d(TAG, baseExportDirString + " is a directory");
            }else{
                Log.d(TAG, baseExportDirString + " is not a directory");
            }

            String exportedFileName = projectName + Constants.extension;

            File zipFileDel = new File(baseExportDirString + exportedFileName);
            if(zipFileDel.exists()){
                zipFileDel.delete();
            }

            ZipFile zipFile = new ZipFile(baseExportDirString + exportedFileName);
            ZipParameters parameters = new ZipParameters();

            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            // Set the compression level. This value has to be in between 0 to 9
            // Set the compression level. This value has to be in between 0 to 9
            // Several predefined compression levels are available
            // DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of compression
            // DEFLATE_LEVEL_FAST - Low compression level but higher speed of compression
            // DEFLATE_LEVEL_NORMAL - Optimal balance between compression level/speed
            // DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
            // DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            // Set the encryption flag to true
            // If this is set to false, then the rest of encryption properties are ignored
            parameters.setEncryptFiles(true);

            // Set the encryption method to Standard Zip Encryption
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);

            /* //Uncomment this block for AES encryption
            // Set the encryption method to AES Zip Encryption
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

            // Set AES Key strength. Key strengths available for AES encryption are:
            // AES_STRENGTH_128 - For both encryption and decryption
            // AES_STRENGTH_192 - For decryption only
            // AES_STRENGTH_256 - For both encryption and decryption
            // Key strength 192 cannot be used for encryption. But if a zip file already has a
            // file encrypted with key strength of 192, then Zip4j can decrypt this file
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            */

            parameters.setPassword("password");

            zipFile.createZipFile(filesToBeZipped, parameters);

            File fileExistCheck = new File(baseExportDirString + exportedFileName);
            if(fileExistCheck.exists()){
                EventBus.getDefault().post(new ExportProjectResultEvent(true));
            }else{
                EventBus.getDefault().post(new ExportProjectResultEvent(false));
            }
        } catch (ZipException e) {
            Log.e(TAG, "Error: " + e.toString());
        }
    }


    public void onEventMainThread(ExportProjectResultEvent projectResultEvent){
        if(projectResultEvent.isFileExists()){
            Toast.makeText(getApplicationContext(), "Project Export Successful :)", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "Project Export Failed :(", Toast.LENGTH_SHORT).show();
        }
    }
}
