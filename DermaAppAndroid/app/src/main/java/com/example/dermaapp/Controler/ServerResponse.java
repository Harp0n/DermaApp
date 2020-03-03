package com.example.dermaapp.Controler;

import android.nfc.Tag;
import android.util.Log;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import com.example.dermaapp.ObserverPattern.IObserver;
import com.example.dermaapp.ResultActivity;

import org.w3c.dom.Text;

public class ServerResponse implements IObserver, Serializable {

    private String response;

    public ServerResponse() { }

    @Override
    public void update(String response) {
        this.response = response;
        ResultActivity.receiveResponse(this.response);
    }

    public String getResponse() {
        return response;
    }
}
