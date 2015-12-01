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
import android.widget.Toast;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

import ayushkumar.smartroomsop.adapters.ProjectsRecyclerViewAdapter;
import ayushkumar.smartroomsop.events.CopyProjectBackgroundEvent;
import ayushkumar.smartroomsop.events.CopyProjectResultEvent;
import ayushkumar.smartroomsop.events.LoadProjectBackgroundEvent;
import ayushkumar.smartroomsop.events.LoadProjectResultEvent;
import ayushkumar.smartroomsop.util.Constants;
import ayushkumar.smartroomsop.util.FileUtil;
import ayushkumar.smartroomsop.util.ItemClickSupport;
import de.greenrobot.event.EventBus;

/**
 * @author Ayush Kumar
 *
 * Activity that allows the user to load a Project
 * This will be the main activity in Production use
 */
public class LoadActivity extends AppCompatActivity {

    /*
     * Tag to be used for logging in Android Monitor (LogCat)
     */
    private static final String TAG = "LoadActivity";

    /*
     * RecyclerView to show list of already existing Projects
     */
    private RecyclerView mRecyclerView;

    /*
     * Adapter for the RecyclerView to get data to the RecyclerView from the appropriate directories
     */
    private ProjectsRecyclerViewAdapter mRecyclerViewAdapter;

    /*
     * LayoutManager for the RecyclerView
     */
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Set UI of this activity
         */
        setContentView(R.layout.activity_load);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * Register with EventBus library
         */
        EventBus.getDefault().register(this);

        /*
         * Add FAB to create new Project
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateInfoActivity.class);
                startActivity(intent);
            }
        });

        /*
         * Initialize RecyclerView
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.projects_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /*
         * Set adapter of the RecyclerView
         */
        String baseProjectDirString = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Constants.app_directory + File.separator;

        mRecyclerViewAdapter = new ProjectsRecyclerViewAdapter(baseProjectDirString);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        /*
         * Set a click listener on the RecyclerView
         */
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                if(v.findViewById(R.id.project_name_tv) != null){
                    /*
                     * If the clicked view is not the empty view, then
                     * load the clicked Project in the background
                     */
                    ProjectsRecyclerViewAdapter adapter = (ProjectsRecyclerViewAdapter) mRecyclerView.getAdapter();

                    /*
                     * Get path of Project from file name
                     */
                    String fileName = adapter.getFiles().get(position).getName();
                    String filePath = adapter.getFiles().get(position).getAbsolutePath() + File.separator + fileName + Constants.extension;
                    Toast.makeText(getApplicationContext(), "Opened " + fileName, Toast.LENGTH_SHORT).show();
//                Log.d(TAG, adapter.getFiles().toString());
//                Log.d(TAG, adapter.getFiles().get(position).getName());
//                Log.d(TAG, adapter.getFiles().get(position).getAbsolutePath());

                    /*
                     * Load the clicked Project in background
                     */
                    EventBus.getDefault().post(new LoadProjectBackgroundEvent(filePath));

                    /*
                     * Asynchronously open the Open Mode
                     */
                    Intent intent1 = new Intent(getApplicationContext(), OpenActivity.class);
                    intent1.putExtra("fileName", fileName);
                    intent1.putExtra("filePath", filePath);
                    startActivity(intent1);
                }

            }
        });



        /*
         * Get the intent that started this activity
         */
        Intent intent = getIntent();
        Uri data = intent.getData();

        /*
         * If file ends in our app's extension, then try to copy it into our app
         */
        if(data!=null && data.getLastPathSegment().endsWith(Constants.extension)){

            /*
             * Get file path
             */
            String fileName = data.getLastPathSegment();
            int filePathLength = data.getEncodedPath().length();
            String filePath = data.getEncodedPath().substring(0, filePathLength - fileName.length());

            Log.d(TAG, "File Name to be copied : " + fileName);
            Log.d(TAG, "File Path : " + filePath);

            /*
             * Try to copy Project into our app in the background
             */
            EventBus.getDefault().post(new CopyProjectBackgroundEvent(filePath, fileName));
        }


    }

    @Override
    protected void onStart() {
        //EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        /*
         * Unregister the activity from EventBus
         */
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Load project in the background
     * @param loadProjectEvent Event containing information about the project
     */
    public void onEventBackgroundThread(LoadProjectBackgroundEvent loadProjectEvent) {
        boolean loaded = true;
        try {
            ZipFile zipFile = new ZipFile(loadProjectEvent.getFilePath());
            /*
             * Check to see if the zip file is password protected
             */
            if (zipFile.isEncrypted()) {
                /*
                 * if yes, then set the password for the zip file
                 */
                zipFile.setPassword("password");
            }

            /*
             * Specify the file name which has to be extracted and the path to which
             * this file has to be extracted
             */
            String base = getApplicationContext().getExternalFilesDir(null) + File.separator;

            /*
             * Extract info & data files
             */
            zipFile.extractFile(Constants.infofile, base);
            zipFile.extractFile(Constants.filename, base);
            /*
             * Extract audio file
             */
            zipFile.extractFile(Constants.audioFile, base);

        } catch (ZipException e) {
            Log.e(TAG, "Error: " + e.toString());
            loaded = false;
        }finally {
            /*
             * Send result to the Main Thread (UI Thread) for display
             */
            EventBus.getDefault().post(new LoadProjectResultEvent(loaded));
        }
    }

    /**
     * Show result of Project load on the Main Thread
     * @param resultEvent Event containing information about the project
     */
    public void onEventMainThread(LoadProjectResultEvent resultEvent){
        /*
         * Show the result in a Snackbar
         */
        View coordinatorLayout = findViewById(R.id.coordinatorLayout);
        if(resultEvent.isLoaded()){
            Snackbar.make(coordinatorLayout, "Project loaded! :)", Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(coordinatorLayout, "Error while loading project! :(", Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Copy the Project in a background Thread
     * @param copyEvent Event containing information about the project
     */
    public void onEventBackgroundThread(CopyProjectBackgroundEvent copyEvent){

        /*
         * Get the Project Name
         */
        String projectName = copyEvent.getFileName().substring(0, copyEvent.getFileName().length() - Constants.extension.length());

        /*
         * Get appropriate directory
         */
        String baseExportDirString = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Constants.app_directory + File.separator + projectName + File.separator;
        Log.d(TAG, "baseExportDirString : " + baseExportDirString);

        /*
         * Copy the file
         */
        FileUtil.copyFile(copyEvent.getFilePath(), copyEvent.getFileName(), baseExportDirString);

        Log.d(TAG, "Copied file");

        /*
         * Show the result
         */
        EventBus.getDefault().post(new CopyProjectResultEvent());
    }

    /**
     * Show the result of copy of Project in Main Thread
     * @param copyResultEvent Event containing information about the project
     */
    public void onEventMainThread(CopyProjectResultEvent copyResultEvent){

        Toast.makeText(getApplicationContext(), "Imported the project!", Toast.LENGTH_LONG).show();

        /*
         * Refresh the adapter to reflect new Project
         */
        mRecyclerViewAdapter.refreshData();
    }
}
