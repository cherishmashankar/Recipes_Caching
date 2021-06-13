package com.codingwithmitch.foodrecipes.viewmodels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Resource;

import org.w3c.dom.ls.LSInput;

import java.util.GregorianCalendar;
import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";
    private static final String QUERY_EXHAUSTED = "No more results.";

    public enum ViewState {CATEGORIES,RECIPE};
    private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();
    private RecipeRepository recipeRepository;


    //query extras
    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
    private int mPageNumber;
    private String mQuery;

    public int getPageNumber() {
        return mPageNumber;
    }

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipeRepository = RecipeRepository.getInstance(application);
        init();
    }

    public void init(){
        if(viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
    }

    public LiveData<ViewState> getViewState(){
        return viewState;
    }

    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return recipes;
    }

    public void searchRecipesApi(String query, int pageNumber){
        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            mPageNumber = pageNumber;
            mQuery = query;
            isQueryExhausted = false;
            executeSearch(query, pageNumber);
        }

    }

    private void executeSearch(String query, int pageNumber){
        isPerformingQuery = true;
        viewState.setValue(ViewState.RECIPE);
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesApi(query,pageNumber);
        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    recipes.setValue(listResource);
                    if(listResource.status == Resource.Status.SUCCESS){
                        isPerformingQuery = false;
                        if(listResource.data != null && listResource.data.size() == 0){
                            Log.e(TAG, "onChanged: Query is exhausted...." );
                            recipes.setValue(new Resource<List<Recipe>>(Resource.Status.ERROR,listResource.data, QUERY_EXHAUSTED));
                        }
                        recipes.removeSource(repositorySource);
                    }else if(listResource.status == Resource.Status.ERROR){
                        isPerformingQuery = false;
                        recipes.removeSource(repositorySource);
                    }

                }else{
                    recipes.removeSource(repositorySource);//avoid getting duplicate data
                }


                recipes.setValue(listResource);
            }
        });

    }



}















