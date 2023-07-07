package cn.edu.bistu.weatherforecast.data;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.edu.bistu.weatherforecast.data.model.City;

public class CitiesDataRepository {
    private static volatile CitiesDataRepository instance = null;
    private JSONArray cityData = null;
    final private List<City> cities = new LinkedList<>();
    final private Map<Integer, City> id2city = new HashMap<>();
    final private Map<String, Integer> code2id = new HashMap<>();

    private CitiesDataRepository() {

    }

    static public CitiesDataRepository getInstance() {
        if (instance == null) {
            instance = new CitiesDataRepository();
        }
        return instance;
    }

    public void init(@NonNull Context context) {
        if (initialized())
            return;
        try {
            InputStream inputStream = context.getAssets().open("city.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            cityData = new JSONArray(new String(buffer, StandardCharsets.UTF_8));

            for (int i = 0; i < cityData.length(); i++) {
                JSONObject jsonObject = cityData.getJSONObject(i);
                int id = jsonObject.getInt("id");
                int pid = jsonObject.getInt("pid");
                City city = new City(
                        id,
                        pid,
                        jsonObject.getString("city_code"),
                        jsonObject.getString("city_name")
                );
                id2city.put(id, city);
                code2id.put(city.getCityCode(), id);
                cities.add(city);
            }

            for (int i = 0; i < cityData.length(); i++) {
                JSONObject jsonObject = cityData.getJSONObject(i);
                int id = jsonObject.getInt("id");
                int pid = jsonObject.getInt("pid");
                City city = id2city.get(id);
                City parent = id2city.getOrDefault(pid, null);
                if (parent != null && city != null) {
                    parent.addSubCity(city);
                    city.setParent(parent);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean initialized() {
        return cityData != null;
    }

    public List<City> getRootList() {
        return cities.stream().filter(City::isRoot).collect(Collectors.toList());
    }

    public City findCityById(int id) {
        return id2city.get(id);
    }

    public City findCityByCode(@NonNull String code) {
        Integer id = code2id.get(code);
        if (id == null) {
            return null;
        }
        return findCityById(id);
    }
}
