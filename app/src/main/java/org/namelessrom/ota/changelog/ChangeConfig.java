package org.namelessrom.ota.changelog;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ChangeConfig {
    public static final String KEY_CHANGES_INITIAL = "changes_initial";
    public static final String KEY_CHANGES_TO_FETCH = "changes_to_fetch";

    public int changesInitial;
    public int changesToFetch;

    private static ChangeConfig sInstance;

    private ChangeConfig(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        changesInitial = Integer.parseInt(prefs.getString(KEY_CHANGES_INITIAL, "75"));
        changesToFetch = Integer.parseInt(prefs.getString(KEY_CHANGES_TO_FETCH, "50"));
    }

    public static ChangeConfig get(Context context) {
        if (sInstance == null) {
            sInstance = new ChangeConfig(context);
        }
        return sInstance;
    }
}
