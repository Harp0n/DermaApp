package com.example.dermaapp;

import android.graphics.Color;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.example.dermaapp.Constants.Constants;
import com.example.dermaapp.Controler.ServerControler;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setBackgroundColor(Constants.BACKGROUND_COLOR);

        ServerControler serverControler = new ServerControler();

        serverControler.uploadFoto(this.getBaseContext());
    }
}
