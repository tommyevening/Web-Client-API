package done;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppGUI extends Application {
    private static Service weatherService;
    private WebView webView;
    private WebEngine webEngine;

    public static void initService(Service service) {
        weatherService = service;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather and Currency Information");

        // GRID
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Countries combobox
        ComboBox<String> countryComboBox = new ComboBox<>();


        //Countries from Locale
        List<String> countries = new ArrayList<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            String country = locale.getDisplayCountry(Locale.ENGLISH);
            if (!country.isEmpty() && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);
        countryComboBox.getItems().addAll(countries);
        countryComboBox.setPromptText("Select country");
        countryComboBox.setPrefWidth(200);





        //Inputs
        TextField cityField = new TextField();
        cityField.setPromptText("Enter city");
        cityField.setPrefWidth(200);

        TextField currencyField = new TextField();
        currencyField.setPromptText("Enter target currency");
        currencyField.setPrefWidth(200);
        //---------------------------------------------------------------------





        // Outputs
        TextArea weatherResult = new TextArea();
        weatherResult.setEditable(false);
        weatherResult.setPrefRowCount(4);
        weatherResult.setWrapText(true);
        weatherResult.setPrefWidth(400);

        Label exchangeRateLabel = new Label();
        Label nbpRateLabel = new Label();

        //Wikipedia
        webView = new WebView();
        webEngine = webView.getEngine();
        webView.setPrefSize(800, 400);

        //-----------------------------------------------------




        // Buttons
        Button weatherButton = new Button("Get Weather");
        Button rateButton = new Button("Get Exchange Rate");
        Button nbpButton = new Button("Get NBP Rate");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(weatherButton, rateButton, nbpButton);
        buttonBox.setAlignment(Pos.CENTER);

        weatherButton.setOnAction(e -> {
            try {
                String city = cityField.getText().trim();
                String country = countryComboBox.getValue();
                if (country == null || city.isEmpty()) {
                    weatherResult.setText("Please select country and enter city name"); // if country is not selected
                    return;
                }
                weatherService = new Service(country);
                String weatherJson = weatherService.getWeather(city);



                //JSON
                JsonObject weatherObject = new Gson().fromJson(weatherJson, JsonObject.class);
                JsonObject main = weatherObject.getAsJsonObject("main");
                JsonObject weather = weatherObject.getAsJsonArray("weather").get(0).getAsJsonObject();

                String formattedWeather = String.format(
                        "City: %s\nTemperature: %.2f°C\nFeels Like: %.2f°C\nHumidity: %d%%\nPressure: %d hPa\nConditions: %s",
                        weatherObject.get("name").getAsString(),
                        main.get("temp").getAsDouble(),
                        main.get("feels_like").getAsDouble(),
                        main.get("humidity").getAsInt(),
                        main.get("pressure").getAsInt(),
                        weather.get("description").getAsString()
                );

                weatherResult.setText(formattedWeather);

                // Load Wikipedia page
                String wikiUrl = "https://en.wikipedia.org/wiki/" + city;
                webEngine.load(wikiUrl);
            } catch (Exception ex) {
                weatherResult.setText("Error: " + ex.getMessage());
            }
        });



        // Rate button action
        rateButton.setOnAction(e -> {
            try {
                String targetCurrency = currencyField.getText().trim().toUpperCase();
                if (targetCurrency.isEmpty()) {
                    exchangeRateLabel.setText("Please enter target currency");
                    return;
                }
                Double rate = weatherService.getRateFor(targetCurrency);
                if (rate != null) {
                    exchangeRateLabel.setText(String.format("Exchange rate: 1 %s = %.4f %s",
                            weatherService.getCurrencyCode(), rate, targetCurrency));
                } else {
                    exchangeRateLabel.setText("Could not fetch exchange rate");
                }
            } catch (Exception ex) {
                exchangeRateLabel.setText("Error: " + ex.getMessage());
            }
        });





        // NBP button action
        nbpButton.setOnAction(e -> {
            try {
                Double rate = weatherService.getNBPRate();
                if (rate != null) {
                    nbpRateLabel.setText(String.format("NBP rate: %.4f PLN", rate));
                } else {
                    nbpRateLabel.setText("Could not fetch NBP rate");
                }
            } catch (Exception ex) {
                nbpRateLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Adding components to grid
        int row = 0;
        grid.add(new Label("Country:"), 0, row);
        grid.add(countryComboBox, 1, row);

        row++;
        grid.add(new Label("City:"), 0, row);
        grid.add(cityField, 1, row);

        row++;
        grid.add(new Label("Target Currency:"), 0, row);
        grid.add(currencyField, 1, row);

        row++;
        grid.add(buttonBox, 0, row, 2, 1);

        row++;
        grid.add(new Label("Weather Information:"), 0, row, 2, 1);

        row++;
        grid.add(weatherResult, 0, row, 2, 1);

        row++;
        grid.add(exchangeRateLabel, 0, row, 2, 1);

        row++;
        grid.add(nbpRateLabel, 0, row, 2, 1);

        row++;
        grid.add(webView, 0, row, 2, 1);




        // Create scene and show stage
        Scene scene = new Scene(grid, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}