package cn.edu.bistu.weatherforecast.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import cn.edu.bistu.weatherforecast.data.model.City;
import cn.edu.bistu.weatherforecast.data.model.Weather;

public class WeatherDataRepository {
    private static volatile WeatherDataRepository instance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public final static int DATA_FETCHED = 0x01;

    private WeatherDataRepository() {
    }

    public static WeatherDataRepository getInstance() {
        if (instance == null) {
            instance = new WeatherDataRepository();
        }
        return instance;
    }

    public void init(Context context) {
        instance.preferences = context.getSharedPreferences("weather_cache", Context.MODE_PRIVATE);
        instance.editor = instance.preferences.edit();
    }

    public Weather getWeather(City city, boolean forceUpdate) {
        try {
            if (forceUpdate)
                return fetchData(city);

            Weather weather = getData(city);
            if (weather == null)
                weather = fetchData(city);
            return weather;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cache(City city, String rawJsonString) {
        long now = new Date().getTime();
        editor.putString(city.getCityCode(), now + "#" + rawJsonString);
        editor.commit();
    }

    private Weather getData(City city) throws JSONException {
        if (!preferences.contains(city.getCityCode()))
            return null;
        String[] rawData = preferences.getString(city.getCityCode(), null).split("#");
        long now = new Date().getTime();
        long time = Long.parseLong(rawData[0]);
        if (now - time <= 30 * 60 * 1000) { // 缓存半个小时
            Log.i("WEATHER_DATA", "cache hit! " + city.getCityName());
            return parseJson(rawData[1]);
        }
        return null;
    }

    private Weather parseJson(String rawJsonString) throws JSONException {
        JSONObject data = new JSONObject(rawJsonString);
        JSONObject wData = data.getJSONObject("data");
        JSONArray forecast = wData.getJSONArray("forecast");
        JSONObject fData = forecast.getJSONObject(0);

        return new Weather(
                data.getString("time"),
                data.getJSONObject("cityInfo").getString("updateTime"),
                wData.getString("wendu"),
                wData.getString("shidu"),
                wData.getDouble("pm25"),
                fData.getString("type"),
                fData.getString("high"),
                fData.getString("low"),
                fData.getString("notice")
        );
    }

    private Weather fetchData(City city) {
        Weather result = null;
        try {
            // 创建 URL 对象
            URL url = new URL("http://t.weather.sojson.com/api/weather/city/" + city.getCityCode());
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求的方法和超时时间
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            // 发起连接
            connection.connect();
            // 检查连接是否成功
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                inputStream.close();
                // 处理 JSON 数据
                String rawJsonData = response.toString();
                cache(city, rawJsonData);
                result = parseJson(rawJsonData);
            }
            // 断开连接
            connection.disconnect();
            Log.i("FetchDataTask", "doInBackground:" + city.getCityCode());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
