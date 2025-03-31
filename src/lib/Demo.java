package lib;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Demo {
    public static void main(String[] args) {
        String apiKey = "70618e1387f1b113a0af812ff8422e7f";
        String cityName = "Warszawa";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey;

        try {
            // Pobierz dane z API jako strumień
            String response = fetchWeatherData(url);

            // Wyświetl surową odpowiedź JSON
            System.out.println("Raw JSON Response:");
            System.out.println(response);

            // Zmapuj dane JSON na obiekt Java
            Weather weather = new Gson().fromJson(response, Weather.class);

            // Wyświetl dane pogodowe
            displayWeatherData(weather);
        } catch (Exception e) {
            System.err.println("Error fetching weather data: " + e.getMessage());
        }
    }

    /**
     * Pobiera dane pogodowe z podanego URL-a za pomocą strumieni.
     *
     * @param url URL API pogodowego
     * @return Surowa odpowiedź JSON jako String
     * @throws Exception Jeśli wystąpi błąd podczas pobierania danych
     */
    private static String fetchWeatherData(String url) throws Exception {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
            return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    /**
     * Wyświetla dane pogodowe na konsoli.
     *
     * @param weather Obiekt Weather zawierający dane pogodowe
     */
    private static void displayWeatherData(Weather weather) {
        System.out.println("\nWeather Data:");
        System.out.println("Temperature: " + weather.main.temp + " K");
        System.out.println("Feels Like: " + weather.main.feels_like + " K");
        System.out.println("Min Temperature: " + weather.main.temp_min + " K");
        System.out.println("Max Temperature: " + weather.main.temp_max + " K");
        System.out.println("Pressure: " + weather.main.pressure + " hPa");
        System.out.println("Humidity: " + weather.main.humidity + " %");
    }
}

class Weather {
    MainWeather main;
}

class MainWeather {
    double temp;
    double feels_like;
    double temp_min;
    double temp_max;
    double pressure;
    double humidity;
}