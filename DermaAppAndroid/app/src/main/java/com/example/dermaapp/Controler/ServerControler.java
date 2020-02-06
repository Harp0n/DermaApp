package com.example.dermaapp.Controler;

import android.content.Context;
import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerControler {
    public void uploadFoto(Context context, File imageFile) {

        if(imageFile.exists()) {
            Log.d("works", "works");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IDjangoApi.DJANGO_SITE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        IDjangoApi postApi = retrofit.create(IDjangoApi.class);


        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/data"), imageFile);
        MultipartBody.Part multiPartBody = MultipartBody.Part
                .createFormData("model_pic", imageFile.getName(), requestBody);



        Call<RequestBody> call = postApi.uploadFile(multiPartBody);

        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                Log.d("good", "good");

            }
            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                Log.d("fail", "fail");
            }
        });


    }
}
