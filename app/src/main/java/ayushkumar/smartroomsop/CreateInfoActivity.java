package ayushkumar.smartroomsop;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class CreateInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Open create activity", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                validateInputs();
            }
        });
    }

    private void validateInputs() {
        TextInputLayout til1 = (TextInputLayout) findViewById(R.id.til1);
        TextInputLayout til2 = (TextInputLayout) findViewById(R.id.til2);

        EditText name_et = (EditText) findViewById(R.id.name_et);
        EditText desc_et = (EditText) findViewById(R.id.desc_et);

        String name = name_et.getText().toString();
        String desc = desc_et.getText().toString();
        boolean validated = true;

        if(name.equals("")){
            til1.setError("Name cannot be blank");
            validated = false;
        }else {
            til1.setError(null);
        }
        if(desc.equals("")){
            til2.setError("Description cannot be blank");
            validated = false;
        }else {
            til2.setError(null);
        }

        if(validated){
            Intent i = new Intent(this, CreateActivity.class);
            i.putExtra("name", name);
            i.putExtra("description", desc);
            startActivity(i);
        }

    }

}
