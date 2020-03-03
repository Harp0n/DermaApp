package com.example.dermaapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import static android.support.v4.graphics.ColorUtils.HSLToColor;

import com.example.dermaapp.Controler.ServerResponse;


public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ProgressBar progressBar = findViewById(R.id.progressBarResult);
        TextView textViewResult = findViewById(R.id.textViewResult);

        progressBar.setMax(100);
        //progressBar.setProgress(0);

        ObjectAnimator.ofInt(progressBar, "progress", 100)
                .setDuration(1000)
                .start();

        ServerResponse serverResponse = new ServerResponse();
        serverResponse.getTextViewsToUpdate().add(textViewResult);


    }
}
