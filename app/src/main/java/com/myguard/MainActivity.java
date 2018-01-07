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

    //TODO application icon
    //TODO string and default values in multiple places
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

        //TODO fix location management, don't need to ask for these if not using location monitoring? What about location tracking later, then cant allow access remotely
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        }, 0);

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
        boolean isLocked = sharedPreferences.getBoolean(PreferenceKeys.locked.name(), false);
        if (isLocked) {
            unlock();
        } else {
            lock(sharedPreferences);
        }
    }

    private void unlock() {
        button.setBackgroundResource(R.drawable.unlocked);
        sharedPreferences.edit().putBoolean(PreferenceKeys.locked.name(), false).apply();

        if (monitoringService != null) {
            stopService(monitoringService);
        }
    }

    private void lock(SharedPreferences sharedPreferences) {
        final MovementParameters movementParameters = new MovementParameters(sharedPreferences);
        final LocationParameters locationParameters = new LocationParameters(sharedPreferences);
        final AlertParameters alertParameters = new AlertParameters(this, sharedPreferences);

        if (!movementParameters.enabled && !locationParameters.enabled) {
            UIAlert.showAlert(this, R.string.title_no_monitoring_enabled, R.string.description_no_monitoring_enabled);
            return;
        }

        if (!alertParameters.soundAlertEnabled && !alertParameters.smsAlertEnabled && !alertParameters.callAlertEnabled) {
            UIAlert.showAlert(this, R.string.title_no_alerts_enabled, R.string.description_no_alerts_enabled);
            return;
        }

        button.setBackgroundResource(R.drawable.locked);
        sharedPreferences.edit().putBoolean(PreferenceKeys.locked.name(), true).apply();

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
