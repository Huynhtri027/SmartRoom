package ayushkumar.smartroomsop.util;

import android.os.Environment;

/**
 * Created by ayush on 16/10/15.
 */
public class Util {

    /**
     * Checks if external storage is available for read and write
     * @return boolean
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     *  Checks if external storage is available to at least read
     *  @return boolean
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Convert a String with spaces to one without spaces
     * @param name with spaces
     * @return name without spaces
     */
    public static String convertStringWithSpacesToOneString(String name){
        String[] name_parts = name.trim().split(" ");
        String newName = "";
        for(String name_part: name_parts){
            newName += (name_part + "_");
        }
        return newName.substring(0, newName.length() - 1);
    }
}
