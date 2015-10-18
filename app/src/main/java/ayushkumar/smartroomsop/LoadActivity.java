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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

import ayushkumar.smartroomsop.adapters.ProjectsListAdapter;
import ayushkumar.smartroomsop.adapters.ProjectsRecyclerViewAdapter;
import ayushkumar.smartroomsop.events.LoadProjectBackgroundEvent;
import ayushkumar.smartroomsop.events.LoadProjectResultEvent;
import ayushkumar.smartroomsop.util.Constants;
import de.greenrobot.event.EventBus;

public class LoadActivity extends AppCompatActivity {

    private static final String TAG = "LoadActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
                Intent intent = new Intent(getApplicationContext(), CreateInfoActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.projects_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Get the intent that started this activity
        Intent intent = getIntent();
        Uri data = intent.getData();

        // Figure out what to do based on the intent type
        if(data!=null && data.getLastPathSegment().endsWith(Constants.extension)){
            String filePath = data.getEncodedPath();

            EventBus.getDefault().post(new LoadProjectBackgroundEvent(filePath));
        }

        // TODO: Implement list of already saved projects

        String baseProjectDirString = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Constants.app_directory + File.separator;
        mRecyclerView.setAdapter(new ProjectsRecyclerViewAdapter(baseProjectDirString));


    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventBackgroundThread(LoadProjectBackgroundEvent loadProjectEvent) {
        boolean loaded = true;
        try {
            ZipFile zipFile = new ZipFile(loadProjectEvent.getFilePath());
            // Check to see if the zip file is password protected
            if (zipFile.isEncrypted()) {
                // if yes, then set the password for the zip file
                zipFile.setPassword("password");
            }

            // Specify the file name which has to be extracted and the path to which
            // this file has to be extracted
            String base = getApplicationContext().getExternalFilesDir(null) + File.separator;

            // Extract info & data files
            zipFile.extractFile(Constants.infofile, base);
            zipFile.extractFile(Constants.filename, base);
            // Extract audio file
            zipFile.extractFile(Constants.audioFile, base);

        } catch (ZipException e) {
            Log.e(TAG, "Error: " + e.toString());
            loaded = false;
        }finally {
            EventBus.getDefault().post(new LoadProjectResultEvent(loaded));
        }
    }

    public void onEventMainThread(LoadProjectResultEvent resultEvent){
        View coordinatorLayout = findViewById(R.id.coordinatorLayout);
        if(resultEvent.isLoaded()){
            Snackbar.make(coordinatorLayout, "Project loaded! :)", Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(coordinatorLayout, "Error while loading project! :(", Snackbar.LENGTH_LONG).show();
        }
    }
}
