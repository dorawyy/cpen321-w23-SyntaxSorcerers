package com.example.frontend.apiwrappers;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Whole class generated with ChatGPT
 */
public class UBCGradesRequest {
    public static final String RequestTag = "Requests";
    private static final String BASE_URL = "https://ubcgrades.com/"; // Replace with your API base URL

    private ApiServiceArray apiServiceArray;
    private ApiServiceObject apiServiceObject;

    /**
     * Generated by ChatGPT
     */
    public UBCGradesRequest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiServiceArray = retrofit.create(ApiServiceArray.class);
        apiServiceObject = retrofit.create(ApiServiceObject.class);
    }

    public void makeGetRequestForJsonArray(String endpoint, final ApiRequestListener<JsonArray> listener) {
        Call<JsonArray> call = apiServiceArray.getData(endpoint);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    JsonArray jsonResponseObject = response.body();
                    Log.d(RequestTag, jsonResponseObject.toString());
                    listener.onApiRequestComplete(jsonResponseObject);
                } else {
                    listener.onApiRequestError("Error response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                listener.onApiRequestError(t.getMessage());
            }
        });
    }

    /**
     * Generated by ChatGPT
     */
    public void makeGetRequestForJsonObject(String endpoint, final ApiRequestListener<JsonObject> listener) throws UnsupportedEncodingException {
        Call<JsonObject> call = apiServiceObject.getData(endpoint);
        Log.d(RequestTag, call.request().url().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponseObject = response.body();
                    Log.d(RequestTag, jsonResponseObject.toString());
                    listener.onApiRequestComplete(jsonResponseObject);
                } else {
                    listener.onApiRequestError("Error response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onApiRequestError(t.getMessage());
            }
        });
    }

    /**
     * Generated by ChatGPT
     */
    public interface ApiRequestListener<T> {
        void onApiRequestComplete(T response);
        void onApiRequestError(String error);
    }

    /**
     * Generated by ChatGPT
     */
    private interface ApiServiceArray {
        @GET
        Call<JsonArray> getData(@Url String endpoint);
    }

    private interface ApiServiceObject {
        @GET
        Call<JsonObject> getData(@Url String endpoint);
    }
}