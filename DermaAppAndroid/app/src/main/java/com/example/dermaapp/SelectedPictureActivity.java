package com.example.dermaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;

import com.example.dermaapp.Controler.ServerControler;
import com.squareup.picasso.Picasso;

import java.io.File;


public class SelectedPictureActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_picture);

        ImageView imageView = findViewById(R.id.imageView2);
        FloatingActionButton buttonSend = findViewById(R.id.floatingActionButtonSend);

        Intent intent = getIntent();


        String filePath = intent.getStringExtra("PhotoPath");
        final File file = new File(filePath);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Picasso.get().load(file).into(imageView);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerControler.getInstance().uploadFoto(getApplicationContext(), file);
                startActivity(new Intent(getApplicationContext(), ResultActivity.class));
            }
        });

    }


}
