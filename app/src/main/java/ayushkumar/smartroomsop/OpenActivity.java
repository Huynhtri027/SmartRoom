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
 * Created by Ayush Kumar on 22-01-15.
 *
 * @author Ayush Kumar
 *
 * Activity that allows a user to open an existing Project
 */
public class OpenActivity extends BaseActivity implements AudioRecordListener, View.OnClickListener{

    /*
     * Tag to be used for logging in Android Monitor (LogCat)
     */
    private static final String TAG = "OpenActivity";

    /*
     * Paint object to be used for BaseView's path (Path drawn by the user)
     */
    Paint mPaint;

    /*
     * Instance of BaseView (View to be used for drawing by the user)
     */
    BaseView baseView;

    /*
     * File to store all InputModels in. This corresponds to all touches in the Project
     */
    File file;

    /*
     * File to store information of the Project
     */
    File infoFile;

    /*
     * Output Streams for the files
     */
    FileInputStream fileInputStream;
    FileInputStream infoFileInputStream;

    /*
     * Store last time so that continuation of drawing events is possible
     * Threads can be scheduled to be run in order by using this information
     */
    Long lastTime;

    /*
     * Count of total pages in the Project
     * Initialized to 1 initially
     * Will be incremented as user adds new pages
     */
    int totalPages = 1;

    /*
     * Current active page in the Project
     */
    int currentPage = 1;

    /*
     * Menu instance
     */
    Menu menu;

    /*
     * If any animation is playing or not
     * TODO: Delete this variable, as now audio is being used to figure out if the animation is playing or not, which makes more sense
     */
    private Boolean animationPlaying = false;

    /*
     * MediaPlayer instance for playing Audio
     */
    MediaPlayer mPlayer;

    /*
     * OkHttpClient library for networking
     */
    OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        /*
         * Create Paint object to be passed to BaseView to decide attributes of Path object of drawing
         */
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        /*
         * Initialize the BaseView view object
         */
        baseView = new BaseView(this, mPaint, false);

        /*
         * Add baseView(canvas) to the view hierarchy of this activity
         */
        ((FrameLayout) findViewById(R.id.open_ll)).addView(baseView, 0);

        /*
         * Set click listeners to buttons
         */
        (findViewById(R.id.bt_prev)).setOnClickListener(this);
        (findViewById(R.id.bt_play)).setOnClickListener(this);
        (findViewById(R.id.bt_next)).setOnClickListener(this);

        /*
         * Initialize OkHttpClient library
         */
        client = new OkHttpClient();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        /*
         * Set the animation playing to false initially
         */
        setAnimationPlaying(false);

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        /*
         * Stop playing if activity is paused
         */
        stopPlaying();
        setAnimationPlaying(false);

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_next:

                /*
                 * Click on Next Page button
                 * Switch to next page if next page exists
                 */
                Log.d(TAG, "Next Page");

                /*
                 * Can't switch pages while current animation going on.
                 * Not possible with the current implementation. Show this message.
                 */
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
                /*
                 * Play this page/slide's animation
                 */
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
                /*
                 * Go to the previous page
                 */
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

            case R.id.action_info:
                /*
                 * Show information of the Project
                 */
                Intent projectInfoIntent = new Intent(this, ProjectInfoActivity.class);
                startActivity(projectInfoIntent);
                break;

            case R.id.action_upload:
                /*
                 * Upload this Project to the webserver
                 */
                Log.d(TAG, "Upload clicked");

                initFile(this);
                Gson gson = new Gson();

                String title = getIntent().getStringExtra("fileName");
                String description = "";
                String author = "";

                /*
                 * Read the infoFile to get the information of the Project
                 */
                try {
                    BufferedReader br = new BufferedReader(new FileReader(infoFile));

                    //Skip unnecessary lines
                    br.readLine();
                    br.readLine();
                    String line = br.readLine();
                    ProjectInfoModel projectInfoModel = gson.fromJson(line, ProjectInfoModel.class);

                    title = projectInfoModel.getName();
                    description = projectInfoModel.getDescription();
                    author = projectInfoModel.getAuthor();

                }catch(FileNotFoundException f){
                    Log.e(TAG, f.getMessage());
                }catch (IOException e){
                    Log.e(TAG, e.getMessage());
                }

                /*
                 * Get file to be uploaded
                 */
                File file = new File(getIntent().getStringExtra("filePath"));

                /*
                 * Start the upload in the background
                 */
                EventBus.getDefault().post(new UploadProjectBackgroundEvent(title, description, author, file));


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

