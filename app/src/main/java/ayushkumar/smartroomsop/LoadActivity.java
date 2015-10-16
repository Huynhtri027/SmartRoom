package ayushkumar.smartroomsop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

import ayushkumar.smartroomsop.util.Constants;

public class LoadActivity extends AppCompatActivity {

    private static final String TAG = "LoadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Create new", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(getApplicationContext(), CreateInfoActivity.class);
                startActivity(intent);
            }
        });


        // Get the intent that started this activity
        Intent intent = getIntent();
        Uri data = intent.getData();

        // Figure out what to do based on the intent type
        /*Log.d(TAG, "Intent data: " + data.toString());
        Log.d(TAG, "Intent authority: " + data.getAuthority());
        Log.d(TAG, "Intent last path seg: " + data.getLastPathSegment());
        Log.d(TAG, "Intent path " + data.getPath());
        Log.d(TAG, "Intent encoded path " + data.getEncodedPath());*/

        if(data!=null && data.getLastPathSegment().endsWith(Constants.extension)){
            String filePath = data.getEncodedPath();

            //TODO: Do this in a background thread.
            unzipFile(filePath);
        }

    }

    private void unzipFile(String filePath) {
        try {
            ZipFile zipFile = new ZipFile(filePath);
            // Check to see if the zip file is password protected
            if (zipFile.isEncrypted()) {
                // if yes, then set the password for the zip file
                zipFile.setPassword("password");
            }

            // Specify the file name which has to be extracted and the path to which
            // this file has to be extracted
            String audioPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator;
            // Extract audio file
            zipFile.extractFile(Constants.audioFile, audioPath);

            String base = getApplicationContext().getExternalFilesDir(null) + File.separator;
            String infoFileString = base + Constants.infofile;
            String dataFileString = base + Constants.filename;
            // Extract info & data files
            zipFile.extractFile(Constants.infofile, base);
            zipFile.extractFile(Constants.filename, base);


            View coordinatorLayout = findViewById(R.id.coordinatorLayout);
            Snackbar.make(coordinatorLayout, "Load complete! :)", Snackbar.LENGTH_LONG).show();
        } catch (ZipException e) {
            Log.e(TAG, "Error: " + e.toString());
            View coordinatorLayout = findViewById(R.id.coordinatorLayout);
            Snackbar.make(coordinatorLayout, "Error! :(", Snackbar.LENGTH_LONG).show();
        }

    }

}
