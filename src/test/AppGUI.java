package test;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class AppGUI extends Application {
    private static Service weatherService;
    private WebView webView;
    private WebEngine webEngine;

    public static void initService(Service service) {
        weatherService = service;
    }

    @Override
    public void init() throws Exception {
        super.init();
        // Any additional initialization if needed
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather and Currency Information");

        // Initialize WebView
        webView = new WebView();
        webEngine = webView.getEngine();
        webView.setPrefSize(800, 400);

        // Create main layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Left side - Weather information
        VBox weatherBox = new VBox(10);
        weatherBox.setPadding(new Insets(10));
        weatherBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");

        // Input fields
        TextField cityField = new TextField();
        cityField.setPromptText("Enter city");

        TextField countryField = new TextField();
        countryField.setPromptText("Enter country");

        // Results area
        TextArea weatherResult = new TextArea();
        weatherResult.setEditable(false);
        weatherResult.setPrefRowCount(5);
        weatherResult.setWrapText(true);

        // Right side - Currency information
        VBox currencyBox = new VBox(10);
        currencyBox.setPadding(new Insets(10));
        currencyBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");

        TextArea currencyResult = new TextArea();
        currencyResult.setEditable(false);
        currencyResult.setPrefRowCount(5);
        currencyResult.setWrapText(true);

        // Search button
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            try {
                String city = cityField.getText().trim();
                String country = countryField.getText().trim();

                // Get weather information
                String weatherInfo = weatherService.getWeather(city);
                weatherResult.setText(weatherInfo);

                // Load Wikipedia page
                String wikiUrl = "https://en.wikipedia.org/wiki/" + city;
                webEngine.load(wikiUrl);

                // Get currency rates (if implemented)
                if (weatherService != null) {
                    Double usdRate = weatherService.getRateFor("USD");
                    Double nbpRate = weatherService.getNBPRate();

                    StringBuilder currencyInfo = new StringBuilder();
                    if (usdRate != null) {
                        currencyInfo.append("USD Rate: ").append(usdRate).append("\n");
                    }
                    if (nbpRate != null) {
                        currencyInfo.append("NBP Rate: ").append(nbpRate);
                    }
                    currencyResult.setText(currencyInfo.toString());
                }
            } catch (Exception ex) {
                weatherResult.setText("Error: " + ex.getMessage());
            }
        });

        // Add components to weather box
        weatherBox.getChildren().addAll(
                new Label("City:"),
                cityField,
                new Label("Country:"),
                countryField,
                searchButton,
                new Label("Weather Information:"),
                weatherResult
        );

        // Add components to currency box
        currencyBox.getChildren().addAll(
                new Label("Currency Exchange Rates:"),
                currencyResult
        );

        // Add boxes to grid
        grid.add(weatherBox, 0, 0);
        grid.add(currencyBox, 1, 0);

        // Add WebView below both boxes
        grid.add(webView, 0, 1, 2, 1); // spans 2 columns

        Scene scene = new Scene(grid, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



}