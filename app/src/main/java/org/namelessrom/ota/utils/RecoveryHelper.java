/*
 * Copyright 2014 ParanoidAndroid Project
 * Modifications Copyright (C) 2014 Alexander "Evisceration" Martinz
 *
 * This file is part of Paranoid OTA.
 *
 * Paranoid OTA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Paranoid OTA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Paranoid OTA.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.namelessrom.ota.utils;

import android.content.Context;
import android.util.SparseArray;

import org.namelessrom.ota.utils.recovery.CwmBasedRecovery;
import org.namelessrom.ota.utils.recovery.RecoveryInfo;
import org.namelessrom.ota.utils.recovery.TwrpRecovery;

public class RecoveryHelper {

    private SparseArray<RecoveryInfo> mRecoveries = new SparseArray<>();

    public RecoveryHelper(Context context) {
        mRecoveries.put(RecoveryInfo.CWM_BASED, new CwmBasedRecovery(context));
        mRecoveries.put(RecoveryInfo.TWRP_BASED, new TwrpRecovery());
    }

    public RecoveryInfo getRecovery(int id) {
        final int size = mRecoveries.size();
        for (int i = 0; i < size; i++) {
            int key = mRecoveries.keyAt(i);
            RecoveryInfo info = mRecoveries.get(key);
            if (info.getId() == id) {
                return info;
            }
        }
        return null;
    }

    public String getCommandsFile(int id) {
        return getRecovery(id).getCommandsFile();
    }

    public String[] getCommands(int id, String[] items, boolean wipeData, boolean wipeCache,
            String backupFolder, String backupOptions) {
        return getRecovery(id).getCommands(items, wipeData, wipeCache, backupFolder, backupOptions);
    }
}
