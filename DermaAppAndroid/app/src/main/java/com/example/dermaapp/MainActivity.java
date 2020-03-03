package com.example.dermaapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dermaapp.Constants.Constants;
import com.example.dermaapp.Controler.ServerControler;
import com.example.dermaapp.Controler.ServerResponse;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private String currentPhotoTakenPath;
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivity = MainActivity.this;

        final Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
        final ImageButton buttonGallery = findViewById(R.id.buttonGalleryPicture);
        final ImageButton buttonPhoto = findViewById(R.id.buttonCameraPhoto);

        TextView tx = findViewById(R.id.textViewCheckSkin);
        Typeface lemonJellyFont = ResourcesCompat.getFont(thisActivity, R.font.lemon_jelly);

        tx.setTypeface(lemonJellyFont);

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // Scheduled runnable tasks to shake buttons
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                buttonGallery.startAnimation(shake);
            }
        }, 0, 4, TimeUnit.SECONDS);

        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                buttonPhoto.startAnimation(shake);
            }
        }, 2, 4, TimeUnit.SECONDS);


        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                // checking for READ_EXTERNAL permission
                if (ContextCompat.checkSelfPermission(thisActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(thisActivity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.REQUEST_EXTERNAL_STORAGE);

                }
                else
                    pickPhoto();
            }
        });

        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for CAMERA and WRITE_EXTERNAL permissions
                if (!hasPermissions(thisActivity, Constants.cameraPermissions)) {

                    ActivityCompat.requestPermissions(thisActivity,
                            Constants.cameraPermissions,
                            Constants.PHOTO_REQUEST_CODE);

//                    ActivityCompat.requestPermissions(thisActivity,
//                            new String[]{Manifest.permission.CAMERA},
//                            Constants.PHOTO_REQUEST_CODE);

                }
                else
                    takeAPhoto();
            }
        });
    }

    // called after photo took by camera or picked from gallery
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode == Activity.RESULT_OK)
            switch(requestCode){
                case Constants.GALLERY_REQUEST_CODE:
                    // Let's read picked image data - its URI
                    Uri pickedImage = imageReturnedIntent.getData();
                    String imagePath = getRealPathFromURI(pickedImage);
                    Intent intent = new Intent(this, SelectedPictureActivity.class);
                    intent.putExtra("PhotoPath", imagePath);
                    startActivity(intent);
                    break;
                case Constants.PHOTO_REQUEST_CODE:

                    Intent intent1 = new Intent(this, SelectedPictureActivity.class);
                    intent1.putExtra("PhotoPath", currentPhotoTakenPath);
                    startActivity(intent1);
                    break;
            }

    }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case Constants.REQUEST_EXTERNAL_STORAGE: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("permission granted");
                        pickPhoto();
                    } else {
                        System.out.println("permission denied");
                        Toast.makeText(this, "You must accept all permissions!", Toast.LENGTH_LONG).show();                    }
                    break;

                }
                case Constants.PHOTO_REQUEST_CODE:{
                    if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && hasPermissions(thisActivity, Constants.cameraPermissions)){
                        System.out.println("permission granted");
                        takeAPhoto();
                    } else {
                        System.out.println("permission denied");
                        Toast.makeText(this, "You must accept all permissions!", Toast.LENGTH_LONG).show();
                    }
                    break;

                }
            }
        }


        public void pickPhoto(){
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhoto.setDataAndType(Uri.EMPTY,"image/*");
            startActivityForResult(pickPhoto, Constants.GALLERY_REQUEST_CODE);
        }


    public void takeAPhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            assert photoFile != null;
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.dermaapp.provider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, Constants.PHOTO_REQUEST_CODE);

        }
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //String imageFileName = timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                timeStamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoTakenPath = image.getAbsolutePath();

        return image;
    }

    public static void changeTextViews(String response)
    {
        Log.d("Observer", "Works");
    }

    // check whether all permissions has been granted by user
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}



