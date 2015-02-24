/*
 * <!--
 *    Copyright (C) 2013 - 2015 The NamelessRom Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * -->
 */

package org.namelessrom.ota.updater;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import org.namelessrom.ota.R;
import org.namelessrom.ota.utils.AlarmScheduler;

@SuppressWarnings("deprecation")
public class UpdatePreferenceActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    public static final String KEY_CHECK_FREQUENCY = "check_frequency";

    private ListPreference mCheckFrequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        addPreferencesFromResource(R.xml.preferences);

        mCheckFrequency = (ListPreference) findPreference(KEY_CHECK_FREQUENCY);
        final String value = String.valueOf(mCheckFrequency.getValue());
        mCheckFrequency.setSummary(mapCheckFrequencyToSummary(value));
        mCheckFrequency.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceChange(final Preference preference, final Object o) {
        if (preference == mCheckFrequency) {
            final String value = String.valueOf(o);
            mCheckFrequency.setValueIndex(Integer.parseInt(value));
            mCheckFrequency.setSummary(mapCheckFrequencyToSummary(value));

            // update alarm schedule
            AlarmScheduler.get(this).scheduleAlarm();
            return true;
        }

        return false;
    }

    private int mapCheckFrequencyToSummary(final String entry) {
        switch (entry) {
            case Updater.CHECK_DISABLED: {
                return R.string.disabled;
            }
            case Updater.CHECK_DAILY: {
                return R.string.daily;
            }
            default:
            case Updater.CHECK_THIRD_DAY: {
                return R.string.every_third_day;
            }
            case Updater.CHECK_WEEKLY: {
                return R.string.weekly;
            }
        }
    }

}
