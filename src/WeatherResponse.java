public class WeatherResponse {
    private WeatherMain main;
    private WeatherCondition[] weather;

    public WeatherMain getMain() {
        return main;
    }

    public WeatherCondition[] getWeather() {
        return weather;
    }
}