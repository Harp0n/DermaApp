package com.example.dermaapp.Controler;


import com.example.dermaapp.Constants.Constants;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IDjangoApi {

    String DJANGO_SITE = Constants.DJANGO_SITE_BASIC_URL;

    @Multipart
    @POST("upload/")
        Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);
}
