import java.util.*;

interface WeatherObserver {
    void update(float temp);
}
class PhoneDisplay implements WeatherObserver {
    public void update(float temp) { System.out.println("Phone: " + temp); }
}
class DesktopDisplay implements WeatherObserver {
    public void update(float temp) { System.out.println("Desktop: " + temp); }
}
class WeatherStation {
    private List<WeatherObserver> observers = new ArrayList<>();
    private float temp;
    public void add(WeatherObserver o){observers.add(o);}
    public void setTemp(float t){temp=t;for(WeatherObserver o:observers)o.update(temp);}
}
public class ObserverDemo {
    public static void main(String[] args){
        WeatherStation ws=new WeatherStation();
        ws.add(new PhoneDisplay());
        ws.add(new DesktopDisplay());
        ws.setTemp(25.5f);
    }
}
