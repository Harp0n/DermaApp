package com.example.dermaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dermaapp.Controler.ServerResponse;


public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ProgressBar progressBar = findViewById(R.id.progressBarResult);
        TextView textViewResult = findViewById(R.id.textViewResult);

        progressBar.setMax(100);
        progressBar.setProgress(0);

        ServerResponse serverResponse = new ServerResponse();
        serverResponse.getTextViewsToUpdate().add(textViewResult);


    }
}
