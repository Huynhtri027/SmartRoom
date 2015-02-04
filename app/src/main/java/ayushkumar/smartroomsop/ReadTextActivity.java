package ayushkumar.smartroomsop;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ayushkumar.smartroomsop.events.StartDrawingEvent;

/**
 * Created by Ayush on 22-01-15.
 */
public class ReadTextActivity extends BaseActivity{

    private File file;
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;
    private static final String TAG = "ReadTextActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readtext);

        initFile(this);
        String text = readFile(file);
        TextView textView = (TextView) findViewById(R.id.readtext_tv);
        textView.setText(text);
    }

    private String readFile(File file){
        int length = (int) file.length();
        byte[] bytes = new byte[length];

        try {
            fileInputStream.read(bytes);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

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
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found");
                e.printStackTrace();
            }
        }else{
            Toast.makeText(context, "Can't access SD Card.", Toast.LENGTH_LONG).show();
            Log.i(TAG,"Mem card not available?");
        }
    }

    public void onEvent(StartDrawingEvent startDrawingEvent){
        //TODO Remove
        Toast.makeText(this, "Start Drawing", Toast.LENGTH_SHORT).show();
    }
}
