package app.weatheroverview;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import app.weatheroverview.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.container_settings, new SettingsFragment())
				.commit();
	}
}
