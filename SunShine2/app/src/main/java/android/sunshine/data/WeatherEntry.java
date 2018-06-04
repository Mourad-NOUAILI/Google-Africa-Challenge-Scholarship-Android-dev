/*
For temperature in Fahrenheit use units=imperial
For temperature in Celsius use units=metric
For Temperature in Kelvin is used by default, no need to use units

pressure (hPa): Atmospheric pressure (on the sea level,
if there is no sea_level or grnd_level data)

speed: Wind speed
For meter/sec use units = metric
For miles/hour use units = imperial

precipitation: (3h in json string)
Precipitation volume for last 3 hours in mm
 */

package android.sunshine.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "weather_details")
public class WeatherEntry {

    @PrimaryKey
    private long date_time;

    private int temp;

    private int temp_min;

    private int temp_max;

    private double pressure;

    private double humidity;

    private double wind_speed;

    private String description;

    private String icon;

    private String date_time_txt;

    private double precipitation;

    private String city_name;

    private String country;

    private double latitude;




    private double longitude;

    public WeatherEntry(
                        long date_time,
                        int temp,
                        int temp_min,
                        int temp_max,
                        double pressure,
                        double humidity,
                        double wind_speed,
                        String description,
                        String icon,
                        String date_time_txt,
                        double precipitation,
                        String city_name,
                        String country,
                        double latitude,
                        double longitude){
        this.date_time = date_time;
        this.temp = temp;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.pressure = pressure;
        this.humidity = humidity;
        this.wind_speed = wind_speed;
        this.description = description;
        this.icon = icon;
        this.date_time_txt = date_time_txt;
        this.precipitation = precipitation;
        this.city_name = city_name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

   /* public WeatherEntry(long id,
                        long date_time,
                        int temp,
                        int temp_min,
                        int temp_max,
                        double pressure,
                        double humidity,
                        double wind_speed,
                        String description,
                        String icon,
                        String date_time_txt,
                        double precipitation,
                        String city_name,
                        String country,
                        double latitude,
                        double longitude){
        this.id = id;
        this.date_time = date_time;
        this.temp = temp;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.pressure = pressure;
        this.humidity = humidity;
        this.wind_speed = wind_speed;
        this.description = description;
        this.icon = icon;
        this.date_time_txt = date_time_txt;
        this.precipitation = precipitation;
        this.city_name = city_name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }*/

   /* public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }*/

    public long getDate_time() {
        return date_time;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(int temp_min) {
        this.temp_min = temp_min;
    }

    public int getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(int temp_max) {
        this.temp_max = temp_max;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(double wind_speed) {
        this.wind_speed = wind_speed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDate_time_txt() {
        return date_time_txt;
    }

    public void setDate_time_txt(String date_time_txt) {
        this.date_time_txt = date_time_txt;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
