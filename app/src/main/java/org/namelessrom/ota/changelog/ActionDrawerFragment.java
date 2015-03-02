package org.namelessrom.ota.changelog;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.namelessrom.ota.R;

public class ActionDrawerFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private EditTextPreference mChangesInitial;
    private EditTextPreference mChangesToFetch;

    public ActionDrawerFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_recent_changes_preferences, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.recent_changes);

        ChangeConfig config = ChangeConfig.get(getActivity());

        mChangesInitial = (EditTextPreference) findPreference(ChangeConfig.KEY_CHANGES_INITIAL);
        mChangesInitial.setText(String.valueOf(config.changesInitial));
        mChangesInitial.setSummary(getString(R.string.changes_to_load, config.changesInitial));
        mChangesInitial.setOnPreferenceChangeListener(this);

        mChangesToFetch = (EditTextPreference) findPreference(ChangeConfig.KEY_CHANGES_TO_FETCH);
        mChangesToFetch.setText(String.valueOf(config.changesToFetch));
        mChangesToFetch.setSummary(getString(R.string.changes_to_load, config.changesToFetch));
        mChangesToFetch.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference instanceof EditTextPreference) {
            String value = String.valueOf(o);
            preference.setSummary(getString(R.string.changes_to_load, value));
            ((EditTextPreference) preference).setText(value);

            if (preference == mChangesInitial) {
                ChangeConfig.get(getActivity()).changesInitial = Integer.parseInt(value);
            } else if (preference == mChangesToFetch) {
                ChangeConfig.get(getActivity()).changesToFetch = Integer.parseInt(value);
            }
            return true;
        }
        return false;
    }
}
