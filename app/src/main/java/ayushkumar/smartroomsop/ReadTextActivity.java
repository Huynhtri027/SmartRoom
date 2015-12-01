package ayushkumar.smartroomsop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ayushkumar.smartroomsop.events.StartDrawingEvent;
import ayushkumar.smartroomsop.util.BaseActivity;
import ayushkumar.smartroomsop.util.Constants;

/**
 * Created by Ayush Kumar on 22-01-15.
 *
 * @author Ayush Kumar
 *
 * Activity to show information associated with a Project
 * For Debugging purposes only
 */
public class ReadTextActivity extends ActionBarActivity {

    private File file;
    private File infoFile;
    private FileInputStream fileInputStream;
    private FileInputStream infoFileInputStream;
    private static final String TAG = "ReadTextActivity";
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readtext);

        initFile(this);
        String text = readFile(file,fileInputStream);
        textView = (TextView) findViewById(R.id.readtext_tv);
        textView.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        initFile(this);

        switch (id) {

            case R.id.action_data:
                textView.setText(readFile(file,fileInputStream));
                return true;
            case R.id.action_info:
                textView.setText(readFile(infoFile,infoFileInputStream));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private String readFile(File file, FileInputStream fileInputStream){
        int length = (int) file.length();
        byte[] bytes = new byte[length];

        try {
            int read = fileInputStream.read(bytes);
            Log.d("ReadTextActivity","Bytes read : " + read + "");
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(bytes);
    }


    private void initFile(Context context) {
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

            try {
                fileInputStream = new FileInputStream(file);
                infoFileInputStream = new FileInputStream((infoFile));
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found");
                e.printStackTrace();
            }
        }else{
            Toast.makeText(context, "Can't access SD Card.", Toast.LENGTH_LONG).show();
            Log.i(TAG,"Mem card not available?");
        }
    }

}
