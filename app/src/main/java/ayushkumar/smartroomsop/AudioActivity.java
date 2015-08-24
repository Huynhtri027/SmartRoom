package ayushkumar.smartroomsop;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import ayushkumar.smartroomsop.util.Constants;

public class AudioActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "AudioActivity";
    Button start,stop,play;
    MediaRecorder mRecorder;
    MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        start = (Button) findViewById(R.id.bt_startrec);
        stop = (Button) findViewById(R.id.bt_stoprec);
        play = (Button) findViewById(R.id.bt_play);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        play.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_startrec:
                startRecording();
                break;
            case R.id.bt_stoprec:
                stopRecording();
                break;
            case R.id.bt_play:
                startPlaying();
                break;
        }
    }

    public void startRecording() {
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

    public void stopRecording() {
        if(mRecorder != null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }


    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        String audioFile = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + (Constants.audioFile);
        try {
            mPlayer.setDataSource(audioFile);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if(mPlayer != null){
            mPlayer.release();
            mPlayer = null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopPlaying();
        stopRecording();
    }
}
