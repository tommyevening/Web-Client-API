package done;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;

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
        // While comparing PLN == PLN
        if (currencyCode.equalsIgnoreCase("PLN")) {
            System.out.println("Currency is PLN, returning 1.0");
            return 1.0;
        }


        /*
        API NBP has TABLES A, B, C: every table has different currencies
        We need to check all tables
        */

        String[] tables = {"a", "b", "c"};
        for (String table : tables) {
            try {
                String apiUrl = String.format("http://api.nbp.pl/api/exchangerates/rates/%s/%s/?format=json",
                        table, currencyCode.toLowerCase());

                String jsonResponse = makeHttpRequest(apiUrl);

                // JSON parsing
                Double rate = gson.fromJson(jsonResponse, JsonObject.class)
                        .getAsJsonArray("rates")
                        .get(0)
                        .getAsJsonObject()
                        .get("mid") // middle rate
                        .getAsDouble();

                System.out.println("Rate found in table " + table.toUpperCase());
                return rate;
            } catch (Exception e) {
                System.out.println("Not found in table " + table.toUpperCase());
            }
        }
        System.out.println("Currency not found in any table");
        return null;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}