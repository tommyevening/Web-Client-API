package done;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        Service s = new Service("Poland");
        String weatherInfo = s.getWeather("Kielce");

        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();

        AppGUI.initService(s);
        Application.launch(AppGUI.class, args);
    }
}