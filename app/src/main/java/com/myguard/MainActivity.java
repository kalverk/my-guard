package com.myguard;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
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

    //TODO test SMS on battery
    //TODO write tests
    //TODO when exception is thrown we should unlock automatically?

    //TODO lag for alert so that vibration does not trigger the alarm - vibration is set to zero, works?

    //TODO allow signes in numbers?
    //TODO test that preferences do not change if invalid values are added

    private final String APP_RUN_FIRST_TIME = "app_run_first_time";

    private SharedPreferences sharedPreferences;
    private Intent monitoringService;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        monitoringService = new Intent(this, MonitoringService.class);

        if (sharedPreferences.getString(APP_RUN_FIRST_TIME, null) == null) {
            sharedPreferences.edit().putString(APP_RUN_FIRST_TIME, APP_RUN_FIRST_TIME).commit();
            setInitialPreferenceValues();
        }

        button = findViewById(R.id.toggleAlarm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLockState();
            }
        });

        unlock(); //Initial state is always unlocked
    }

    private void setInitialPreferenceValues() {
        sharedPreferences.edit().putBoolean(PreferenceKey.movement_enabled.name(), Boolean.parseBoolean(PreferenceKey.movement_enabled.defaultValue)).commit();

        sharedPreferences.edit().putBoolean(PreferenceKey.sound_alert_enabled.name(), Boolean.parseBoolean(PreferenceKey.sound_alert_enabled.defaultValue)).commit();
        sharedPreferences.edit().putString(PreferenceKey.sound_alert_alarm.name(), PreferenceKey.sound_alert_alarm.defaultValue).commit();
    }

    private void handleLockState() {
        boolean isLocked = sharedPreferences.getBoolean(PreferenceKey.locked.name(), Boolean.parseBoolean(PreferenceKey.locked.defaultValue));
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
