package com.example.dermaapp.Controler;

import android.widget.TextView;

import java.util.ArrayList;

import com.example.dermaapp.ObserverPattern.IObserver;

public class ServerResponse implements IObserver {

    private String response;
    private ArrayList<TextView> textViewsToUpdate = new ArrayList<>();

    public ServerResponse() { }

    public ArrayList<TextView> getTextViewsToUpdate() {
        return textViewsToUpdate;
    }

    public void setTextViewsToUpdate(ArrayList<TextView> textViewsToUpdate) {
        this.textViewsToUpdate = textViewsToUpdate;
    }

    private void updateTextViews() {
        for(TextView textView: textViewsToUpdate)
        {
            textView.setText(response);
        }
    }

    @Override
    public void update(String response) {
        this.response = response;
        updateTextViews();
    }
}
