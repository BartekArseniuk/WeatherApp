import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GetWeatherData {
    private static final String API_KEY = "51693ae4e2407675c84c40be63cc5704";
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/";

    private Map<String, String> weatherGroups;

    public GetWeatherData() {
        initializeWeatherGroups();
    }

    public String getWeatherData(String location, String date, String unit) {
        try {
            String apiUrl = API_URL + "forecast?q=" + location + "&units=" + unit + "&appid=" + API_KEY;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray forecastList = jsonResponse.getJSONArray("list");
            List<Double> temperatures = new ArrayList<>();
            List<Double> humidities = new ArrayList<>();
            List<Integer> cloudinesses = new ArrayList<>();
            List<Double> windSpeeds = new ArrayList<>();

            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject forecast = forecastList.getJSONObject(i);
                String dateTime = forecast.getString("dt_txt");
                LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDate forecastDate = forecastDateTime.toLocalDate();

                if (forecastDate.isEqual(LocalDate.parse(date))) {
                    JSONObject main = forecast.getJSONObject("main");
                    double temperature = main.getDouble("temp");
                    double humidity = main.getDouble("humidity");
                    JSONArray weatherArray = forecast.getJSONArray("weather");
                    JSONObject weather = weatherArray.getJSONObject(0);
                    int cloudiness = forecast.getJSONObject("clouds").getInt("all");
                    double windSpeed = forecast.getJSONObject("wind").getDouble("speed");

                    temperatures.add(temperature);
                    humidities.add(humidity);
                    cloudinesses.add(cloudiness);
                    windSpeeds.add(windSpeed);
                }
            }

            if (temperatures.isEmpty() || humidities.isEmpty() || cloudinesses.isEmpty() || windSpeeds.isEmpty()) {
                return "Brak danych pogodowych dla wybranego dnia.";
            } else {
                double minTemperature = getMinValue(temperatures);
                double maxTemperature = getMaxValue(temperatures);
                double averageHumidity = calculateAverage(humidities);
                int averageCloudiness = (int) calculateAverage(cloudinesses);
                double averageWindSpeed = convertToKilometersPerHour(calculateAverage(windSpeeds));
                StringBuilder weatherData = new StringBuilder();
                weatherData.append("Średnia pogoda dla: ").append(location).append(", ").append(date).append("\n");
                weatherData.append("Opis pogody: ").append(getWeatherDescription(forecastList, date)).append("\n");
                weatherData.append("Najniższa temperatura: ").append(String.format("%.1f", minTemperature)).append(" °" + getTemperatureSymbol(unit) + "\n");
                weatherData.append("Najwyższa temperatura: ").append(String.format("%.1f", maxTemperature)).append(" °" + getTemperatureSymbol(unit) + "\n");
                weatherData.append("Średnia wilgotność: ").append(String.format("%.1f", averageHumidity)).append("%\n");
                weatherData.append("Średnie zachmurzenie: ").append(averageCloudiness).append("%\n");
                weatherData.append("Średnia prędkość wiatru: ").append(String.format("%.1f", averageWindSpeed)).append(" km/h");
                return weatherData.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Wystąpił błąd podczas pobierania danych pogodowych.";
        }
    }

    private double calculateAverage(List<? extends Number> numbers) {
        double sum = 0;
        for (Number number : numbers) {
            sum += number.doubleValue();
        }
        return sum / numbers.size();
    }

    private double getMinValue(List<Double> values) {
        double min = values.get(0);
        for (double value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    private double getMaxValue(List<Double> values) {
        double max = values.get(0);
        for (double value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private String getTemperatureSymbol(String unit) {
        if (unit.equalsIgnoreCase("metric")) {
            return "C";
        } else if (unit.equalsIgnoreCase("imperial")) {
            return "F";
        } else {
            return "K";
        }
    }

    private String getWeatherDescription(JSONArray forecastList, String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < forecastList.length(); i++) {
            JSONObject forecast = forecastList.getJSONObject(i);
            String dateTime = forecast.getString("dt_txt");
            LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime, formatter);
            LocalDate forecastDate = forecastDateTime.toLocalDate();

            if (forecastDate.isEqual(LocalDate.parse(date))) {
                JSONArray weatherArray = forecast.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                return initializeWeatherGroups(weather.getString("description"));
            }
        }
        return "";
    }

    private void initializeWeatherGroups() {
        weatherGroups = new HashMap<>();
        weatherGroups.put("clear sky", "Słońce");
        weatherGroups.put("sunny", "Słońce");
        weatherGroups.put("fair", "Słońce");

        weatherGroups.put("clouds", "Zachmurzenie");
        weatherGroups.put("few clouds", "Zachmurzenie");
        weatherGroups.put("scattered clouds", "Zachmurzenie");
        weatherGroups.put("broken clouds", "Zachmurzenie");
        weatherGroups.put("overcast clouds", "Zachmurzenie");

        weatherGroups.put("rain", "Deszcz");
        weatherGroups.put("light rain", "Deszcz");
        weatherGroups.put("moderate rain", "Deszcz");
        weatherGroups.put("heavy rain", "Deszcz");
        weatherGroups.put("shower rain", "Deszcz");
        weatherGroups.put("drizzle", "Deszcz");
        weatherGroups.put("light intensity drizzle", "Deszcz");
        weatherGroups.put("heavy intensity drizzle", "Deszcz");
        weatherGroups.put("light intensity shower rain", "Deszcz");
        weatherGroups.put("heavy intensity shower rain", "Deszcz");
        weatherGroups.put("ragged shower rain", "Deszcz");

        weatherGroups.put("mist", "Mgła");
        weatherGroups.put("smoke", "Mgła");
        weatherGroups.put("haze", "Mgła");
        weatherGroups.put("sand/dust whirls", "Mgła");
        weatherGroups.put("fog", "Mgła");
        weatherGroups.put("sand", "Mgła");
        weatherGroups.put("dust", "Mgła");
        weatherGroups.put("volcanic ash", "Mgła");

        weatherGroups.put("thunderstorm", "Burza");
        weatherGroups.put("thunderstorm with light rain", "Burza");
        weatherGroups.put("thunderstorm with rain", "Burza");
        weatherGroups.put("light Thunderstorm", "Burza");
        weatherGroups.put("heavy Thunderstorm", "Burza");
        weatherGroups.put("ragged Thunderstorm", "Burza");
        weatherGroups.put("thunderstorm with heavy rain", "Burza");
        weatherGroups.put("thunderstorm with light drizzle", "Burza");
        weatherGroups.put("thunderstorm with drizzle", "Burza");
        weatherGroups.put("thunderstorm with heavy drizzle", "Burza");
        weatherGroups.put("thunderstorm with Hail", "Burza");
        weatherGroups.put("squalls", "Burza");
        weatherGroups.put("tornado", "Burza");
    }

    private String initializeWeatherGroups(String description) {
        return weatherGroups.getOrDefault(description.toLowerCase(Locale.ENGLISH), "");
    }

    private double convertToKilometersPerHour(double speed) {
        return speed * 3.6;
    }
}
