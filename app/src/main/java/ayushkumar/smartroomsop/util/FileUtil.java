package ayushkumar.smartroomsop.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ayush on 18/10/15.
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * Utility to copy a file
     * @param inputPath Input path - Should end in /
     * @param inputFile Input file name
     * @param outputPath Output file path - Should end in /
     */
    public static void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in;
        OutputStream out;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            // write the output file (You have now copied the file)
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            {
                Log.e(TAG, e.getMessage());
            }
        }

    }
}
