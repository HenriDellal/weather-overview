package app.weatheroverview.utils;

import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

import app.weatheroverview.R;

public class WeatherCodeUtils {
	public static Map<String, Integer> iconIds = new HashMap<String, Integer>() {{
		put("113", R.drawable.sunny);
		put("116", R.drawable.partly_cloudy);
		put("119", R.drawable.cloudy);
		put("122", R.drawable.very_cloudy);
		put("143", R.drawable.fog);
		put("176", R.drawable.light_showers);
		put("179", R.drawable.light_sleet_showers);
		put("182", R.drawable.light_sleet);
		put("185", R.drawable.light_sleet);
		put("200", R.drawable.thundery_showers);
		put("227", R.drawable.light_snow);
		put("230", R.drawable.heavy_snow);
		put("248", R.drawable.fog);
		put("260", R.drawable.fog);
		put("263", R.drawable.light_showers);
		put("266", R.drawable.light_rain);
		put("281", R.drawable.light_sleet);
		put("284", R.drawable.light_sleet);
		put("293", R.drawable.light_rain);
		put("296", R.drawable.light_rain);
		put("299", R.drawable.heavy_showers);
		put("302", R.drawable.heavy_rain);
		put("305", R.drawable.heavy_showers);
		put("308", R.drawable.heavy_rain);
		put("311", R.drawable.light_sleet);
		put("314", R.drawable.light_sleet);
		put("317", R.drawable.light_sleet);
		put("320", R.drawable.light_snow);
		put("323", R.drawable.light_snow_showers);
		put("326", R.drawable.light_snow_showers);
		put("329", R.drawable.heavy_snow);
		put("332", R.drawable.heavy_snow);
		put("335", R.drawable.heavy_snow_showers);
		put("338", R.drawable.heavy_snow);
		put("350", R.drawable.light_sleet);
		put("353", R.drawable.light_showers);
		put("356", R.drawable.heavy_showers);
		put("359", R.drawable.heavy_rain);
		put("362", R.drawable.light_sleet_showers);
		put("365", R.drawable.light_sleet_showers);
		put("368", R.drawable.light_snow_showers);
		put("371", R.drawable.heavy_snow_showers);
		put("374", R.drawable.light_sleet_showers);
		put("377", R.drawable.light_sleet);
		put("386", R.drawable.thundery_showers);
		put("389", R.drawable.thundery_heavy_rain);
		put("392", R.drawable.thundery_snow_showers);
		put("395", R.drawable.heavy_snow_showers);
	}};

	private static Map<String, String> strings;

	public static void init(Resources res) {


		final String[] stringArray = res.getStringArray(R.array.weather_codes);
		strings = new HashMap<String, String>() {{
			String[] s;
			for (int i = 0; i < stringArray.length; i++) {
				s = stringArray[i].split("\\|");
				put(s[0], s[1]);
			}
		}};
	}
	public static String getString(String weatherCode) {
		return strings.get(weatherCode);
	}
}