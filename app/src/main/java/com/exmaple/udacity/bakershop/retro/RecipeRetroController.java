package com.exmaple.udacity.bakershop.retro;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RecipeRetroController {
    private static Retrofit retrofit=new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(RetroClient.baseUrl)
            .build();

    private static RetroClient retroClient=retrofit.create(RetroClient.class);

    public static void loadRecipeJson(Callback<String> callback) {
        Call<String> recipeJsonCall=retroClient.getRecipeJson();
        recipeJsonCall.enqueue(callback);
    }
}
