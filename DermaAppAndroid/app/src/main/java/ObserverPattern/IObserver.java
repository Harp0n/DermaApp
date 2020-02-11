package ObserverPattern;

import android.util.JsonReader;

import org.json.JSONObject;

public interface IObserver {
    void update(String response);
}
