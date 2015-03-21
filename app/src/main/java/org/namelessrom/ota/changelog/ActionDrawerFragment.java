package org.namelessrom.ota.changelog;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.namelessrom.ota.R;

public class ActionDrawerFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private EditTextPreference mChangesToFetch;

    public ActionDrawerFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_recent_changes_preferences, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.recent_changes);

        ChangeConfig config = ChangeConfig.get(getActivity());

        mChangesToFetch = (EditTextPreference) findPreference(ChangeConfig.KEY_CHANGES_TO_FETCH);
        mChangesToFetch.setText(String.valueOf(config.changesToFetch));
        mChangesToFetch.setSummary(String.valueOf(config.changesToFetch));
        mChangesToFetch.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference == mChangesToFetch) {
            String value = String.valueOf(o);
            preference.setSummary(value);
            ((EditTextPreference) preference).setText(value);

            ChangeConfig.get(getActivity()).changesToFetch = Integer.parseInt(value);
            return true;
        }
        return false;
    }
}
