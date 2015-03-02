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

import org.namelessrom.ota.updater.Updater;
import org.namelessrom.ota.utils.Logger;

public class UpdateCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Logger.wtf(this, "intent is null!");
            return;
        }

        Logger.d(this, "checking for updates...");
        final Updater updater = new Updater(context, null);
        updater.checkWithNotification();
    }

}
