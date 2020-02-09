package com.example.dermaapp.Controler;

import android.content.Context;
import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerControler {

    private Retrofit createInstanceRetrofit()
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IDjangoApi.DJANGO_SITE)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
    public void uploadFoto(Context context, File imageFile) {

        IDjangoApi postApi = createInstanceRetrofit().create(IDjangoApi.class);

        final RequestBody requestBody =  RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);

        MultipartBody.Part part = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestBody);

        Call call = postApi.uploadImage(part);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success" + response.message()  );
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
}
