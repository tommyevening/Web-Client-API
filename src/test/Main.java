/**
 *
 *  @author Wieczorek Tomasz S27161
 *
 */

package test;


import javafx.application.Application;
import zad1.AppGUI;

public class Main {
  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();

//    done.AppGUI.initService(s);
//    Application.launch(done.AppGUI.class, args);
  }
}
