/**
 * @author Wieczorek Tomasz S27161
 */

package test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Service {
    private final String WEATHER_API_KEY = "4d0b712cfe32d5c8595dfd2cb312168d";
    private String country;
    private String currencyCode;

    public Service(String country) {
        this.country = country;
        // You'll need to map countries to their currency codes
        // This is a simplified example
        if (country.equalsIgnoreCase("Poland")) {
            this.currencyCode = "PLN";
        }
        // Add more mappings as needed
    }

    public String getWeather(String city) {
        try {
            String apiUrl = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s,%s&appid=%s&units=metric",
                    city, country, WEATHER_API_KEY);

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

            // Parse the JSON response
            JsonObject jsonResponse = new Gson().fromJson(response.toString(), JsonObject.class);

            // Extract relevant weather information
            double temperature = jsonResponse.getAsJsonObject("main").get("temp").getAsDouble();
            double humidity = jsonResponse.getAsJsonObject("main").get("humidity").getAsDouble();
            String weatherDescription = jsonResponse.getAsJsonArray("weather")
                    .get(0).getAsJsonObject().get("description").getAsString();

            // Format the weather information
            return String.format("Weather in %s, %s:\n" +
                            "Temperature: %.1f°C\n" +
                            "Humidity: %.1f%%\n" +
                            "Conditions: %s",
                    city, country, temperature, humidity, weatherDescription);

        } catch (Exception e) {
            return "Error fetching weather data: " + e.getMessage();
        }
    }

    public String getCurrencyRates(String country) {
        try {
            // Get currency code for the country
            String currencyCode = getCurrencyCodeForCountry(country);
            if (currencyCode == null) {
                return "Currency code not found for " + country;
            }

            StringBuilder result = new StringBuilder();

            // Get NBP Table A rates
            String tableAUrl = "https://api.nbp.pl/api/exchangerates/rates/a/" + currencyCode + "/?format=json";
            Double rateA = getNBPRateFromTable(tableAUrl);
            if (rateA != null) {
                result.append("NBP Table A Rate: ").append(String.format("%.4f PLN", rateA)).append("\n");
            }

            // Get NBP Table B rates
            String tableBUrl = "https://api.nbp.pl/api/exchangerates/rates/b/" + currencyCode + "/?format=json";
            Double rateB = getNBPRateFromTable(tableBUrl);
            if (rateB != null) {
                result.append("NBP Table B Rate: ").append(String.format("%.4f PLN", rateB)).append("\n");
            }

            // Get NBP Table C rates
            String tableCUrl = "https://api.nbp.pl/api/exchangerates/rates/c/" + currencyCode + "/?format=json";
            Double rateC = getNBPRateFromTable(tableCUrl);
            if (rateC != null) {
                result.append("NBP Table C Rate: ").append(String.format("%.4f PLN", rateC));
            }

            return result.toString();
        } catch (Exception e) {
            return "Error getting currency rates: " + e.getMessage();
        }
    }

    private Double getNBPRateFromTable(String apiUrl) {
        try {
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

            JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
            return jsonObject.getAsJsonArray("rates").get(0).getAsJsonObject().get("mid").getAsDouble();
        } catch (Exception e) {
            return null;
        }
    }

    private String getCurrencyCodeForCountry(String country) {
        // Simple mapping of countries to their currency codes
        switch (country.toLowerCase()) {
            case "poland": return "PLN";
            case "usa": return "USD";
            case "uk": return "GBP";
            case "european union": return "EUR";
            // Add more mappings as needed
            default: return null;
        }
    }

    public Double getRateFor(String targetCurrency) {
        try {
            String apiUrl = "https://open.er-api.com/v6/latest/" + currencyCode;
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

            JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
            return jsonObject.getAsJsonObject("rates").get(targetCurrency).getAsDouble();
        } catch (Exception e) {
            return null;
        }
    }

    public Double getNBPRate() {
        try {
            // This is a simplified version - you'll need to implement proper NBP API handling
            String apiUrl = "https://api.nbp.pl/api/exchangerates/rates/a/" + currencyCode + "/?format=json";
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

            JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
            return jsonObject.getAsJsonArray("rates").get(0).getAsJsonObject().get("mid").getAsDouble();
        } catch (Exception e) {
            return null;
        }
    }


    public String getRawWeatherJson(String city) {
        try {
            // Construct the API URL
            String apiUrl = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s,%s&appid=%s&units=metric",
                    city, country, WEATHER_API_KEY
            );

            // Open a connection to the API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Return the raw JSON response
            return response.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}