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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import org.namelessrom.ota.R;
import org.namelessrom.ota.SystemUpdateActivity;

public class NotificationUtil {
    private static final int NOTIFICATION_ID = 102314;

    public static void updateAvailable(Context context) {
        final Resources res = context.getResources();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // get the intent to open the ota updater
        Intent i = new Intent(context, SystemUpdateActivity.class);
        PendingIntent pi =
                PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(res.getString(R.string.app_name))
                .setContentText(res.getString(R.string.update_avail))
                .setSmallIcon(R.drawable.ic_system_update)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public static void cancelAll(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
