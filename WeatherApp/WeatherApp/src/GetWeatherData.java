import org.json.JSONArray; // Importuje klasę JSONArray z pakietu org.json, która umożliwia operacje na danych w formacie JSON.
import org.json.JSONObject; // Importuje klasę JSONObject z pakietu org.json, która umożliwia operacje na danych w formacie JSON.
import java.io.BufferedReader; // Importuje klasę BufferedReader z pakietu java.io, która umożliwia odczyt danych ze strumienia wejściowego.
import java.io.IOException; // Importuje klasę IOException z pakietu java.io, która reprezentuje wyjątek we/wy.
import java.io.InputStreamReader; // Importuje klasę InputStreamReader z pakietu java.io, która odczytuje znaki ze strumienia wejściowego.
import java.net.HttpURLConnection; // Importuje klasę HttpURLConnection z pakietu java.net, która umożliwia komunikację sieciową za pomocą protokołu HTTP.
import java.net.URL; // Importuje klasę URL z pakietu java.net, która reprezentuje adres URL.
import java.time.LocalDate; // Importuje klasę LocalDate z pakietu java.time, która reprezentuje datę.
import java.time.LocalDateTime; // Importuje klasę LocalDateTime z pakietu java.time, która reprezentuje datę i czas.
import java.time.format.DateTimeFormatter; // Importuje klasę DateTimeFormatter z pakietu java.time.format, która umożliwia formatowanie daty i czasu.
import java.util.ArrayList; // Importuje klasę ArrayList z pakietu java.util, która reprezentuje dynamiczną tablicę.
import java.util.HashMap; // Importuje klasę HashMap z pakietu java.util, która reprezentuje mapę z wartościami klucz-wartość.
import java.util.List; // Importuje klasę List z pakietu java.util, która reprezentuje listę elementów.
import java.util.Locale; // Importuje klasę Locale z pakietu java.util, która reprezentuje lokalizację w kontekście języka i kraju.
import java.util.Map; // Importuje klasę Map z pakietu java.util, która reprezentuje mapę z wartościami klucz-wartość.

public class GetWeatherData {
    private static final String API_KEY = "51693ae4e2407675c84c40be63cc5704"; // Stała przechowująca klucz API do usługi pogodowej.
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/"; // Stała przechowująca adres URL API usługi pogodowej.

    private Map<String, String> weatherGroups; // Mapa przechowująca grupy warunków pogodowych.

    public GetWeatherData() {
        initializeWeatherGroups(); // Wywołanie metody initializeWeatherGroups() w konstruktorze klasy GetWeatherData().
    }


