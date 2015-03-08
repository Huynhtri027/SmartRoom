package ayushkumar.smartroomsop;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ayushkumar.smartroomsop.events.StartDrawingEvent;
import ayushkumar.smartroomsop.util.BaseActivity;
import ayushkumar.smartroomsop.view.BaseView;


public class CreateActivity extends BaseActivity {

    Paint mPaint;
    BaseView baseView;
    int totalPages = 1;
    int currentPage = 1;

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

        baseView = new BaseView(this, mPaint, true);
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

        switch (id) {
            case R.id.action_clear:
                baseView.clearCanvas();
                return true;
            case R.id.action_size:
                return true;
            case R.id.action_color:
                baseView.setPaintColor(Color.BLUE);
                return true;
            case R.id.action_nextpage:
                incrementTotalPages();
                incrementCurrentPage();
                baseView.clearCanvasForNextPage();
                return true;
            case R.id.action_prevpage:
                if(currentPage > 1){
                    decrementCurrentPage();
                }
                return true;
            /*case R.id.action_savetotext:
                baseView.saveToText();
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEvent(StartDrawingEvent startDrawingEvent) {
        Toast.makeText(this, "start drawing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem nextPageMenuItem = menu.findItem(R.id.action_nextpage);
        MenuItem prevPageMenuItem = menu.findItem(R.id.action_prevpage);
        /*

        //Enable this if we need previous mode availability in Create mode too.
        //Changes will have to be made in the rest of the code to bring the desired results too.
        if(totalPages == 1){
            prevPageMenuItem.setEnabled(false);
        }else{
            prevPageMenuItem.setEnabled(true);
        }*/
        prevPageMenuItem.setEnabled(false);
        prevPageMenuItem.setVisible(false);

        return true;
    }

    public void incrementTotalPages(){
        totalPages++;
        baseView.setTotalPages(baseView.getTotalPages() + 1);
    }

    public void decrementTotalPages(){
        totalPages--;
        baseView.setTotalPages(baseView.getTotalPages() - 1);
    }

    public void incrementCurrentPage(){
        currentPage++;
        baseView.setCurrentPage(baseView.getCurrentPage() + 1);
    }

    public void decrementCurrentPage(){
        currentPage--;
        baseView.setCurrentPage(baseView.getCurrentPage() - 1);
    }
}
