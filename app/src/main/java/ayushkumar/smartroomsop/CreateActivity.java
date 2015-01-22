package ayushkumar.smartroomsop;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ayushkumar.smartroomsop.view.BaseView;


public class CreateActivity extends ActionBarActivity {

    Paint mPaint;
    BaseView baseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        baseView = new BaseView(this, mPaint);
        setContentView(baseView);

    }

    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_clear :
                baseView.clearCanvas();

                return true;
            case R.id.action_size :


                return true;
            case R.id.action_color :
                baseView.setPaintColor(Color.BLUE);
                return true;
            /*case R.id.action_savetotext:
                baseView.saveToText();
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }
}