    public String getWeatherData(String location, String date, String unit) {
        try {
            // Tworzenie adresu URL na podstawie podanych parametrów
            String apiUrl = API_URL + "forecast?q=" + location + "&units=" + unit + "&appid=" + API_KEY;

            // Tworzenie obiektu URL na podstawie apiUrl
            URL url = new URL(apiUrl);
            // Utworzenie połączenia HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Tworzenie BufferedReader do odczytu danych z połączenia
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Tworzenie obiektu JSON z otrzymanej odpowiedzi
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray forecastList = jsonResponse.getJSONArray("list");

            // Inicjalizacja list przechowujących dane pogodowe
            List<Double> temperatures = new ArrayList<>();
            List<Double> humidities = new ArrayList<>();
            List<Integer> cloudinesses = new ArrayList<>();
            List<Double> windSpeeds = new ArrayList<>(); // Lista prędkości wiatru

            // Przetwarzanie danych pogodowych z forecastList
            for (int i = 0; i < forecastList.length(); i++) {
                // Pobieranie obiektu JSON reprezentującego prognozę pogody dla danego dnia i godziny
                JSONObject forecast = forecastList.getJSONObject(i);
                String dateTime = forecast.getString("dt_txt");
                LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDate forecastDate = forecastDateTime.toLocalDate();

                if (forecastDate.isEqual(LocalDate.parse(date))) {
                    // Pobieranie temperatury i wilgotności z obiektu JSON
                    JSONObject main = forecast.getJSONObject("main");
                    double temperature = main.getDouble("temp");
                    double humidity = main.getDouble("humidity");

                    // Pobieranie opisu pogody i zachmurzenia z obiektu JSON
                    JSONArray weatherArray = forecast.getJSONArray("weather");
                    JSONObject weather = weatherArray.getJSONObject(0);
                    String description = weather.getString("description");

                    // Pobieranie informacji o zachmurzeniu z obiektu JSON
                    JSONObject clouds = forecast.getJSONObject("clouds");
                    int cloudiness = clouds.getInt("all");

                    // Pobieranie informacji o prędkości wiatru z obiektu JSON
                    JSONObject wind = forecast.getJSONObject("wind");
                    double windSpeed = wind.getDouble("speed");

                    // Dodawanie danych do odpowiednich list
                    temperatures.add(temperature);
                    humidities.add(humidity);
                    cloudinesses.add(cloudiness);
                    windSpeeds.add(windSpeed);
                }
            }

            if (temperatures.isEmpty() || humidities.isEmpty() || cloudinesses.isEmpty() || windSpeeds.isEmpty()) {
                // Sprawdzanie, czy któreś z list jest pusta
                return "Brak danych pogodowych dla wybranego dnia.";
            } else {
                // Obliczanie minimalnej i maksymalnej temperatury, średniej wilgotności, średniego zachmurzenia i średniej prędkości wiatru

                double minTemperature = getMinValue(temperatures);
                double maxTemperature = getMaxValue(temperatures);
                double averageHumidity = calculateAverage(humidities);
                int averageCloudiness = (int) calculateAverage(cloudinesses);
                double averageWindSpeed = convertToKilometersPerHour(calculateAverage(windSpeeds));

                // Tworzenie obiektu StringBuilder do budowania wynikowego ciągu znaków
                StringBuilder weatherData = new StringBuilder();
                weatherData.append("Średnia pogoda dla: ").append(location).append(", ").append(date).append("\n");
                weatherData.append("Opis pogody: ").append(getWeatherDescription(forecastList, date)).append("\n");
                weatherData.append("Najniższa temperatura: ").append(String.format("%.1f", minTemperature)).append(" °" + getTemperatureSymbol(unit) + "\n");
                weatherData.append("Najwyższa temperatura: ").append(String.format("%.1f", maxTemperature)).append(" °" + getTemperatureSymbol(unit) + "\n");
                weatherData.append("Średnia wilgotność: ").append(String.format("%.1f", averageHumidity)).append("%\n");
                weatherData.append("Średnie zachmurzenie: ").append(averageCloudiness).append("%\n");
                weatherData.append("Średnia prędkość wiatru: ").append(String.format("%.1f", averageWindSpeed)).append(" km/h");

                // Zwracanie wynikowego ciągu znaków
                return weatherData.toString();
            }
        } catch (IOException e) {
            // Obsługa wyjątku w przypadku problemów z pobraniem danych pogodowych
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
        // Tworzenie formatera daty i godziny
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < forecastList.length(); i++) {
            // Pobieranie obiektu JSON reprezentującego prognozę pogody dla danego dnia i godziny
            JSONObject forecast = forecastList.getJSONObject(i);
            String dateTime = forecast.getString("dt_txt");
            LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime, formatter);
            LocalDate forecastDate = forecastDateTime.toLocalDate();

            if (forecastDate.isEqual(LocalDate.parse(date))) {
                // Pobieranie informacji o pogodzie z obiektu JSON
                JSONArray weatherArray = forecast.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String description = weather.getString("description");

                // Zwracanie opisu pogody z zainicjalizowanymi grupami pogodowymi
                return initializeWeatherGroups(description);
            }
        }
        // Zwracanie pustego ciągu znaków w przypadku braku opisu pogody
        return "";
    }

//Metoda grupująca opisy pogody
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
        // Zwracanie wartości z mapy weatherGroups dla danego opisu pogody
        // Jeśli nie ma dopasowania, zwraca pusty ciąg znaków
        return weatherGroups.getOrDefault(description.toLowerCase(Locale.ENGLISH), "");
    }
    //Metoda zmieniająca m/s na km\h
    private double convertToKilometersPerHour(double speed) {
        return speed * 3.6;
    }
}