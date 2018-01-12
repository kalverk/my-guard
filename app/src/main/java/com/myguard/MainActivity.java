package com.myguard;

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

import com.myguard.alerts.UIAlert;
import com.myguard.model.AlertParameters;
import com.myguard.model.LocationParameters;
import com.myguard.model.MovementParameters;
import com.myguard.service.MonitoringService;

public class MainActivity extends AppCompatActivity {

    //Charge different phones to 100%
    //Set location change listener to one (later switch also)
    //Set acceleration change listener to another (later switch also)
    //Measure battery drain, alarms and false alarms

    //Log alarms changes (accelerator, gps) and log alarms

    //Take the oldest API version and test everything


    //TODO test SMS on battery
    //TODO write tests
    //TODO when exception is thrown we should unlock automatically?

    private Intent monitoringService;
    private SharedPreferences sharedPreferences;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        monitoringService = new Intent(this, MonitoringService.class);

        button = findViewById(R.id.toggleAlarm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLockState();
            }
        });

        unlock(); //Initial state is always unlocked
    }

    private void handleLockState() {
        boolean isLocked = sharedPreferences.getBoolean(PreferenceKey.locked.name(), false);
        if (isLocked) {
            unlock();
        } else {
            lock(sharedPreferences);
        }
    }

    private void unlock() {
        button.setBackgroundResource(R.drawable.unlocked);
        sharedPreferences.edit().putBoolean(PreferenceKey.locked.name(), false).apply();

        if (monitoringService != null) {
            stopService(monitoringService);
        }
    }

    private void lock(SharedPreferences sharedPreferences) {
        final MovementParameters movementParameters = new MovementParameters(sharedPreferences);
        final LocationParameters locationParameters = new LocationParameters(sharedPreferences);
        final AlertParameters alertParameters = new AlertParameters(sharedPreferences);

        if (!movementParameters.enabled && !locationParameters.enabled) {
            UIAlert.showAlert(this, R.string.title_no_monitoring_enabled, R.string.description_no_monitoring_enabled);
            return;
        }

        if (!alertParameters.soundAlertEnabled && !alertParameters.smsAlertEnabled && !alertParameters.callAlertEnabled) {
            UIAlert.showAlert(this, R.string.title_no_alerts_enabled, R.string.description_no_alerts_enabled);
            return;
        }

        if ((alertParameters.smsAlertEnabled || alertParameters.callAlertEnabled) && (alertParameters.alertNumber == null || alertParameters.alertNumber.length() < 1)) {
            UIAlert.showAlert(this, R.string.title_invalid_phone_number, R.string.description_invalid_phone_number);
            return;
        }

        if (alertParameters.soundAlertEnabled && alertParameters.soundAlertAlarm == null) {
            UIAlert.showAlert(this, R.string.title_invalid_ringtone, R.string.description_invalid_ringtone);
            return;
        }

        button.setBackgroundResource(R.drawable.locked);
        sharedPreferences.edit().putBoolean(PreferenceKey.locked.name(), true).apply();

        monitoringService.putExtra(Constants.MOVEMENT_PARAMETERS, movementParameters);
        monitoringService.putExtra(Constants.LOCATION_PARAMETERS, locationParameters);
        monitoringService.putExtra(Constants.ALERT_PARAMETERS, alertParameters);
        startService(monitoringService);
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
