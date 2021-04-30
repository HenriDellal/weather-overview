package app.weatheroverview.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ForecastDownloader {
	private static boolean useCache;

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static String tag = "app.weatheroverview";
	private static String server;
	private static String location;

	public static JSONObject get(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		server = prefs.getString("server", "https://wttr.in/");
		location = prefs.getString("location", "");
		useCache = prefs.getBoolean("use_cache", true);
		if (useCache) {
			Date date = new Date();
			File cachedFile = getCacheFile(context, date);
			if (cachedFile.exists()) {
				return readCache(cachedFile);
			}
		}
		return download(context);
	}

	private static JSONObject download(Context context) {
		URL url;
		HttpURLConnection connection;
		try {
			if (!server.endsWith("/"))
				server += "/";
			url = new URL(String.format("%s%s?format=j1", server, location));
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.setRequestProperty("Accept", "application/json");
			connection.connect();
		} catch (IOException e) {
			e.printStackTrace();
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
			String jsonContents = sb.toString();
			obj = new JSONObject(jsonContents);
			cleanCache(context);
			if (useCache)
				writeCache(jsonContents, getCacheFile(context));
		} catch (JSONException| IOException e) {
			Log.e(tag, e.toString());
			e.printStackTrace();
		}
		return obj;
	}

	public static void cleanCache(Context context) {
		File[] files = context.getCacheDir().listFiles();
		if (null != files)
			for (File f : files)
				f.delete();
	}

	private static File getCacheFile(Context context) {
		return getCacheFile(context, new Date());
	}

	@SuppressLint("DefaultLocale")
	private static File getCacheFile(Context context, Date date) {
		File cacheDir = context.getCacheDir();

		String filename = String.format("%s/%s/", getNormalizedServerName(), getNormalizedLocation());
		File cacheFileDir = new File(cacheDir, filename);
		if (!cacheFileDir.exists())
			cacheFileDir.mkdirs();

		filename = String.format("%s.json", dateFormat.format(date));
		File cacheFile = new File(cacheFileDir, filename);

		return cacheFile;
	}

	private static JSONObject readCache(File cachedFile) {
		JSONObject obj = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(cachedFile))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String content = sb.toString();
			obj = new JSONObject(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	private static void writeCache(String jsonContents, File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(jsonContents);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getNormalizedServerName() {
		String domain;
		try {
			URI uri = new URI(server);
			domain = uri.getHost();
			int lastIndex = Math.min(domain.length(), 128);
			return domain.substring(0, lastIndex);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			domain = "default-domain";
		}
		return domain;
	}

	private static String getNormalizedLocation() {
		try {
			String result = "location-" + URLEncoder.encode(location, "UTF-8");
			int lastIndex = Math.min(result.length(), 128);
			return result.substring(0, lastIndex);
		} catch (Exception e) {
			e.printStackTrace();
			return "location-default";
		}
	}
}
