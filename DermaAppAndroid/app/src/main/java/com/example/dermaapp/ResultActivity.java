package com.example.dermaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dermaapp.Controler.ServerControler;
import com.example.dermaapp.Controler.ServerResponse;

import java.io.File;


public class ResultActivity extends AppCompatActivity {
    private static TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File file = (File) getIntent().getSerializableExtra("File");
        final ServerResponse serverResponse = new ServerResponse();
        ServerControler.getInstance().addObserver(serverResponse);

        ServerControler.getInstance().uploadFoto(getApplicationContext(), file);

        setContentView(R.layout.activity_result);


        ProgressBar progressBar = findViewById(R.id.progressBarResult);
        textViewResult = findViewById(R.id.textViewResult);


        progressBar.setMax(100);
        progressBar.setProgress(0);


    }

    public static void receiveResponse(String massage)
    {
        textViewResult.setText(massage);
    }
}
