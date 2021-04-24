package app.weatheroverview.data;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class ForecastDownloader {
	private static String tag = "app.weatheroverview";

	public static JSONObject get(String location) {
		URL url;
		HttpURLConnection connection;
		try {
			url = new URL(String.format("http://wttr.in/%s?format=j1", location));
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.setRequestProperty("Accept", "application/json");
			connection.connect();
			Log.e(tag, String.valueOf(connection.getResponseCode()));
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(tag, e.toString());
			return null;
		}
		JSONObject obj = null;
		try (InputStream is = connection.getInputStream()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while (null != (line = reader.readLine())) {
				sb.append(line);
			}
			obj = new JSONObject(sb.toString());
		} catch (JSONException| IOException e) {
			Log.e(tag, e.toString());
			e.printStackTrace();
		}
		return obj;
	}
}