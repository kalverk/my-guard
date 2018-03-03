package com.myguard;

import com.myguard.exeptions.ValidationException;

/**
 * Created by user on 07.01.2018.
 */

public enum PreferenceKey {
    sound_alert_enabled("true") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },
    sound_alert_alarm("content://settings/system/alarm_alert") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },
    sms_alert_enabled("false") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },
    call_alert_enabled("false") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },
    management_number("") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },

    location_enabled("false") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },
    location_interval("60") {
        @Override
        public void validate(String value) {
            Long.parseLong(value);
        }
    },
    location_distance("150") {
        @Override
        public void validate(String value) {
            Long.parseLong(value);
        }
    },

    location_via_sms("false") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },
    location_keyword("location") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },

    manage_via_sms("false") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },
    lock_keyword("lock") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },
    unlock_keyword("unlock") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },

    movement_enabled("true") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    },
    movement_sensitivity("100") {
        @Override
        public void validate(String value) throws ValidationException {
            long sensitivity = Long.parseLong(value);
            if (sensitivity < 0 || sensitivity > 100) {
                throw new ValidationException(R.string.movement_sensitivity_validation_error);
            }
        }
    },

    locked("false") {
        @Override
        public void validate(String value) {
            //Nothing to validate
        }
    };

    public final String defaultValue;

    PreferenceKey(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public abstract void validate(String value) throws ValidationException;
}
