package com.example.dermaapp.Constants;

import android.Manifest;
import android.graphics.Color;

public class Constants {
    public static final int GALLERY_REQUEST_CODE = 1;
    public static final int PHOTO_REQUEST_CODE = 0;
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final String[] cameraPermissions ={
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String DJANGO_SITE_BASIC_URL = "http://192.168.1.21:5000/analyze/";
    public static final String DJANGO_SITE_BASIC_SITE_URL = "https://skin-changes.herokuapp.com/analyze/";

}
