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

package org.namelessrom.ota.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.namelessrom.ota.Device;
import org.namelessrom.ota.updater.UpdateEntry;
import org.namelessrom.ota.utils.AlarmScheduler;
import org.namelessrom.ota.utils.IOUtils;
import org.namelessrom.ota.utils.Logger;
import org.namelessrom.ota.utils.Utils;

import java.io.File;

public class BootupReceiver extends BroadcastReceiver {
    private static final String TAG = "BootupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "--> checking if update got applied");
        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... voids) {
                final File file = new File(IOUtils.DOWNLOAD_PATH, "update.zip.info");
                final String content = Utils.readFromFile(file);
                if (TextUtils.isEmpty(content)) {
                    Logger.d(TAG, "--> update information does not exist or is empty, exit");
                    return null;
                }

                UpdateEntry entry;
                try {
                    entry = new Gson().fromJson(content, UpdateEntry.class);
                } catch (JsonSyntaxException ignored) {
                    entry = null;
                }
                if (entry == null) {
                    Logger.d(TAG, "--> update information is not valid, exit");
                    return null;
                }

                if (Device.get().date >= entry.timestamp) {
                    Logger.d(TAG, "--> installed build is newer or equal to update, delete update");
                    new File(IOUtils.DOWNLOAD_PATH, "update.zip").delete();
                    file.delete();
                }

                Logger.d(TAG, "--> done");
                return null;
            }
        }.execute();

        // schedule update check
        AlarmScheduler.get(context).scheduleAlarm();
    }
}
