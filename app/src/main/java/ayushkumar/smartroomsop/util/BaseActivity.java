package ayushkumar.smartroomsop.util;

import android.support.v7.app.ActionBarActivity;
import de.greenrobot.event.EventBus;

/**
 * @author Ayush Kumar
 *
 * Utility activity that serves as a base to other activities
 * This activity initializes the EventBus library
 */
public abstract class BaseActivity extends ActionBarActivity {

    @Override
    protected void onStart() {
        super.onStart();

        /*
         * Register the activity to EventBus
         */
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {

        /*
         * Unregister the activity from EventBus
         */
        EventBus.getDefault().unregister(this);

        super.onStop();
    }
}
