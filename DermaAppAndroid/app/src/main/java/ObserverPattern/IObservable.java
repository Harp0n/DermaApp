package ObserverPattern;

import org.json.JSONObject;

public interface IObservable {
    boolean addObserver(IObserver observer);

    boolean removeObserver(IObserver observer);

    void notifyObservers(String response);
}
