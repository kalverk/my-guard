package com.myguard;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.myguard.alerts.UIAlert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String REPLACE_LABEL = "{X}";

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value == null ? "" : value.toString();

            if (preference instanceof RingtonePreference) {
                Ringtone ringtone = RingtoneManager.getRingtone(
                        preference.getContext(), Uri.parse(stringValue));

                if (ringtone == null) {
                    // Clear the summary if there was a lookup error.
                    preference.setSummary(null);
                } else {
                    // Set the summary to reflect the new ringtone display
                    // name.
                    String name = ringtone.getTitle(preference.getContext());
                    preference.setSummary(name);
                }
            } else if (preference instanceof EditTextPreference) {
                Context context = preference.getContext();
                String summary = context.getResources().getString(context.getResources().getIdentifier("pref_description_" + preference.getKey(), "string", context.getPackageName())).replace(REPLACE_LABEL, stringValue);
                preference.setSummary(summary);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference, String summary) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, summary);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || LocationPreferenceFragment.class.getName().equals(fragmentName)
                || MovementPreferenceFragment.class.getName().equals(fragmentName)
                || AlertPreferenceFragment.class.getName().equals(fragmentName);
    }

    private static final Map<Right, String[]> preferenceRights = new HashMap<>();
    private static final Map<Right, SwitchPreference> rightPreference = new HashMap<>();

    enum Right {
        location_enabled(111),
        sms_alert_enabled(222),
        call_alert_enabled(333);

        public final int requestCode;

        Right(int requestCode) {
            this.requestCode = requestCode;
        }
    }

    static {
        preferenceRights.put(Right.location_enabled, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
        preferenceRights.put(Right.sms_alert_enabled, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS});
        preferenceRights.put(Right.call_alert_enabled, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE});
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceRequireRightsListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            Right right = Right.valueOf(preference.getKey());
            if (preferenceRights.containsKey(right)) {
                String[] requiredRights = preferenceRights.get(right);
                if (!hasRequiredRights(preference.getContext(), requiredRights) && preference.getContext() instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) preference.getContext(), requiredRights, right.requestCode);
                }
            }
            return true;
        }

        private boolean hasRequiredRights(Context context, String[] requiredRights) {
            for (String requiredRight : requiredRights) {
                if (ContextCompat.checkSelfPermission(context, requiredRight) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean rightsGranted = grantResults.length == permissions.length && allRightsGranted(grantResults);
        if (requestCode == Right.location_enabled.requestCode && !rightsGranted) {
            SwitchPreference switchPreference = rightPreference.get(Right.location_enabled);
            switchPreference.setChecked(false);

            UIAlert.showAlert(this, R.string.title_location_rights_missing, R.string.description_location_rights_missing);
        } else if (requestCode == Right.sms_alert_enabled.requestCode && !rightsGranted) {
            SwitchPreference switchPreference = rightPreference.get(Right.sms_alert_enabled);
            switchPreference.setChecked(false);

            UIAlert.showAlert(this, R.string.title_sms_rights_missing, R.string.description_sms_rights_missing);
        } else if (requestCode == Right.call_alert_enabled.requestCode && !rightsGranted) {
            SwitchPreference switchPreference = rightPreference.get(Right.call_alert_enabled);
            switchPreference.setChecked(false);

            UIAlert.showAlert(this, R.string.title_call_rights_missing, R.string.description_call_rights_missing);
        }
    }

    private static boolean allRightsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * This fragment shows movement preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MovementPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_movement);
            setHasOptionsMenu(true);

            EditTextPreference movementSensitivity = (EditTextPreference) findPreference(PreferenceKey.movement_sensitivity.name());
            bindPreferenceSummaryToValue(movementSensitivity, movementSensitivity.getText());
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows movement preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LocationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_location);
            setHasOptionsMenu(true);

            EditTextPreference locationInterval = (EditTextPreference) findPreference(PreferenceKey.location_interval.name());
            EditTextPreference locationDistance = (EditTextPreference) findPreference(PreferenceKey.location_distance.name());
            bindPreferenceSummaryToValue(locationInterval, locationInterval.getText());
            bindPreferenceSummaryToValue(locationDistance, locationDistance.getText());

            SwitchPreference locationEnabled = (SwitchPreference) findPreference(PreferenceKey.location_enabled.name());
            rightPreference.put(Right.location_enabled, locationEnabled);
            locationEnabled.setOnPreferenceChangeListener(sBindPreferenceRequireRightsListener);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows alert preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AlertPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_alert);
            setHasOptionsMenu(true);

            final EditTextPreference alertNumber = (EditTextPreference) findPreference(PreferenceKey.alert_number.name());
            bindPreferenceSummaryToValue(alertNumber, alertNumber.getText());

            SwitchPreference smsAlertEnabled = (SwitchPreference) findPreference(PreferenceKey.sms_alert_enabled.name());
            rightPreference.put(Right.sms_alert_enabled, smsAlertEnabled);
            smsAlertEnabled.setOnPreferenceChangeListener(sBindPreferenceRequireRightsListener);

            SwitchPreference callAlertEnabled = (SwitchPreference) findPreference(PreferenceKey.call_alert_enabled.name());
            rightPreference.put(Right.call_alert_enabled, callAlertEnabled);
            callAlertEnabled.setOnPreferenceChangeListener(sBindPreferenceRequireRightsListener);

            //TODO initial loadi ajal tuleb ka seda checkkida

            Preference.OnPreferenceClickListener sBindPreferenceEnableNumberInputListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

                    boolean smsAlertEnabledValue = sharedPreferences.getBoolean(PreferenceKey.sms_alert_enabled.name(), false);
                    boolean callAlertEnabledValue = sharedPreferences.getBoolean(PreferenceKey.call_alert_enabled.name(), false);
                    alertNumber.setEnabled(smsAlertEnabledValue || callAlertEnabledValue);

                    String alertNumberValue = sharedPreferences.getString(PreferenceKey.alert_number.name(), null);
                    if ((alertNumberValue == null || alertNumberValue.length() < 1) && (smsAlertEnabledValue || callAlertEnabledValue)) {
                        alertNumber.setIcon(R.drawable.ic_info_outline_black_24dp);
                    } else {
                        alertNumber.setIcon(null);
                    }

                    return true;
                }
            };

            alertNumber.setOnPreferenceClickListener(sBindPreferenceEnableNumberInputListener);
            smsAlertEnabled.setOnPreferenceClickListener(sBindPreferenceEnableNumberInputListener);
            callAlertEnabled.setOnPreferenceClickListener(sBindPreferenceEnableNumberInputListener);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
