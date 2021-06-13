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
    private boolean cancelQuery;
    private long requestStartTime;

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
            executeSearch();
        }

    }

    public void searchNextPage(){
        if(!isQueryExhausted && !isPerformingQuery){}
        mPageNumber++;
        executeSearch();

    }
    public void setViewStateCategoryState(){
        viewState.setValue(ViewState.CATEGORIES);
    }

    private void executeSearch(){
        requestStartTime = System.currentTimeMillis();
        cancelQuery = false;
        isPerformingQuery = true;
        viewState.setValue(ViewState.RECIPE);
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesApi(mQuery,mPageNumber);
        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(!cancelQuery) {
                    if (listResource != null) {
                        recipes.setValue(listResource);
                        if (!cancelQuery)
                            if (listResource.status == Resource.Status.SUCCESS) {
                                Log.e(TAG, "onChanged: Request Time---------------" +  (System.currentTimeMillis() - requestStartTime)/ 1000 + "Seconds");
                                isPerformingQuery = false;
                                if (listResource.data != null && listResource.data.size() == 0) {
                                    Log.e(TAG, "onChanged: Query is exhausted....");
                                    recipes.setValue(new Resource<List<Recipe>>(Resource.Status.ERROR, listResource.data, QUERY_EXHAUSTED));
                                }
                                recipes.removeSource(repositorySource);
                            } else if (listResource.status == Resource.Status.ERROR) {
                                isPerformingQuery = false;
                                recipes.removeSource(repositorySource);
                            }

                    } else {
                        recipes.removeSource(repositorySource);//avoid getting duplicate data
                    }
                }else {
                    recipes.removeSource(repositorySource);
                }

            }

        });

    }

    public void cancelSearchRequest(){
        if(isPerformingQuery){
            cancelQuery = true;
            mPageNumber = 1;
            isPerformingQuery = false;
        }
    }



}















