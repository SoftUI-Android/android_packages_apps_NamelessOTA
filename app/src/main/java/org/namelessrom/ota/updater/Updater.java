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

import android.content.Context;
import android.os.SystemProperties;
import android.preference.PreferenceManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.namelessrom.ota.Device;
import org.namelessrom.ota.listeners.UpdateListener;
import org.namelessrom.ota.requests.UpdatesJsonArrayRequest;
import org.namelessrom.ota.utils.AlarmScheduler;
import org.namelessrom.ota.utils.Logger;
import org.namelessrom.ota.utils.NotificationUtil;

import java.util.Date;

public class Updater implements Response.Listener<JSONArray>, Response.ErrorListener {
    private static final String PROP_OTA_URL = "ro.nameless.ota.download";
    private static final String DEFAULT_OTA_URL =
            "http://sourceforge.net/projects/soft-ui/files/SoftUI-1.0/%s/";

    public static final String SF_URL = SystemProperties.get(
            PROP_OTA_URL, DEFAULT_OTA_URL);

    public static final String LAST_UPDATE_CHECK_PREF = "pref_last_update_check";

    public static final String CHECK_DISABLED = "0";
    public static final String CHECK_DAILY = "1";
    public static final String CHECK_THIRD_DAY = "2";
    public static final String CHECK_WEEKLY = "3";

    private static final String URL = "https://raw.githubusercontent.com/Soft-UI/changelog/master/%s";

    private final Context mContext;
    private final UpdateListener mListener;

    public Updater(final Context context, final UpdateListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void check() {
        check(this);
    }

    public void check(Response.Listener<JSONArray> listener) {
        final UpdatesJsonArrayRequest jsArrReq =
                new UpdatesJsonArrayRequest(getUrl(), listener, this);
        ((UpdateApplication) mContext.getApplicationContext()).getQueue().add(jsArrReq);

        // also note down that we just checked for updates
        final Date date = new Date();
        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                .putLong(Updater.LAST_UPDATE_CHECK_PREF, date.getTime())
                .apply();

        // update alarm schedule
        AlarmScheduler.get(mContext).scheduleAlarm();
    }

    public void checkWithNotification() {
        check(new Response.Listener<JSONArray>() {
            @Override public void onResponse(JSONArray jsonArray) {
                Logger.v(this, "onResponse (with notification) -> %s", jsonArray.toString());

                UpdateEntry updateEntry = null;
                if (jsonArray.length() > 0) {
                    try {
                        updateEntry = new UpdateEntry(jsonArray.getJSONObject(0));
                    } catch (JSONException jse) {
                        Logger.e(this, "can not create UpdateEntry from jsonArray", jse);
                    }
                }

                if (updateEntry != null && updateEntry.timestamp > Device.get().date) {
                    NotificationUtil.updateAvailable(mContext);
                }

                // notify the update listener
                if (mListener != null) mListener.updateCheckFinished(true, updateEntry);
            }
        });
    }

    private String getUrl() {
        return String.format(URL, Device.get().name);
    }

    @Override
    public void onResponse(final JSONArray jsonArray) {
        Logger.v(this, "onResponse -> %s", jsonArray.toString());

        UpdateEntry updateEntry = null;
        if (jsonArray.length() > 0) {
            try {
                updateEntry = new UpdateEntry(jsonArray.getJSONObject(0));
            } catch (JSONException jse) {
                Logger.e(this, "can not create UpdateEntry from jsonArray", jse);
            }
        }

        // notify the update listener
        if (mListener != null) mListener.updateCheckFinished(true, updateEntry);
    }


    @Override
    public void onErrorResponse(final VolleyError volleyError) {
        Logger.v(this, "onErrorResponse: %s", volleyError.toString());
        if (Logger.getEnabled()) volleyError.fillInStackTrace();
        // notify the update listener
        if (mListener != null) mListener.updateCheckFinished(false, null);
    }
}
