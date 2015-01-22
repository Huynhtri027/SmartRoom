package ayushkumar.smartroomsop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Ayush on 22-01-15.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button create = (Button) findViewById(R.id.button);
        Button open = (Button) findViewById(R.id.button2);
        Button read = (Button) findViewById(R.id.button3);

        create.setOnClickListener(this);
        open.setOnClickListener(this);
        read.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                Intent intent = new Intent(this, CreateActivity.class);
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
        }
    }
}
