package com.codingwithmitch.foodrecipes.requests;

import com.codingwithmitch.foodrecipes.requests.responses.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {

    //Search Ex: https://recipesapi.herokuapp.com/api/v2/recipes?q=Indian&page=1

    // SEARCH
    @GET("api/v2/recipes")
    Call<RecipeSearchResponse> searchRecipe(
            @Query("q") String query,
            @Query("page") String page
    );

    // GET RECIPE REQUEST Ex: https://recipesapi.herokuapp.com/api/get?rId=49795
    @GET("api/get")
    Call<RecipeResponse> getRecipe(
            @Query("rId") String recipe_id
    );
}
