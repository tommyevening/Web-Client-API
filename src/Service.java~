import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;
import java.util.stream.Collectors;

public class Service {
    private final String WEATHER_API_KEY = "4d0b712cfe32d5c8595dfd2cb312168d";
    private String country;
    private String currencyCode;
    private final Gson gson;

    public Service(String country) {
        this.country = country;
        this.currencyCode = getCurrencyCodeForCountry(country);
        this.gson = new Gson();
    }

    private String getCurrencyCodeForCountry(String countryName) {
        Locale[] locales = Locale.getAvailableLocales();

        for (Locale locale : locales) {
            if (locale.getDisplayCountry(Locale.ENGLISH).equalsIgnoreCase(countryName)) {
                try {
                    Currency currency = Currency.getInstance(locale);
                    return currency.getCurrencyCode();
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return "PLN";
    }

    public String getWeather(String city) {
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "," + country + "&appid=" + WEATHER_API_KEY + "&units=metric";
            return makeHttpRequest(apiUrl); //RAW JSON
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }



    /*
    WeatherResponse  fields as a names of JSON parameters:
        *getMain -> WeatherMain: temp, humidity, pressure
        *getWeather -> WeatherCondition: description
     */
    private String formatWeatherResponse(WeatherResponse weather) {
        return String.format("Temperature: %.2f°C\nConditions: %s\nHumidity: %.1f%%\nPressure: %.1f hPa",
                weather.getMain().getTemp(),
                weather.getWeather()[0].getDescription(),
                weather.getMain().getHumidity(),
                weather.getMain().getPressure());
    }



    private String makeHttpRequest(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    public Double getRateFor(String targetCurrency) {
        try {
            String apiUrl = "https://api.exchangerate-api.com/v4/latest/" + currencyCode;
            String jsonResponse = makeHttpRequest(apiUrl);
            return gson.fromJson(jsonResponse, JsonObject.class)
                    .getAsJsonObject("rates")
                    .get(targetCurrency)
                    .getAsDouble();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double getNBPRate() {
        // Sprawdzenie czy waluta to PLN
        if (currencyCode.equalsIgnoreCase("PLN")) {
            return 1.0;
        }

        String[] tables = {"a", "b", "c"};
        for (String table : tables) {
            try {
                String apiUrl = String.format("http://api.nbp.pl/api/exchangerates/rates/%s/%s/?format=json",
                        table, currencyCode.toLowerCase());

                InputStream inputStream = new URL(apiUrl).openConnection().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String jsonResponse = reader.lines().collect(Collectors.joining());

                // Parsowanie JSON
                Double rate = gson.fromJson(jsonResponse, JsonObject.class)
                        .getAsJsonArray("rates")
                        .get(0)
                        .getAsJsonObject()
                        .get("mid")
                        .getAsDouble();

                reader.close();
                inputStream.close();
                return rate;

            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}