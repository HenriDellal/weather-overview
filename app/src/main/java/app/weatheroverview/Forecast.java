package app.weatheroverview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Forecast {
	private Map<String, String> currentConditionMap;
	private String region;
	private boolean useCelsius, useKilometers;
	public static final String[] currentConditionKeys = new String[] {
			"temp_C",
			"temp_F",
			"FeelsLikeC",
			"FeelsLikeF",
			"weatherCode",
			"humidity",
			"windspeedKmph",
			"windspeedMiles"
	};

	private Map<String, String> weatherToday;

	public static final String[] dailyWeatherKeys = new String[] {
			"avgtempC",
			"avgtempF",
			"date",
			"maxtempC",
			"maxtempF",
			"mintempC",
			"mintempF"
	};

	public static final String[] hourlyWeatherKeys = new String[] {
			"time",
			"tempC",
			"tempF",
			"FeelsLikeC",
			"FeelsLikeF",
			"humidity",
			"weatherCode",
			"windspeedKmph",
			"windspeedMiles"
	};

	public static final Map<String, Integer> templateIds = new HashMap<String, Integer>() {{
			put("windspeedKmph", R.string.wind_speed_kmh);
			put("windspeedMiles", R.string.wind_speed_mph);
	}};

	private ArrayList<Map<String, String>> weatherHourly;

	public Forecast(Context context, JSONObject obj) {
		prefsInit(context);

		currentConditionMap = new HashMap<>();
		weatherToday = new HashMap<>();
		weatherHourly = new ArrayList<>();
		try {
			JSONObject currentCondition = obj.getJSONArray("current_condition").getJSONObject(0);
			for (String key: currentConditionKeys) {
				currentConditionMap.put(key, currentCondition.getString(key));
			}

			region = obj.getJSONArray("nearest_area")
						.getJSONObject(0)
						.getJSONArray("region")
						.getJSONObject(0)
						.getString("value");

			JSONObject weather = obj.getJSONArray("weather").getJSONObject(0);
			for (String key: dailyWeatherKeys) {
				weatherToday.put(key, weather.getString(key));
			}
			JSONArray weatherByHours = weather.getJSONArray("hourly");
			for (int i = 0; i < weatherByHours.length(); i++) {

				Map<String, String> hour = new HashMap<>();
				JSONObject object = weatherByHours.getJSONObject(i);
				for (String key: hourlyWeatherKeys) {

					Log.d("app.weatheroverview", object.getString(key));
					hour.put(key, object.getString(key));
				}
				weatherHourly.add(hour);
			}
			Log.d("app.weatheroverview", weatherHourly.get(0).toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void prefsInit(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Resources res = context.getResources();
		String defaultTempUnit = res.getString(R.string.celsius);
		String defaultSpeedUnit = res.getString(R.string.fahrenheit);
		String tempUnitPref = prefs.getString("measure_unit_temp", defaultTempUnit);
		Log.d("d", tempUnitPref);
		useCelsius = defaultTempUnit.equals(tempUnitPref);
		String speedUnitPerf = prefs.getString("measure_unit_speed", defaultSpeedUnit);
		useKilometers = defaultSpeedUnit.equals(speedUnitPerf);
	}

	public String getRegion() {
		return region;
	}

	public ArrayList<Map<String, String>> getHourlyWeather() {
		return weatherHourly;
	}

	public void adapt(Activity activity) {
		Resources res = activity.getResources();

		((TextView)activity.findViewById(R.id.text_temp)).setText(getTempText(currentConditionMap));
		((TextView)activity.findViewById(R.id.text_temp_feels_like)).setText(getFeelsLikeText(res, currentConditionMap));
		((TextView)activity.findViewById(R.id.text_wind_speed)).setText(getWindSpeedText(res, currentConditionMap));

		String humidity = String.format(res.getString(R.string.humidity), currentConditionMap.get("humidity"));
		((TextView)activity.findViewById(R.id.text_humidity)).setText(humidity);


		// DAILY WEATHER
		String[] keys;
		int templateId;
		if (useCelsius) {
			keys = new String[]{
					"mintempC", "maxtempC"
			};
			templateId = R.string.temp_range_celsius;
		} else {
			keys = new String[]{
					"mintempF", "maxtempF"
			};
			templateId = R.string.temp_range_fahrenheit;
		}
		String minTemp = weatherToday.get(keys[0]);
		String maxTemp = weatherToday.get(keys[1]);
		String tempRange = String.format(res.getString(templateId), minTemp, maxTemp);
		//((TextView)activity.findViewById(R.id.text_temp_range)).setText(tempRange);

		String date = weatherToday.get("date");
		//((TextView)activity.findViewById(R.id.text_date)).setText(date);
	}

	public String getTempText(Map<String, String> map) {
		String key, degreeSymbol;
		boolean isHourlyMap = map.containsKey("time");
		if (useCelsius) {
			key = isHourlyMap ? "tempC" : "temp_C";
			degreeSymbol = "\u2103";
		} else {
			key = isHourlyMap ? "tempF" : "temp_F";
			degreeSymbol = "\u2109";
		}
		return String.format("%s%s", map.get(key), degreeSymbol);
	}

	public String getFeelsLikeText(Resources res, Map<String, String> map) {
		String key, degreeSymbol;
		if (useCelsius) {
			key = "FeelsLikeC";
			degreeSymbol = "\u2103";
		} else {
			key = "FeelsLikeF";
			degreeSymbol = "\u2109";
		}
		String template = res.getString(R.string.feels_like);
		return String.format(template, map.get(key), degreeSymbol);
	}

	public String getWindSpeedText(Resources res, Map<String, String> map) {
		String key = useKilometers ? "windspeedKmph" : "windspeedMiles";
		String template = res.getString(templateIds.get(key));
		return String.format(template, map.get(key));
	}
}