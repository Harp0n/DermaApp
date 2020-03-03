package com.example.dermaapp.Controler;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.example.dermaapp.ObserverPattern.IObservable;
import com.example.dermaapp.ObserverPattern.IObserver;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

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

public class ServerControler implements IObservable {

    private static ServerControler serverControler = null;

    private ArrayList<IObserver> observers = new ArrayList<>();

    private ServerControler()
    { }

    public static ServerControler getInstance()
    {
        if (serverControler == null)
            serverControler = new ServerControler();

        return serverControler;
    }

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
                Log.v("Upload", "Success" + response.body().toString());

                try {
                    String responseString = scoreValueFromJson(response.body().string());
                    notifyObservers(responseString);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally {
                    notifyObservers("Error");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                notifyObservers("Server Failure");
            }
        });
    }

    private String scoreValueFromJson(String responseBodyString)
    {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(responseBodyString);
        JsonObject rootObject = jsonElement.getAsJsonObject();
        return rootObject.get("score").getAsString();
    }

    @Override
    public boolean addObserver(IObserver observer) {
        if(observers.add(observer))
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean removeObserver(IObserver observer) {
        if(observers.remove(observer))
        {
            return true;
        }
        return false;
    }

    @Override
    public void notifyObservers(String response) {
        for(IObserver observer: observers)
        {
            observer.update(response);
        }
    }

}
