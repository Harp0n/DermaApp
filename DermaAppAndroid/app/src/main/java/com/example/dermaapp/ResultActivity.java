package com.example.dermaapp;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import static android.support.v4.graphics.ColorUtils.HSLToColor;

import com.example.dermaapp.Controler.ServerControler;
import com.example.dermaapp.Controler.ServerResponse;

import java.io.File;


public class ResultActivity extends AppCompatActivity {
    private static TextView textViewResult;
    private static ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File file = (File) getIntent().getSerializableExtra("File");
        final ServerResponse serverResponse = new ServerResponse();
        ServerControler.getInstance().addObserver(serverResponse);

        ServerControler.getInstance().uploadFoto(getApplicationContext(), file);

        setContentView(R.layout.activity_result);


        progressBar = findViewById(R.id.progressBarResult);
        textViewResult = findViewById(R.id.textViewResult);
        Button  backButton = findViewById(R.id.buttonBack);


        progressBar.setMax(100);
        //progressBar.setProgress(0);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @SuppressLint("SetTextI18n")
    public static void receiveResponse(String massage)
    {
        Integer response = ((int) (Float.valueOf(massage)*100));
        textViewResult.setText(response.toString()+"%");
        ObjectAnimator.ofInt(progressBar, "progress", response)
                .setDuration(1000)
                .start();

    }
}
