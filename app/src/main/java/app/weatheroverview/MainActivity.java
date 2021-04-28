package app.weatheroverview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import app.weatheroverview.adapter.HourlyWeatherAdapter;
import app.weatheroverview.data.ForecastDownloader;
import app.weatheroverview.utils.WeatherCodeUtils;

public class MainActivity extends AppCompatActivity {

	Executor mainExecutor;
	Forecast forecast;

	private HourlyWeatherAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar_main);
		setSupportActionBar(toolbar);
		WeatherCodeUtils.init(getResources());
		mainExecutor = ContextCompat.getMainExecutor(this);
		adapter = new HourlyWeatherAdapter(this);
		((ListView)findViewById(R.id.list_hourly_weather)).setAdapter(adapter);
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final String server = prefs.getString("server", "http://wttr.in/");
		final String location = prefs.getString("location", "");
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					final JSONObject obj = ForecastDownloader.get(server, location);
					mainExecutor.execute(new Runnable() {
						@Override
						public void run() {
							forecast = new Forecast(MainActivity.this, obj);
							forecast.adapt(MainActivity.this);
							adapter.update(forecast);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}