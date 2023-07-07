package cn.edu.bistu.weatherforecast.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cn.edu.bistu.weatherforecast.data.model.City;

public class LikeDataRepository {
    private static volatile LikeDataRepository instance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private final CitiesDataRepository repository = CitiesDataRepository.getInstance();

    public static LikeDataRepository getInstance() {
        if (instance == null) {
            instance = new LikeDataRepository();
        }
        return instance;
    }

    public void init(Context context) {
        instance.preferences = context.getSharedPreferences("favorite", Context.MODE_PRIVATE);
        instance.editor = instance.preferences.edit();
    }

    public void like(City city) {
        editor.putBoolean(city.getCityCode(), true);
        editor.commit();
    }

    public boolean liked(City city) {
        return preferences.contains(city.getCityCode());
    }

    public void cancel(City city) {
        editor.remove(city.getCityCode());
        editor.commit();
    }

    public List<City> getFavouriteList() {
        Set<String> keys = preferences.getAll().keySet();
        Log.d("LIKE", "getFavouriteList: " + keys);
        LinkedList<City> list = new LinkedList<>();
        for (String key : keys) {
            list.add(repository.findCityByCode(key));
        }
        return list;
    }
}
