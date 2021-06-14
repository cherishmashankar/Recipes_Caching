package com.codingwithmitch.foodrecipes.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.persistence.RecipeDao;
import com.codingwithmitch.foodrecipes.persistence.RecipeDatabase;
import com.codingwithmitch.foodrecipes.requests.ServiceGenerator;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;
import com.codingwithmitch.foodrecipes.util.NetworkBoundResource;
import com.codingwithmitch.foodrecipes.util.Resource;

import java.util.List;

public class RecipeRepository {
    private static final String TAG = "RecipeRepository";
    private static RecipeRepository instance;
    private RecipeDao recipeDao;

    public static RecipeRepository getInstance(Context context){
        if(instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    public RecipeRepository(Context context) {
       recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
    }

    public LiveData<Resource<List<Recipe>>> searchRecipesApi(final String query, final int pageNumber){
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance()){
            //save network data in db
            @Override
            protected void saveCallResult(@NonNull RecipeSearchResponse item) {
                if(item.getRecipes() != null){
                    Recipe[] recipes = new Recipe[item.getRecipes().size()];
                    int index = 0;
                    for(long rowId: recipeDao.insertRecipes((Recipe[]) (item.getRecipes().toArray(recipes)))){
                        if(rowId == -1){
                            Log.d(TAG, "saveCallResult: There is a conflict");
                            // if the recipe already exists... I don't want to set the ingredients or timestamp b/c
                            // they will be erased
                            recipeDao.updateRecipe(
                                    //String id, String title, String publisher, float social_rank, String imageUrl
                                    recipes[index].getId(),
                                    recipes[index].getTitle(),
                                    recipes[index].getPublisher(),
                                    recipes[index].getSocialUrl(),
                                    recipes[index].getImageUrl()
                            );
                        }
                        index++;

                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {
                return recipeDao.searchRecipes(query,pageNumber);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                return ServiceGenerator.getRecipeApi().searchRecipe(query, String.valueOf(pageNumber));
            }
        }.getAsLiveData();

    }

    public LiveData<Resource<Recipe>> searchRecipeApi(final String recipeId){
        return new NetworkBoundResource<Recipe, RecipeResponse>(AppExecutors.getInstance()){

            @Override
            protected void saveCallResult(@NonNull RecipeResponse item) {
                if(item.getRecipe() != null){
                    item.getRecipe().setTimeStamp((int)(System.currentTimeMillis()/1000));
                    item.getRecipe().setId(item.getRecipe().getRecipe_id());
                 //   Log.e(TAG, "saveCallResult: ------------**************" + item.getRecipe().getId() + "   " +item.getRecipe().getRecipe_id() );
                    recipeDao.insertRecipe(item.getRecipe());
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable Recipe data) {
                Log.d(TAG, "shouldFetch: recipe: " + data.toString());
                int currentTime = (int)(System.currentTimeMillis() / 1000);
                Log.d(TAG, "shouldFetch: current time: " + currentTime);
                int lastRefresh = data.getTimeStamp();
                Log.d(TAG, "shouldFetch: last refresh: " + lastRefresh);
                Log.d(TAG, "shouldFetch: it's been " + ((currentTime - lastRefresh) / (60 * 60 * 24)) +
                        " days since this recipe was refreshed. 30 days must elapse before refreshing. ");
                if((currentTime - data.getTimeStamp()) >= Constants.RECIPE_REFRESH_TIME){
                    Log.d(TAG, "shouldFetch: SHOULD REFRESH RECIPE?! " + true);
                    return true;
                }
                Log.d(TAG, "shouldFetch: SHOULD REFRESH RECIPE?! " + false);
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Recipe> loadFromDb() {
                return recipeDao.getRecipe(recipeId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeResponse>> createCall() {
                return ServiceGenerator.getRecipeApi().getRecipe(recipeId);
            }
        }.getAsLiveData();
    }
}