    /**
     * Play recorded drawings of the user into the canvas (BaseView instance)
     * The recorded drawings of the user are stored in JSON format, so GSON is used for parsing them
     * To play the drawing, threads are scheduled to join paths in the canvas in such a way that they correspond to the user's movements.
     * The user's touches had been recorded in the data file, along with their times.
     * This data file is parsed line by line, and threads are scheduled accordingly.
     * The threads just recreate the motion by joining the consecutive coordinates with bezier curves
     * These threads now run at scheduled intervals, creating the perception that the paths are being recreated.
     */
    private void playFromJsonFile(){
        /*
         * Initialize files and GSON instance
         */
        initFile(this);
        Gson gson = new Gson();

        /*
         * Parse the file to play the drawing
         */
        try {
            BufferedReader br = new BufferedReader(new FileReader(infoFile));

            /*
             * Get total number of pages
             */
            String line = br.readLine();
            InfoModel infoModel = gson.fromJson(line,InfoModel.class);
            totalPages = infoModel.getTotalPages();

            /*
             * Get end times of audio of pages
             */
            line = br.readLine();
            PageEndTimesModel pageEndTimesModel = gson.fromJson(line, PageEndTimesModel.class);
            baseView.setEndTimesForPages(pageEndTimesModel.getEndTimesForPage());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException i) {
            i.printStackTrace();
        }
        try {

            /*
             * Start parsing the data file
             */
            Log.d(TAG, "Current page : " + currentPage + ", Total pages : " + totalPages);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while(((line = bufferedReader.readLine()) != null)){
                InputModel inputModel = gson.fromJson(line,InputModel.class);

                /*
                 * If input model's page number corresponds to current page, then we have to start the threads
                 */
                if(inputModel.getPageNumber() == currentPage){
                    switch (inputModel.getType()){
                        case 's':
                            /*
                             * Start the drawing for this path
                             */
                            Log.d(TAG, "Posting start background event.");
                            EventBus.getDefault().post(new StartDrawingBackgroundEvent(inputModel.getTime(), inputModel.getY(), inputModel.getX()));
                            break;
                        case 'm':
                            /*
                             * Continue the drawing for this path
                             */
                            //Log.d(TAG, "Posting middle background event.");
                            EventBus.getDefault().post(new ContinueDrawingBackgroundEvent(inputModel.getTime(), inputModel.getY(), inputModel.getX()));
                            break;
                        case 'e':
                        	/*
							 * End the drawing for this path
                        	 */
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

    /**
     * Stop drawing the path in a background thread
     * @param stopDrawingBackgroundEvent Event containing information about the draw
     */
    public void onEventBackgroundThread(StopDrawingBackgroundEvent stopDrawingBackgroundEvent) {
        //Sleep for a specific period
        try {
            Thread.sleep(stopDrawingBackgroundEvent.getTime() - lastTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new StopDrawingEvent(stopDrawingBackgroundEvent));
    }

    /**
     * Stop drawing the path on the Main Thread
     * @param stopDrawingEvent Event containing information about the draw
     */
    public void onEventMainThread(StopDrawingEvent stopDrawingEvent) {
        setAnimationPlaying(false);
        baseView.stopDrawing(stopDrawingEvent.getX(), stopDrawingEvent.getY());
    }

    /**
     * Continue drawing the path in a background Thread
     * @param continueDrawingBackgroundEvent Event containing information about the draw
     */
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

    /**
     * Continue drawing the path on the Main Thread
     * @param continueDrawingEvent Event containing information about the draw
     */
    public void onEventMainThread(ContinueDrawingEvent continueDrawingEvent) {
        setAnimationPlaying(true);
        baseView.continueDrawing(continueDrawingEvent.getX(), continueDrawingEvent.getY());
    }

    /**
     * Start drawing the path in the background thread
     * @param startDrawingBackgroundEvent Event containing information about the draw
     */
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

    /**
     * Start drawing the path on the Main Thread
     * @param startDrawingEvent Event containing information about the draw
     */
    public void onEventMainThread(StartDrawingEvent startDrawingEvent) {
//        setAnimationPlaying(true);
        baseView.startDrawing(startDrawingEvent.getX(), startDrawingEvent.getY());
        Log.d(TAG, "Start drawing at " + startDrawingEvent.getX() + "," + startDrawingEvent.getY());
    }

    /**
     * Initialize Info File (Information about Project) & Data File (Information about touches)
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

    /**
     * Check if page drawing animation is playing or not.
     * To check this, check if audio is playing or not, because the audio is synced with the page play
     * If audio is playing, that means that the current page is still playing
     * @return Page playing or not
     */
    public Boolean getAnimationPlaying() {
//        return animationPlaying;
        return (mPlayer!= null && mPlayer.isPlaying());
    }

    /**
     * Set page drawing animation playing or not
     * @param animationPlaying page drawing animation or not
     */
    public void setAnimationPlaying(Boolean animationPlaying) {
        this.animationPlaying = animationPlaying;
    }

    /**
     * Can't record in Open mode
     * @throws UnsupportedOperationException
     */
    @Override
    public void startRecording() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Recording audio not supported in Open Mode");
    }

    /**
     * Can't record in Open mode
     * @throws UnsupportedOperationException
     */
    @Override
    public void stopRecording() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Recording audio not supported in Open Mode");
    }

    /**
     * Start playing the audio
     * @throws UnsupportedOperationException
     */
    @Override
    public void startPlaying() throws UnsupportedOperationException {

        /*
         * If audio is already playing, then stop.
         */
        if(mPlayer != null && mPlayer.isPlaying()){
            mPlayer.stop();
            mPlayer = null;
        }

        /*
         * Initialize the Media Player
         */
        mPlayer = new MediaPlayer();
        String audioFile = getApplicationContext().getExternalFilesDir(null) + File.separator + (Constants.audioFile);
        try {
            mPlayer.setDataSource(audioFile);
            mPlayer.prepare();

            /*
             * Seek to the appropriate location depending on the page
             */
            if(currentPage != 1){
                int seek = (int)(long)baseView.getEndTimesForPages().get(currentPage - 1);
                Log.d(TAG, "Seeking to " + seek);
                mPlayer.seekTo(seek);
            }
            Log.d(TAG, "Starting from : " + mPlayer.getCurrentPosition()+ ", Total duration: " + mPlayer.getDuration());

            /*
             * Start playing the audio
             */
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        /*
         * Figure out when to stop the audio
         */
        Long endTimeForPage = baseView.getEndTimesForPages().get(currentPage);
        Long countDownTo = endTimeForPage - mPlayer.getCurrentPosition();
        Log.d(TAG, "Start countdown to " + countDownTo);

        /*
         * Start the countdown timer
         */
        new CountDownTimer(countDownTo, 300){
            @Override
            public void onTick(long millisUntilFinished) {
                // Log.d(TAG, "Current page : " +  currentPage + ", Tick millisUntilFinished" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                /*
                 * Pause the audio when the page's time has elapsed
                 */
                Log.d(TAG, "Current page : " +  currentPage + ", onFinish. Pausing audio.");
                if(mPlayer != null){
                    mPlayer.pause();
                }
            }
        }.start();
    }

    /**
     * Stop playing the audio
     * @throws UnsupportedOperationException
     */
    @Override
    public void stopPlaying() throws UnsupportedOperationException {
        if(mPlayer != null){
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * Upload the Project file to a web server
     * @param serverURL The URL to upload to
     * @param title The title of the project
     * @param description The description of the Project
     * @param author The author of the Project
     * @param file the file to be uploaded
     * @return whether the request was enqueued to OkHttp or not
     */
    public Boolean uploadFile(String serverURL, String title, String description, String author, File file) {
        try {

            /*
             * Set up POST body
             */
            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart(Constants.file, file.getName(),
                            RequestBody.create(MediaType.parse("application/smartroom"), file))
                    .addFormDataPart(Constants.title, title)
                    .addFormDataPart(Constants.author, author)
                    .addFormDataPart(Constants.description, description)
                    .build();

            /*
             * Set up the request
             */
            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();

            /*
             * Enqueue the request to the OkHttpClient
             */
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    /*
                     * Handle the error
                     * Show error message to the user
                     */
                    Log.d(TAG, "OkHTTPError : " + e.getMessage());
                    EventBus.getDefault().post(new UploadProjectResultEvent("Upload unsuccessful :("));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        /*
                         * Handle the error
                         * Show error message to the user
                         */
                        Log.d(TAG, "OkHTTPError : Response unsuccessful");
                        EventBus.getDefault().post(new UploadProjectResultEvent("Upload unsuccessful :("));
                        return;
                    }
                    /*
                     * Upload successful
                     */
                    Log.d(TAG, "Upload successful");
                    EventBus.getDefault().post(new UploadProjectResultEvent("Upload successful :)"));
                }
            });

            return true;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return false;
    }

    /**
     * Upload Project in a background Thread
     * @param uploadEvent The event containing the information of the Project
     */
    public void onEventBackgroundThread(UploadProjectBackgroundEvent uploadEvent){
        if(uploadFile(Constants.upload_url, uploadEvent.getTitle(), uploadEvent.getDescription(), uploadEvent.getAuthor(), uploadEvent.getFile())){
            Log.d(TAG, "Added to OkHttp queue");
        }else{
            Log.d(TAG, "Couldn't add to OkHttp queue");
        }
    }

    /**
     * Show the result of the Upload on the Main Thread (UI thread)
     * @param uploadProjectResultEvent Th event containing the Result
     */
    public void onEventMainThread(UploadProjectResultEvent uploadProjectResultEvent){
        Toast.makeText(getApplicationContext(), uploadProjectResultEvent.getResult(), Toast.LENGTH_SHORT).show();
    }

}
