package ayushkumar.smartroomsop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ayushkumar.smartroomsop.events.ContinueDrawingBackgroundEvent;
import ayushkumar.smartroomsop.events.ContinueDrawingEvent;
import ayushkumar.smartroomsop.events.StartDrawingBackgroundEvent;
import ayushkumar.smartroomsop.events.StartDrawingEvent;
import ayushkumar.smartroomsop.events.StopDrawingBackgroundEvent;
import ayushkumar.smartroomsop.events.StopDrawingEvent;
import ayushkumar.smartroomsop.events.UploadProjectBackgroundEvent;
import ayushkumar.smartroomsop.events.UploadProjectResultEvent;
import ayushkumar.smartroomsop.interfaces.AudioRecordListener;
import ayushkumar.smartroomsop.model.InfoModel;
import ayushkumar.smartroomsop.model.InputModel;
import ayushkumar.smartroomsop.model.PageEndTimesModel;
import ayushkumar.smartroomsop.model.ProjectInfoModel;
import ayushkumar.smartroomsop.util.BaseActivity;
import ayushkumar.smartroomsop.util.Constants;
import ayushkumar.smartroomsop.view.BaseView;
import de.greenrobot.event.EventBus;

/**
 * Created by Ayush on 22-01-15.
 */
public class OpenActivity extends BaseActivity implements AudioRecordListener, View.OnClickListener{

    private static final String TAG = "OpenActivity";
    Paint mPaint;
    BaseView baseView;
    File file;
    File infoFile;
    FileInputStream fileInputStream;
    FileInputStream infoFileInputStream;
    Long lastTime;
    int currentPage = 1;
    int totalPages = 1;
    Menu menu;
    private Boolean animationPlaying = false;
    MediaPlayer mPlayer;
    OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        baseView = new BaseView(this, mPaint, false);
        ((FrameLayout) findViewById(R.id.open_ll)).addView(baseView, 0);

        (findViewById(R.id.bt_prev)).setOnClickListener(this);
        (findViewById(R.id.bt_play)).setOnClickListener(this);
        (findViewById(R.id.bt_next)).setOnClickListener(this);

        client = new OkHttpClient();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setAnimationPlaying(false);

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        stopPlaying();
        setAnimationPlaying(false);
        super.onPause();
        /*if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_next:
                Log.d(TAG, "Next Page");

                if(getAnimationPlaying()){
                    Toast.makeText(getApplicationContext(), "Please wait till this page ends playing.", Toast.LENGTH_SHORT).show();
                    break;
                }

                if (currentPage == totalPages){
                    Toast.makeText(getApplicationContext(), "This is the last page", Toast.LENGTH_SHORT).show();
                    break;
                }

                currentPage++;
                baseView.clearCanvasForNextPage();
                lastTime = null;
                break;

            case R.id.bt_play:
                Log.d(TAG, "Play");

                if(getAnimationPlaying()){
                    Toast.makeText(getApplicationContext(), "Please wait till this page ends playing.", Toast.LENGTH_SHORT).show();
                    break;
                }

                baseView.clearCanvasForNextPage();
                lastTime = null;
                playFromJsonFile();
                startPlaying();     //Start playing audio
                break;

            case R.id.bt_prev:
                Log.d(TAG, "Prev Page");

                if(getAnimationPlaying()){
                    Toast.makeText(getApplicationContext(), "Please wait till this page ends playing.", Toast.LENGTH_SHORT).show();
                    break;
                }

                if(currentPage == 1){
                    Toast.makeText(getApplicationContext(), "This is the first page", Toast.LENGTH_SHORT).show();
                    break;
                }

                baseView.clearCanvasForNextPage();
                currentPage--;
                lastTime = null;
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            /*case R.id.action_play:
                Log.d(TAG, "Play");
                baseView.clearCanvasForNextPage();
                lastTime = null;
                playFromJsonFile();
                startPlaying();     //Start playing audio
                return true;

            case R.id.action_nextpage:
                Log.d(TAG, "Next Page");
                currentPage++;
                baseView.clearCanvasForNextPage();
                lastTime = null;
                return true;

            case R.id.action_prevpage:
                Log.d(TAG, "Prev Page");
                baseView.clearCanvasForNextPage();
                currentPage--;
                lastTime = null;
                return true;*/
            case R.id.action_info:
                Intent projectInfoIntent = new Intent(this, ProjectInfoActivity.class);
                startActivity(projectInfoIntent);
                break;

            case R.id.action_upload:
                Log.d(TAG, "Upload clicked");
                initFile(this);
                Gson gson = new Gson();

                String title = getIntent().getStringExtra("fileName");
                String description = "";

