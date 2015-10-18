package ayushkumar.smartroomsop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import ayushkumar.smartroomsop.R;

/**
 * Created by ayush on 16/10/15.
 */
public class ProjectsListAdapter extends BaseAdapter {

    private static final String TAG = "ProjectsListAdapter";
    private String path;
    private LayoutInflater layoutInflater;
    private ArrayList<File> files;

    public ProjectsListAdapter(String path, Context context) {
        this.path = path;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        File baseDirectory = new File(path);
        if(!baseDirectory.isDirectory()){
            Log.e(TAG, "Not a directory");
        }
        files = new ArrayList<>(Arrays.asList(baseDirectory.listFiles()));
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.project_name_list_item, parent, false);
        }

        TextView project_name_tv = (TextView) convertView.findViewById(R.id.project_name_tv);
        project_name_tv.setText(files.get(position).getName());
        return convertView;
    }
}
