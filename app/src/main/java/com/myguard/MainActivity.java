package com.myguard;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.myguard.model.AlertParameters;
import com.myguard.model.LocationParameters;
import com.myguard.model.MovementParameters;

public class MainActivity extends AppCompatActivity {

    //TODO string and default values in multiple places
    //TODO when exception is thrown we should unlock automatically?

    private final String LOCKED = "locked";

    private SharedPreferences sharedPreferences;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        button = findViewById(R.id.toggleAlarm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLocked = sharedPreferences.getBoolean(LOCKED, false);
                toggleLock(isLocked);

                if (isLocked) {
                    unlock(sharedPreferences);
                } else {
                    lock(sharedPreferences);
                }
            }
        });
    }

    private void toggleLock(boolean isLocked) {
        button.setBackgroundResource(!isLocked ? R.drawable.locked : R.drawable.unlocked);
        sharedPreferences.edit().putBoolean(LOCKED, !isLocked).apply();
    }

    private void unlock(SharedPreferences sharedPreferences) {
        //TODO stop all alarm services
    }

    private void lock(SharedPreferences sharedPreferences) {
        //TODO start all alarm services
        final MovementParameters movementParameters = new MovementParameters(sharedPreferences);
        final LocationParameters locationParameters = new LocationParameters(sharedPreferences);
        final AlertParameters alertParameters = new AlertParameters(this, sharedPreferences);

        if (!movementParameters.enabled && !locationParameters.enabled) {
            showAlert(R.string.title_no_monitoring_enabled, R.string.description_no_monitoring_enabled);
            toggleLock(true);
            return;
        }

        if (!alertParameters.soundAlertEnabled && !alertParameters.smsAlertEnabled && !alertParameters.callAlertEnabled) {
            showAlert(R.string.title_no_alerts_enabled, R.string.description_no_alerts_enabled);
            toggleLock(true);
            return;
        }

        if (movementParameters.enabled) {
            startService()
            System.out.println(movementParameters.toString());
        }

        if (locationParameters.enabled) {
            System.out.println(locationParameters.toString());
        }

        if (alertParameters.soundAlertEnabled) {
//            alertParameters.soundAlertAlarm.play();
            System.out.println(alertParameters.toString());
        }

        if (alertParameters.smsAlertEnabled) {
            System.out.println(alertParameters.toString());
        }

        if (alertParameters.callAlertEnabled) {
            System.out.println(alertParameters.toString());
        }
    }

    private void showAlert(int title, int message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .show();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
