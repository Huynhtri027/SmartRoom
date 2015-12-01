package ayushkumar.smartroomsop.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import ayushkumar.smartroomsop.R;
import ayushkumar.smartroomsop.util.Util;

/**
 * Created by Ayush Kumar on 17/10/15.
 *
 * @author Ayush Kumar
 *
 * @see android.support.v7.widget.RecyclerView.Adapter
 * This class is an adapter to a RecyclerView (For displaying & interacting with lists in Android)
 * This adapter is responsible to interpret the directory structure of the application's projects,
 * and to display the projects in a list format to the user.
*/
public class ProjectsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /*
     * Tag used for logging in Android Monitor (LogCat)
     */
    private static final String TAG = "ProjectsRecyclerAdapter";

    /*
     * The list of files
     */
    private ArrayList<File> files;

    /*
     * The path of the base directory
     */
    private String path;

    /*
     * Constant used for displaying empty view in the RecyclerView.
     * This will be used when there are no existing projects in the app.
     */
    private static final int EMPTY_VIEW = 1;

    /**
     * Constructor
     * @param path The path of the base directory
     */
    public ProjectsRecyclerViewAdapter(String path) {

        this.path = path;

        /*
         * Get listing of files from directory if exists, else create the directory structure necessary
         */
        File baseDirectory = new File(path);
        if(!baseDirectory.isDirectory()){
            Log.d(TAG, "Not a directory");
            if (!baseDirectory.mkdirs()){
                Log.e(TAG, "Couldn't create directory");
            }
            this.files = new ArrayList<>();
        }else {
            this.files = new ArrayList<>(Arrays.asList(baseDirectory.listFiles()));
        }

        /*descriptions = new ArrayList<>();
        if(Util.isExternalStorageReadable()){
            for (File file: files){
            }
        }*/

    }

    /**
     * Refresh the adapter to show new files
     */
    public void refreshData(){
        File baseDirectory = new File(path);
        if(!baseDirectory.isDirectory()){
            Log.e(TAG, "Not a directory");
        }
        this.files = new ArrayList<>(Arrays.asList(baseDirectory.listFiles()));
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v;
        if(viewType == EMPTY_VIEW){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_project_view, parent, false);
            return new EmptyViewHolder(v);
        }

        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_name_list_item, parent, false);
        // set the view's size, margins, padding and layout parameters if required

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder){
            ViewHolder vh = (ViewHolder) holder;
            vh.mTextView.setText(Util.convertStringToStringWithSpaces(files.get(position).getName()));
        }
    }

    @Override
    public int getItemCount() {
        return (files.size() > 0)? files.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {

        // Return empty view if no files exist
        if(files.size() == 0){
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    // Getters & Setters
    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.project_name_tv);
        }
    }
    // ViewHolder to be used in case of an empty view
    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
