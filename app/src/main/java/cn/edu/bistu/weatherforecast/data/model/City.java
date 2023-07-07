package cn.edu.bistu.weatherforecast.data.model;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

public class City {
    private int id;
    private int pid;
    private String cityCode;
    private String cityName;
    final private List<City> subCities;
    private City parent;

    public City(int id, int pid, String cityCode, String cityName) {
        this.id = id;
        this.pid = pid;
        this.cityCode = cityCode;
        this.cityName = cityName;
        subCities = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<City> getSubCities() {
        return subCities;
    }

    public void addSubCity(City city) {
        subCities.add(city);
    }

    public void setParent(City city) {
        parent = city;
    }

    public City getParent() {
        return parent;
    }

    public boolean isRoot() {
        return pid == 0;
    }

    @NonNull
    @Override
    public String toString() {
        return cityName;
    }
}
