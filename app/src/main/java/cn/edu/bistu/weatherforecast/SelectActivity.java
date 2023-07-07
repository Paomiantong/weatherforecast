package cn.edu.bistu.weatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

import cn.edu.bistu.weatherforecast.data.CitiesDataRepository;
import cn.edu.bistu.weatherforecast.data.model.City;
import cn.edu.bistu.weatherforecast.databinding.ActivitySelectBinding;

public class SelectActivity extends AppCompatActivity {
    private ActivitySelectBinding binding;
    private int id;
    final private LinkedList<City> cities = new LinkedList<>();
    private final CitiesDataRepository citiesDataRepository = CitiesDataRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        if (id == -1)
            finish();

        City city = citiesDataRepository.findCityById(id);
        if (!"".equals(city.getCityCode())) // 直辖市也加入列表里
            cities.add(city);
        cities.addAll(city.getSubCities());
        ArrayAdapter<City> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        binding.cityList.setAdapter(adapter);
        binding.region.setText(city.getCityName());

        binding.cityList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent data = new Intent();
            data.putExtra("id", cities.get(i).getId());
            setResult(RESULT_OK, data);
            finish();
        });
    }
}