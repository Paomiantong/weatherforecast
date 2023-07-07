package cn.edu.bistu.weatherforecast.data.model;

public class Weather {
    private String time;
    private String updateTime;
    private String temperature;
    private String humidity;
    private double pm25;
    private String weather;
    private String high;
    private String low;
    private String tips;


    public Weather(String time, String updateTime, String temperature, String humidity,
                   double pm25, String weather, String high, String low, String tips) {
        this.time = time;
        this.updateTime = updateTime;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pm25 = pm25;
        this.weather = weather;
        this.high = high.substring(3).replace("℃", "") + "°";
        this.low = low.substring(3).replace("℃", "") + "°";
        this.tips = tips;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUpdateTime() {
        return "更新时间 " + updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getTemperature() {
        return temperature + "°C";
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return "湿度 " + humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPm25() {
        return "pm2.5 " + pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}

//public class Forecast {
//
//}