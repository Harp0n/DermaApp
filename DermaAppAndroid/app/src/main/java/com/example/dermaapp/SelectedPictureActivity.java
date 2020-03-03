package com.example.dermaapp;

import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;


public class SelectedPictureActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_picture);

        ImageView imageView = findViewById(R.id.imageViewChecked);
        FloatingActionButton buttonSend = findViewById(R.id.floatingActionButtonSend);

        Intent currentIntent = getIntent();

        String filePath = currentIntent.getStringExtra("PhotoPath");
        final File fileToSend = new File(filePath);
        Picasso.get().load(fileToSend).into(imageView);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(getApplicationContext(), ResultActivity.class);
                resultIntent.putExtra("File", fileToSend);

                startActivity(resultIntent);
            }
        });

    }


}
