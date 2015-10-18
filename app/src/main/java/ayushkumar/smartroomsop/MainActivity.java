package ayushkumar.smartroomsop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;

import ayushkumar.smartroomsop.util.Constants;
import ayushkumar.smartroomsop.util.Util;

/**
 * Created by Ayush on 22-01-15.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button create = (Button) findViewById(R.id.button);
        Button open = (Button) findViewById(R.id.button2);
        Button read = (Button) findViewById(R.id.button3);
        Button export = (Button) findViewById(R.id.button4);
        Button load = (Button) findViewById(R.id.button5);

        create.setOnClickListener(this);
        open.setOnClickListener(this);
        read.setOnClickListener(this);
        export.setOnClickListener(this);
        load.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                Intent intent = new Intent(this, CreateInfoActivity.class);
                startActivity(intent);
                return;

            case R.id.button2:
                Intent intent2 = new Intent(this, OpenActivity.class);
                startActivity(intent2);
                return;

            case R.id.button3:
                Intent intent3 = new Intent(this, ReadTextActivity.class);
                startActivity(intent3);
                return;

            case R.id.button4:
                if(Util.isExternalStorageWritable()){
                    createZipFile();
                }else{
                    Toast.makeText(getApplicationContext(), "External Storage not available. :(", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "isExternalStorageWritable returned false");
                }
                return;
            case R.id.button5:
                Intent intent5 = new Intent(this, LoadActivity.class);
                startActivity(intent5);
                return;
        }
    }

    private void createZipFile() {
        // TODO: Do this on a background thread
        Context c = getApplicationContext();

        // baseString takes individual files(info, data, audio etc) from app's private data directory
        /*String baseString = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;*/
        String base = c.getExternalFilesDir(null) + File.separator;
        String audioFileString = base + Constants.audioFile;
        String dataFileString = base + Constants.filename;
        String infoFileString = base + Constants.infofile;

        File audioFile = new File(audioFileString);
        File dataFile = new File(dataFileString);
        File infoFile = new File(infoFileString);

        Log.d(TAG, " " + audioFile.getAbsolutePath() + ", Exists:" + audioFile.exists() );
        Log.d(TAG, " " + dataFile.getAbsolutePath() + ", Exists:" + dataFile.exists());
        Log.d(TAG, " " + infoFile.getAbsolutePath() + ", Exists:" + infoFile.exists());

        ArrayList<File> filesToBeZipped = new ArrayList<>();
        filesToBeZipped.add(audioFile);
        filesToBeZipped.add(dataFile);
        filesToBeZipped.add(infoFile);


        String baseExportDirString = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Constants.app_directory + File.separator;
        Log.d(TAG, "baseExportDirString : " + baseExportDirString);

        try {

            File baseExportDir = new File(baseExportDirString);
            if(baseExportDir.mkdirs()){
                Log.d(TAG, "Make directories returned true");
            }else{
                Log.d(TAG, "Make directories returned false");
            }

            if(baseExportDir.isDirectory()){
                Log.d(TAG, baseExportDirString + " is a directory");
            }else{
                Log.d(TAG, baseExportDirString + " is not a directory");
            }

            File zipFileDel = new File(baseExportDirString + Constants.zipFile);
            if(zipFileDel.exists()){
                zipFileDel.delete();
            }

            ZipFile zipFile = new ZipFile(baseExportDirString + Constants.zipFile);
            ZipParameters parameters = new ZipParameters();

            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            // Set the compression level. This value has to be in between 0 to 9
            // Set the compression level. This value has to be in between 0 to 9
            // Several predefined compression levels are available
            // DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of compression
            // DEFLATE_LEVEL_FAST - Low compression level but higher speed of compression
            // DEFLATE_LEVEL_NORMAL - Optimal balance between compression level/speed
            // DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
            // DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            // Set the encryption flag to true
            // If this is set to false, then the rest of encryption properties are ignored
            parameters.setEncryptFiles(true);

            // Set the encryption method to Standard Zip Encryption
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);

            /* //Uncomment this block for AES encryption
            // Set the encryption method to AES Zip Encryption
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

            // Set AES Key strength. Key strengths available for AES encryption are:
            // AES_STRENGTH_128 - For both encryption and decryption
            // AES_STRENGTH_192 - For decryption only
            // AES_STRENGTH_256 - For both encryption and decryption
            // Key strength 192 cannot be used for encryption. But if a zip file already has a
            // file encrypted with key strength of 192, then Zip4j can decrypt this file
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            */

            parameters.setPassword("password");

            zipFile.createZipFile(filesToBeZipped, parameters);
        } catch (ZipException e) {
            Log.e(TAG, "Error: " + e.toString());
        }

    }

    private void loadZipFile(){

    }
}
