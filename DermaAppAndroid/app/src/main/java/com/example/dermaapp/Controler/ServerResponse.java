package com.example.dermaapp.Controler;

import android.widget.TextView;

import com.example.dermaapp.MainActivity;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import ObserverPattern.IObserver;

public class ServerResponse implements IObserver {

    private String response;
    private ArrayList<TextView> textViewsToUpdate = new ArrayList<TextView>();
    public ServerResponse() { }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ArrayList<TextView> getTextViewsToUpdate() {
        return textViewsToUpdate;
    }

    public void setTextViewsToUpdate(ArrayList<TextView> textViewsToUpdate) {
        this.textViewsToUpdate = textViewsToUpdate;
    }

    private void updateTextViews() {
        for(TextView textView: textViewsToUpdate)
        {
            MainActivity.changeTextViews("to dziala");
            textView.setText(response);
        }
    }

    @Override
    public void update(String response) {
        this.response = response;
        updateTextViews();
    }
}
