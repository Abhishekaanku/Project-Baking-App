package com.exmaple.udacity.bakershop.retro;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetroClient {
    String baseUrl="https://d17h27t6h515a5.cloudfront.net";

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<String> getRecipeJson();

}
