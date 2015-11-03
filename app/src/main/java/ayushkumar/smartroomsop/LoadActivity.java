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

public class LoadActivity extends AppCompatActivity {

    private static final String TAG = "LoadActivity";
    private RecyclerView mRecyclerView;
    private ProjectsRecyclerViewAdapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        EventBus.getDefault().register(this);

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

        // TODO: Implement list of already saved projects

        String baseProjectDirString = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Constants.app_directory + File.separator;

        mRecyclerViewAdapter = new ProjectsRecyclerViewAdapter(baseProjectDirString);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                ProjectsRecyclerViewAdapter adapter = (ProjectsRecyclerViewAdapter) mRecyclerView.getAdapter();
                String fileName = adapter.getFiles().get(position).getName();
                String filePath = adapter.getFiles().get(position).getAbsolutePath() + File.separator + fileName + Constants.extension;
                Toast.makeText(getApplicationContext(), "Opened " + fileName, Toast.LENGTH_SHORT).show();
//                Log.d(TAG, adapter.getFiles().toString());
//                Log.d(TAG, adapter.getFiles().get(position).getName());
//                Log.d(TAG, adapter.getFiles().get(position).getAbsolutePath());
                EventBus.getDefault().post(new LoadProjectBackgroundEvent(filePath));
                Intent intent1 = new Intent(getApplicationContext(), OpenActivity.class);
                startActivity(intent1);
            }
        });

        // Figure out what to do based on the intent type
        if(data!=null && data.getLastPathSegment().endsWith(Constants.extension)){
            String fileName = data.getLastPathSegment();
            int filePathLength = data.getEncodedPath().length();
            String filePath = data.getEncodedPath().substring(0, filePathLength - fileName.length());

            Log.d(TAG, "File Name to be copied : " + fileName);
            Log.d(TAG, "File Path : " + filePath);

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

    public void onEventBackgroundThread(CopyProjectBackgroundEvent copyEvent){

        String projectName = copyEvent.getFileName().substring(0, copyEvent.getFileName().length() - Constants.extension.length());
        String baseExportDirString = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Constants.app_directory + File.separator + projectName + File.separator;
        Log.d(TAG, "baseExportDirString : " + baseExportDirString);
        FileUtil.copyFile(copyEvent.getFilePath(), copyEvent.getFileName(), baseExportDirString);

        Log.d(TAG, "Copied file");
        EventBus.getDefault().post(new CopyProjectResultEvent());
    }

    public void onEventMainThread(CopyProjectResultEvent copyResultEvent){

        Log.d(TAG, "Showing copy result in UI");
        Toast.makeText(getApplicationContext(), "Imported the project!", Toast.LENGTH_LONG).show();
        mRecyclerViewAdapter.refreshData();
    }
}
