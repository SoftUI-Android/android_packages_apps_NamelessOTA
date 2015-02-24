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

package org.namelessrom.ota.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.namelessrom.ota.updater.UpdatePreferenceActivity;
import org.namelessrom.ota.updater.Updater;
import org.namelessrom.ota.receivers.UpdateCheckReceiver;

import java.text.DateFormat;

public class AlarmScheduler {
    private static AlarmScheduler sInstance;

    private static Context mContext;
    private static AlarmManager alarmManager;

    private AlarmScheduler(final Context context) {
        mContext = context;
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public static AlarmScheduler get(final Context context) {
        if (sInstance == null) {
            sInstance = new AlarmScheduler(context);
        }
        return sInstance;
    }

    public void scheduleAlarm() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String updateFrequency = prefs
                .getString(UpdatePreferenceActivity.KEY_CHECK_FREQUENCY, Updater.CHECK_THIRD_DAY);

        // Get the intent ready
        final Intent i = new Intent(mContext, UpdateCheckReceiver.class);
        final PendingIntent pi =
                PendingIntent.getBroadcast(mContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pi);

        final long frequency;
        switch (updateFrequency) {
            case Updater.CHECK_DISABLED: {
                return;
            }
            case Updater.CHECK_DAILY: {
                frequency = 86400 * 1000;
                break;
            }
            default:
            case Updater.CHECK_THIRD_DAY: {
                frequency = 259200 * 1000;
                break;
            }
            case Updater.CHECK_WEEKLY: {
                frequency = 604800 * 1000;
                break;
            }
        }

        final long lastCheck = prefs.getLong(Updater.LAST_UPDATE_CHECK_PREF, 0);
        final long triggerAt = lastCheck + frequency;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAt, frequency, pi);

        Logger.d(this, "Scheduled next (inexact) alarm for: %s",
                DateFormat.getInstance().format(triggerAt));
    }
}
