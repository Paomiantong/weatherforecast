package cn.edu.bistu.weatherforecast;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.LinkedList;
import java.util.List;

import cn.edu.bistu.weatherforecast.data.CitiesDataRepository;
import cn.edu.bistu.weatherforecast.data.LikeDataRepository;
import cn.edu.bistu.weatherforecast.data.WeatherDataRepository;
import cn.edu.bistu.weatherforecast.data.model.City;
import cn.edu.bistu.weatherforecast.data.model.Weather;
import cn.edu.bistu.weatherforecast.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private City city;
    private Weather weather;
    private final CitiesDataRepository citiesData = CitiesDataRepository.getInstance();
    private final WeatherDataRepository weatherData = WeatherDataRepository.getInstance();
    private final LikeDataRepository likeData = LikeDataRepository.getInstance();
    private final List<City> likes = new LinkedList<>();
    private ArrayAdapter<City> adapter;
    private ActivityMainBinding binding;
    private final static int SELECT_CITY_REQUEST_CODE = 0x01;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == WeatherDataRepository.DATA_FETCHED) {
                binding.refresh.setEnabled(true);
                if (weather == null) {
                    Toast.makeText(MainActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "数据获取成功", Toast.LENGTH_SHORT).show();
                applyWeather();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载
        citiesData.init(this);
        weatherData.init(this);
        likeData.init(this);
        city = citiesData.findCityById(1);// 北京
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, likes);
        binding.likeList.setAdapter(adapter);

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent, SELECT_CITY_REQUEST_CODE);
        });

        binding.refresh.setOnClickListener(view -> {
            refresh(true);
            Toast.makeText(MainActivity.this, "数据获取中", Toast.LENGTH_SHORT).show();
        });

        binding.like.setOnClickListener(view -> {
            if (likeData.liked(city))
                likeData.cancel(city);
            else
                likeData.like(city);
            foo();
        });

        binding.likeList.setOnItemClickListener((adapterView, view, i, l) -> {
            city = likes.get(i);
            refresh(false);
        });
    }

    private void foo() {
        if (likeData.liked(city)) {
            binding.like.setImageTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pink)));
        } else {
            binding.like.setImageTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));
        }
        likes.clear();
        likes.addAll(likeData.getFavouriteList());
        adapter.notifyDataSetChanged();
    }

    private void refresh(boolean force) {
        binding.refresh.setEnabled(false);
        new Thread(() ->
        {
            weather = weatherData.getWeather(city, force);
            Message message = new Message();
            message.what = WeatherDataRepository.DATA_FETCHED;
            handler.sendMessage(message);
        }).start();
    }

    private void applyWeather() {
        String parent = city.isRoot() ? "" : (city.getParent().getCityName() + " ");
        String regionTitle = parent + city.getCityName();
        binding.region.setText(regionTitle);
        binding.temperature.setText(weather.getTemperature());
        binding.humidity.setText(weather.getHumidity());
        binding.pm25.setText(String.valueOf(weather.getPm25()));
        binding.updateTime.setText(weather.getUpdateTime());
        binding.weather.setText(weather.getWeather());
        String tRange = weather.getHigh() + "/" + weather.getLow();
        binding.temperatureRange.setText(tRange);
        binding.tips.setText(weather.getTips());
        foo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_CITY_REQUEST_CODE) {
                assert data != null;
                int id = data.getIntExtra("id", 501);
                city = citiesData.findCityById(id);
                refresh(false);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh(false);
    }
}