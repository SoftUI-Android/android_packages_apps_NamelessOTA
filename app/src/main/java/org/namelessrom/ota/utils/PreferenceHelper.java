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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {
    private static PreferenceHelper sInstance;

    private static SharedPreferences sPrefs;

    public static final String PREF_LAST_CHECKED = "pref_last_checked";
    public static final String PREF_UPDATE_AVAIL = "pref_update_avail";

    public static final String DOWNLOAD_ROM_ID = "download_rom_id";
    public static final String DOWNLOAD_ROM_MD5 = "download_rom_md5";
    public static final String DOWNLOAD_ROM_FILENAME = "download_rom_filaname";

    private PreferenceHelper(final Context context) {
        sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceHelper get(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceHelper(context);
        }
        return sInstance;
    }

    public String getString(final String key, final String def) {
        return sPrefs.getString(key, def);
    }

    public PreferenceHelper putString(final String key, final String value) {
        sPrefs.edit().putString(key, value).apply();
        return sInstance;
    }

    public void setDownloadRomId(Long id, String md5, String fileName) {
        if (id == null) {
            removePreference(DOWNLOAD_ROM_ID);
            removePreference(DOWNLOAD_ROM_MD5);
            removePreference(DOWNLOAD_ROM_FILENAME);
        } else {
            savePreference(DOWNLOAD_ROM_ID, String.valueOf(id));
            savePreference(DOWNLOAD_ROM_MD5, md5);
            savePreference(DOWNLOAD_ROM_FILENAME, fileName);
        }
    }

    public long getDownloadRomId() {
        return Utils.tryParseLong(sPrefs.getString(DOWNLOAD_ROM_ID, "-1"));
    }

    public String getDownloadRomMd5() {
        return getString(DOWNLOAD_ROM_MD5, null);
    }

    public String getDownloadRomName() {
        return getString(DOWNLOAD_ROM_FILENAME, null);
    }

    private void savePreference(final String key, String value) {
        sPrefs.edit().putString(key, value).apply();
    }

    private void removePreference(final String key) {
        sPrefs.edit().remove(key).apply();
    }
}
