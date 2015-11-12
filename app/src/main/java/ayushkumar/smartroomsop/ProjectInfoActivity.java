package ayushkumar.smartroomsop;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ayushkumar.smartroomsop.model.InfoModel;
import ayushkumar.smartroomsop.model.PageEndTimesModel;
import ayushkumar.smartroomsop.model.ProjectInfoModel;
import ayushkumar.smartroomsop.util.Constants;

public class ProjectInfoActivity extends AppCompatActivity {


    private static final String TAG = "ProjectInfoActivity";
    File infoFile;
    FileInputStream infoFileInputStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFile(this);
        Gson gson = new Gson();
        try {
            BufferedReader br = new BufferedReader(new FileReader(infoFile));
            br.readLine();
            br.readLine();
            String line = br.readLine();
            ProjectInfoModel projectInfoModel = gson.fromJson(line, ProjectInfoModel.class);

            TextView title_tv = (TextView)findViewById(R.id.title_tv);
            TextView author_tv = (TextView) findViewById(R.id.author_tv);
            TextView desc_tv = (TextView) findViewById(R.id.desc_tv);

            title_tv.setText(projectInfoModel.getName());
            author_tv.setText(projectInfoModel.getAuthor());
            desc_tv.setText(projectInfoModel.getDescription());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException i) {
            i.printStackTrace();
        }
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
            infoFile = new File(context.getExternalFilesDir(null) + File.separator + (Constants.infofile));
            if (infoFile.exists()) {
                Log.d(TAG, "Info File exists");
            } else {
                Log.d(TAG, "Info file doesn't exist");
            }
            try {
                infoFileInputStream = new FileInputStream(infoFile);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "Info File not found");
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Can't access SD Card.", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Mem card not available?");
        }
    }

}
