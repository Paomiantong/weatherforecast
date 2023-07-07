package cn.edu.bistu.weatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import cn.edu.bistu.weatherforecast.data.CitiesDataRepository;
import cn.edu.bistu.weatherforecast.data.model.City;
import cn.edu.bistu.weatherforecast.databinding.ActivitySearchBinding;

public class SearchActivity extends AppCompatActivity {
    private ActivitySearchBinding binding;
    private final CitiesDataRepository citiesDataRepository = CitiesDataRepository.getInstance();
    private final List<City> cities = citiesDataRepository.getRootList();
    private final static int SUB_CITY_SELECT_REQUEST_CODE = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ArrayAdapter<City> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        binding.cityList.setAdapter(adapter);
        binding.cityList.setOnItemClickListener((adapterView, view, i, l) -> {
            City city = cities.get(i);
            Intent intent = new Intent(this, SelectActivity.class);
            intent.putExtra("id", city.getId());
            startActivityForResult(intent, SUB_CITY_SELECT_REQUEST_CODE);
        });

        binding.searchButton.setOnClickListener(view -> onSearch());
    }

    private boolean isCityCode(String input) {
        if (input.length() == 9) {
            for (int i = 0; i < input.length(); i++) {
                if (!Character.isDigit(input.charAt(i)))
                    return false;
            }
            return true;
        }
        return false;
    }

    private void onSearch() {
        String input = String.valueOf(binding.searchEdit.getText());
        if (isCityCode(input)) {
            City city = citiesDataRepository.findCityByCode(input);
            if (city == null) {
                Toast.makeText(this, "没有这个城市", Toast.LENGTH_SHORT).show();
            } else {
                Intent data = new Intent();
                data.putExtra("id", city.getId());
                setResult(RESULT_OK, data);
                finish();
            }
        } else {
            for (City c : cities) {
                if (c.getCityName().equals(input)) {
                    Intent intent = new Intent(this, SelectActivity.class);
                    intent.putExtra("id", c.getId());
                    startActivityForResult(intent, SUB_CITY_SELECT_REQUEST_CODE);
                    return;
                }
            }
            Toast.makeText(this, "没有这个省份或直辖市", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SUB_CITY_SELECT_REQUEST_CODE) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}