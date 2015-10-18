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
import java.util.HashMap;

import ayushkumar.smartroomsop.R;
import ayushkumar.smartroomsop.util.Util;

/**
 * Created by ayush on 17/10/15.
 */
public class ProjectsRecyclerViewAdapter extends RecyclerView.Adapter<ProjectsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "ProjectsRecyclerAdapter";

    private ArrayList<File> files;
//    private ArrayList<String> descriptions;

    public ProjectsRecyclerViewAdapter(String path) {

        File baseDirectory = new File(path);
        if(!baseDirectory.isDirectory()){
            Log.e(TAG, "Not a directory");
        }
        this.files = new ArrayList<>(Arrays.asList(baseDirectory.listFiles()));
        /*descriptions = new ArrayList<>();
        if(Util.isExternalStorageReadable()){
            for (File file: files){
            }
        }*/

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProjectsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_name_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters if required

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ProjectsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText(Util.convertStringToStringWithSpaces(files.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
//        public TextView mTextViewDesc;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.project_name_tv);
//            mTextViewDesc = (TextView) v.findViewById(R.id.project_desc_tv);
        }
    }
}
