package app.weatheroverview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import app.weatheroverview.Forecast;
import app.weatheroverview.R;
import app.weatheroverview.utils.WeatherCodeUtils;

public class HourlyWeatherAdapter extends BaseAdapter {
	private ArrayList<Map<String, String>> items;
	private Context context;
	private Forecast forecast;
	public HourlyWeatherAdapter(Context context) {
		super();
		this.context = context;
		items = new ArrayList<>();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int i) {
		return items.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View convertView, ViewGroup viewGroup) {
		View view;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.hourly_weather_card_layout, viewGroup, false);
		} else {
			view = convertView;
		}

		((TextView)view.findViewById(R.id.text_time)).setText(getTime(i));
		String temp = forecast.getTempText(items.get(i));
		((TextView)view.findViewById(R.id.text_temp_hourly)).setText(temp);
		int drawableId = WeatherCodeUtils.iconIds.get(items.get(i).get("weatherCode"));
		((ImageView)view.findViewById(R.id.img_weather_status)).setImageResource(drawableId);
		String weatherDesc = WeatherCodeUtils.getString(items.get(i).get("weatherCode"));
		((TextView)view.findViewById(R.id.text_desc)).setText(weatherDesc);
		String feelsLike = forecast.getFeelsLikeText(context.getResources(), items.get(i));
		((TextView)view.findViewById(R.id.text_temp_hourly_feels_like)).setText(feelsLike);
		return view;
	}

	public String getTime(int i) {
		int time = Integer.valueOf(items.get(i).get("time"));
		int hours = time / 100;
		int minutes = time % 100;
		return String.format("%02d:%02d", hours, minutes);
	}

	public void update(Forecast forecast) {
		this.forecast = forecast;
		items = forecast.getHourlyWeather();
		notifyDataSetChanged();
	}
}
