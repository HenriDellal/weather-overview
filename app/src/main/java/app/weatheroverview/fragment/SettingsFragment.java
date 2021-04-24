package app.weatheroverview.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import app.weatheroverview.R;

public class SettingsFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.preferences, rootKey);
	}
}
