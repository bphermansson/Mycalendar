package nu.paheco.patrik.mycalendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by user on 11/1/14.
 */
public class help extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        Log.d("Help", "In onCreate()");
    }
}
