package com.myguard;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
                    Context context = preference.getContext();
                    UIAlert.showAlert(context, context.getResources().getIdentifier(String.format("title_%s_invalid", preference.getKey()), "string", context.getPackageName()), context.getResources().getIdentifier(String.format("description_%s_invalid", preference.getKey()), "string", context.getPackageName()));
                    preference.setSummary(null);
                    return false;
                } else {
                    // Set the summary to reflect the new ringtone display
                    // name.
                    String name = ringtone.getTitle(preference.getContext());
                    preference.setSummary(name);
                }
            } else if (preference instanceof EditTextPreference) {
                Context context = preference.getContext();

                PreferenceKey preferenceKey = null;
                try {
                    if (preference.getKey().equals(PreferenceKey.movement_sensitivity.name())) {
                        preferenceKey = PreferenceKey.movement_sensitivity;
                        Long.parseLong(stringValue);
                    } else if (preference.getKey().equals(PreferenceKey.location_interval.name())) {
                        preferenceKey = PreferenceKey.location_interval;
                        Long.parseLong(stringValue);
                    } else if (preference.getKey().equals(PreferenceKey.location_distance.name())) {
                        preferenceKey = PreferenceKey.location_distance;
                        Long.parseLong(stringValue);
                    }
                } catch (Exception e) {
                    if (preferenceKey != null) {
                        UIAlert.showAlert(context, context.getResources().getIdentifier(String.format("title_%s_invalid", preferenceKey.name()), "string", context.getPackageName()), context.getResources().getIdentifier(String.format("description_%s_invalid", preferenceKey.name()), "string", context.getPackageName()));
                        return false;
                    }
                }

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
                || AlarmTriggersPreferenceFragment.class.getName().equals(fragmentName)
                || AlarmsPreferenceFragment.class.getName().equals(fragmentName)
                || CommandsPreferenceFragment.class.getName().equals(fragmentName);
    }

    private static final Map<Right, String[]> preferenceRights = new HashMap<>();
    private static final Map<Right, SwitchPreference> rightPreference = new HashMap<>();

    enum Right {
        location_enabled(111),
        sms_alert_enabled(222),
        call_alert_enabled(333),
        location_via_sms(444),
        manage_via_sms(555);

        public final int requestCode;

        Right(int requestCode) {
            this.requestCode = requestCode;
        }
    }

    static {
        preferenceRights.put(Right.location_enabled, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
        preferenceRights.put(Right.sms_alert_enabled, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS});
        preferenceRights.put(Right.call_alert_enabled, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE});
        preferenceRights.put(Right.location_via_sms, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE});
        preferenceRights.put(Right.manage_via_sms, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE});
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
            final SwitchPreference switchPreference = rightPreference.get(Right.location_enabled);
            switchPreference.setChecked(false);

            UIAlert.showAlert(this, R.string.title_location_rights_missing, R.string.description_location_rights_missing);
        } else if (requestCode == Right.sms_alert_enabled.requestCode && !rightsGranted) {
            final SwitchPreference switchPreference = rightPreference.get(Right.sms_alert_enabled);
            switchPreference.setChecked(false);

            UIAlert.showAlert(this, R.string.title_sms_rights_missing, R.string.description_sms_rights_missing);
        } else if (requestCode == Right.call_alert_enabled.requestCode && !rightsGranted) {
            final SwitchPreference switchPreference = rightPreference.get(Right.call_alert_enabled);
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
     * This fragment shows alarm trigger preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AlarmTriggersPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_alarm_triggers);
            setHasOptionsMenu(true);

            final EditTextPreference movementSensitivity = (EditTextPreference) findPreference(PreferenceKey.movement_sensitivity.name());
            bindPreferenceSummaryToValue(movementSensitivity, movementSensitivity.getText());

            //Location
            final EditTextPreference locationInterval = (EditTextPreference) findPreference(PreferenceKey.location_interval.name());
            final EditTextPreference locationDistance = (EditTextPreference) findPreference(PreferenceKey.location_distance.name());
            bindPreferenceSummaryToValue(locationInterval, locationInterval.getText());
            bindPreferenceSummaryToValue(locationDistance, locationDistance.getText());

            final SwitchPreference locationEnabled = (SwitchPreference) findPreference(PreferenceKey.location_enabled.name());
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
     * This fragment shows alarm preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AlarmsPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_alarms);
            setHasOptionsMenu(true);

            final EditTextPreference managementNumber = (EditTextPreference) findPreference(PreferenceKey.management_number.name());
            bindPreferenceSummaryToValue(managementNumber, managementNumber.getText());

            final SwitchPreference smsAlertEnabled = (SwitchPreference) findPreference(PreferenceKey.sms_alert_enabled.name());
            rightPreference.put(Right.sms_alert_enabled, smsAlertEnabled);
            smsAlertEnabled.setOnPreferenceChangeListener(sBindPreferenceRequireRightsListener);

            final SwitchPreference callAlertEnabled = (SwitchPreference) findPreference(PreferenceKey.call_alert_enabled.name());
            rightPreference.put(Right.call_alert_enabled, callAlertEnabled);
            callAlertEnabled.setOnPreferenceChangeListener(sBindPreferenceRequireRightsListener);
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
     * This fragment shows commands preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CommandsPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_user_commands);
            setHasOptionsMenu(true);

            final EditTextPreference managementNumber = (EditTextPreference) findPreference(PreferenceKey.management_number.name());
            bindPreferenceSummaryToValue(managementNumber, managementNumber.getText());

            final SwitchPreference locationViaSMSEnabled = (SwitchPreference) findPreference(PreferenceKey.location_via_sms.name());
            rightPreference.put(Right.location_via_sms, locationViaSMSEnabled);
            locationViaSMSEnabled.setOnPreferenceChangeListener(sBindPreferenceRequireRightsListener);

            final EditTextPreference locationKeyword = (EditTextPreference) findPreference(PreferenceKey.location_keyword.name());
            bindPreferenceSummaryToValue(locationKeyword, locationKeyword.getText());

            final SwitchPreference manageViaSMSEnabled = (SwitchPreference) findPreference(PreferenceKey.manage_via_sms.name());
            rightPreference.put(Right.manage_via_sms, manageViaSMSEnabled);
            manageViaSMSEnabled.setOnPreferenceChangeListener(sBindPreferenceRequireRightsListener);

            final EditTextPreference lockKeyword = (EditTextPreference) findPreference(PreferenceKey.lock_keyword.name());
            bindPreferenceSummaryToValue(lockKeyword, lockKeyword.getText());

            final EditTextPreference unlockKeyword = (EditTextPreference) findPreference(PreferenceKey.unlock_keyword.name());
            bindPreferenceSummaryToValue(unlockKeyword, unlockKeyword.getText());
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
