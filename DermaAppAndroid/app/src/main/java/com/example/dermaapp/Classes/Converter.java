package com.example.dermaapp.Classes;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class Converter {
    private Context context;

    public Converter(Context context) {
        this.context = context;
    }

    public File bitToFile(Bitmap photo)
    {
        File f = null;
        try {
            f = new File(context.getCacheDir(), "photo.jpg");
            f.createNewFile();
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return f;
    }
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


}