                try {
                    BufferedReader br = new BufferedReader(new FileReader(infoFile));
                    br.readLine();
                    br.readLine();
                    String line = br.readLine();
                    ProjectInfoModel projectInfoModel = gson.fromJson(line, ProjectInfoModel.class);

                    title = projectInfoModel.getName();
                    description = projectInfoModel.getDescription();

                }catch(FileNotFoundException f){
                    Log.e(TAG, f.getMessage());
                }catch (IOException e){
                    Log.e(TAG, e.getMessage());
                }

                File file = new File(getIntent().getStringExtra("filePath"));
                EventBus.getDefault().post(new UploadProjectBackgroundEvent(title, description, file));


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open, menu);
        this.menu = menu;
        return true;
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem nextPageMenuItem = menu.findItem(R.id.action_nextpage);
        MenuItem prevPageMenuItem = menu.findItem(R.id.action_prevpage);
        MenuItem playMenuItem = menu.findItem(R.id.action_play);

        if(getAnimationPlaying()){
            nextPageMenuItem.setEnabled(false);
            prevPageMenuItem.setEnabled(false);
            playMenuItem.setEnabled(false);
            return true;
        }else{
            //nextPageMenuItem.setEnabled(true);
            playMenuItem.setEnabled(true);
            if((currentPage < totalPages)){
                nextPageMenuItem.setEnabled(true);
            }else if(currentPage == totalPages){
                nextPageMenuItem.setEnabled(false);
            }
            if(currentPage == 1){
                prevPageMenuItem.setEnabled(false);
            }else{
                prevPageMenuItem.setEnabled(true);
            }
        }
        return true;
    }*/

    private void playFromJsonFile(){
        initFile(this);
        Gson gson = new Gson();
        try {
            BufferedReader br = new BufferedReader(new FileReader(infoFile));
            String line = br.readLine();
            InfoModel infoModel = gson.fromJson(line,InfoModel.class);
            totalPages = infoModel.getTotalPages();

            line = br.readLine();
            PageEndTimesModel pageEndTimesModel = gson.fromJson(line, PageEndTimesModel.class);
            baseView.setEndTimesForPages(pageEndTimesModel.getEndTimesForPage());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException i) {
            i.printStackTrace();
        }
        try {
            Log.d(TAG, "Current page : " + currentPage + ", Total pages : " + totalPages);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while(((line = bufferedReader.readLine()) != null)){
                InputModel inputModel = gson.fromJson(line,InputModel.class);
                if(inputModel.getPageNumber() == currentPage){
                    switch (inputModel.getType()){
                        case 's':
                            Log.d(TAG, "Posting start background event.");
                            EventBus.getDefault().post(new StartDrawingBackgroundEvent(inputModel.getTime(), inputModel.getY(), inputModel.getX()));
                            break;
                        case 'm':
                            //Log.d(TAG, "Posting middle background event.");
                            EventBus.getDefault().post(new ContinueDrawingBackgroundEvent(inputModel.getTime(), inputModel.getY(), inputModel.getX()));
                            break;
                        case 'e':
                            Log.d(TAG, "Posting end background event.");
                            EventBus.getDefault().post(new StopDrawingBackgroundEvent(inputModel.getTime(), inputModel.getY(), inputModel.getX()));
                            break;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEventBackgroundThread(StopDrawingBackgroundEvent stopDrawingBackgroundEvent) {
        //Sleep for a specific period
        try {
            Thread.sleep(stopDrawingBackgroundEvent.getTime() - lastTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new StopDrawingEvent(stopDrawingBackgroundEvent));
    }

    public void onEventMainThread(StopDrawingEvent stopDrawingEvent) {
        setAnimationPlaying(false);
        baseView.stopDrawing(stopDrawingEvent.getX(), stopDrawingEvent.getY());
    }

    public void onEventBackgroundThread(ContinueDrawingBackgroundEvent continueDrawingBackgroundEvent) {
        //Sleep for a specific period
        try {
            Thread.sleep(continueDrawingBackgroundEvent.getTime() - lastTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lastTime = continueDrawingBackgroundEvent.getTime();
        EventBus.getDefault().post(new ContinueDrawingEvent(continueDrawingBackgroundEvent));
    }

    public void onEventMainThread(ContinueDrawingEvent continueDrawingEvent) {
        setAnimationPlaying(true);
        baseView.continueDrawing(continueDrawingEvent.getX(), continueDrawingEvent.getY());
    }

    public void onEventBackgroundThread(StartDrawingBackgroundEvent startDrawingBackgroundEvent) {
        if (lastTime == null) {
            lastTime = 0L;

        }
        //Sleep for a specific period
        try {
            Thread.sleep(startDrawingBackgroundEvent.getTime() - lastTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lastTime = startDrawingBackgroundEvent.getTime();
        Log.d(TAG, "Posting start drawing event from BackgroundThread");
        EventBus.getDefault().post(new StartDrawingEvent(startDrawingBackgroundEvent));

    }

    public void onEventMainThread(StartDrawingEvent startDrawingEvent) {
        setAnimationPlaying(true);
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
        if (check) {
            file = new File(context.getExternalFilesDir(null)
                    + File.separator + (Constants.filename));
            infoFile = new File(context.getExternalFilesDir(null) + File.separator + (Constants.infofile));
            if (file.exists() && infoFile.exists()) {
                Log.d(TAG, "Files exist");
            } else {
                Log.d(TAG, "One or more Files don't exist");
            }
            try {
                fileInputStream = new FileInputStream(file);
                infoFileInputStream = new FileInputStream(infoFile);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found");
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Can't access SD Card.", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Mem card not available?");
        }
    }

    public Boolean getAnimationPlaying() {
        return animationPlaying;
    }

    public void setAnimationPlaying(Boolean animationPlaying) {
        this.animationPlaying = animationPlaying;
    }

    @Override
    public void startRecording() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Recording audio not supported in Open Mode");
    }

    @Override
    public void stopRecording() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Recording audio not supported in Open Mode");
    }

    @Override
    public void startPlaying() throws UnsupportedOperationException {

        // If audio is already playing, then stop.
        if(mPlayer != null && mPlayer.isPlaying()){
            mPlayer.stop();
            mPlayer = null;
        }

        mPlayer = new MediaPlayer();
        String audioFile = getApplicationContext().getExternalFilesDir(null) + File.separator + (Constants.audioFile);
        try {
            mPlayer.setDataSource(audioFile);
            mPlayer.prepare();
            if(currentPage != 1){
                int seek = (int)(long)baseView.getEndTimesForPages().get(currentPage - 1);
                Log.d(TAG, "Seeking to " + seek);
                mPlayer.seekTo(seek);
            }
            Log.d(TAG, "Starting from : " + mPlayer.getCurrentPosition()+ ", Total duration: " + mPlayer.getDuration());
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        Long endTimeForPage = baseView.getEndTimesForPages().get(currentPage);
        Long countDownTo = endTimeForPage - mPlayer.getCurrentPosition();
        Log.d(TAG, "Start countdown to " + countDownTo);

        new CountDownTimer(countDownTo, 300){
            @Override
            public void onTick(long millisUntilFinished) {
                // Log.d(TAG, "Current page : " +  currentPage + ", Tick millisUntilFinished" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Current page : " +  currentPage + ", onFinish. Pausing audio.");
                if(mPlayer != null){
                    mPlayer.pause();
                }
            }
        }.start();
    }

    @Override
    public void stopPlaying() throws UnsupportedOperationException {
        if(mPlayer != null){
            mPlayer.release();
            mPlayer = null;
        }
    }

    public Boolean uploadFile(String serverURL, String title, String description, File file) {
        try {

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart(Constants.file, file.getName(),
                            RequestBody.create(MediaType.parse("application/smartroom"), file))
                    .addFormDataPart(Constants.title, title)
                    .addFormDataPart(Constants.description, description)
                    .build();

            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    // Handle the error
                    Log.d(TAG, "OkHTTPError : " + e.getMessage());
                    EventBus.getDefault().post(new UploadProjectResultEvent("Upload unsuccessful :("));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        // Handle the error
                        Log.d(TAG, "OkHTTPError : Response unsuccessful");
                        EventBus.getDefault().post(new UploadProjectResultEvent("Upload unsuccessful :("));
                        return;
                    }
                    // Upload successful
                    Log.d(TAG, "Upload successful");
                    EventBus.getDefault().post(new UploadProjectResultEvent("Upload successful :)"));
                }
            });

            return true;
        } catch (Exception ex) {
            // Handle the error
        }
        return false;
    }

    public void onEventBackgroundThread(UploadProjectBackgroundEvent uploadEvent){
        if(uploadFile(Constants.upload_url, uploadEvent.getTitle(), uploadEvent.getDescription(), uploadEvent.getFile())){
            Log.d(TAG, "Added to OkHttp queue");
        }else{
            Log.d(TAG, "Couldn't add to OkHttp queue");
        }
    }

    public void onEventMainThread(UploadProjectResultEvent uploadProjectResultEvent){
        Toast.makeText(getApplicationContext(), uploadProjectResultEvent.getResult(), Toast.LENGTH_SHORT).show();
    }

}
