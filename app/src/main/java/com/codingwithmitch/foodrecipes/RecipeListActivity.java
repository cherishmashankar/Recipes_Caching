package com.codingwithmitch.foodrecipes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.codingwithmitch.foodrecipes.adapters.OnRecipeListener;
import com.codingwithmitch.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.Resource;
import com.codingwithmitch.foodrecipes.util.Testing;
import com.codingwithmitch.foodrecipes.util.VerticalSpacingItemDecorator;
import com.codingwithmitch.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;


public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";
    private static final String QUERY_EXHAUSTED = "No more results.";


    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recipe_list);
        mSearchView = findViewById(R.id.search_view);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        initRecyclerView();
        initSearchView();
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        subscribeObservers();
    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return  Glide.with(this)
                .setDefaultRequestOptions(options);


    }


    private void initRecyclerView(){
        ViewPreloadSizeProvider<String> viewPreloader = new ViewPreloadSizeProvider<String>();
        mAdapter = new RecipeRecyclerAdapter(this, initGlide(), viewPreloader);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewPreloader<String> recyclerViewPreloader = new RecyclerViewPreloader<String>(
                Glide.with(this),
                mAdapter,
                viewPreloader,
                30
        );
        mRecyclerView.addOnScrollListener(recyclerViewPreloader);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!mRecyclerView.canScrollVertically(1)  && mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.RECIPE){
                  mRecipeListViewModel.searchNextPage();
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    private void subscribeObservers(){
        mRecipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                Log.e(TAG, "onChanged: Status " + listResource.status);
                if(listResource.data != null){
                    switch (listResource.status){
                        case LOADING:
                            if(mRecipeListViewModel.getPageNumber() > 1){
                                mAdapter.displayLoading();
                            }else{
                                mAdapter.displayOnlyLoading();
                            }
                            break;
                        case ERROR:
                            Log.e(TAG, "onChanged: cannot refresh the cache." );
                            Log.e(TAG, "onChanged: ERROR message: " + listResource.message );
                            Log.e(TAG, "onChanged: status: ERROR, #recipes: " + listResource.data.size());

                            mAdapter.hideLoading();
                            mAdapter.setRecipes(listResource.data);
                            Toast.makeText(RecipeListActivity.this, listResource.message, Toast.LENGTH_SHORT).show();

                            if(listResource.message.equals(QUERY_EXHAUSTED)){
                                mAdapter.setQueryExhausted();
                            }
                            break;

                        case SUCCESS:
                            Log.d(TAG, "onChanged: cache has been refreshed.");
                            Log.d(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size());
                            mAdapter.hideLoading();
                            mAdapter.setRecipes(listResource.data);
                            break;
                    }
                }

            }
        });
        mRecipeListViewModel.getViewState().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(@Nullable RecipeListViewModel.ViewState viewState) {
                    if(viewState != null){
                        switch(viewState){
                            case RECIPE:
                                Log.e(TAG, "onChanged: Recipe is displayed" );
                                break;
                            case CATEGORIES:
                                Log.e(TAG, "onChanged: Categories");
                                displayCategory();
                                break;

                        }
                    }
            }
        });
    }
    private void searchRecipesApi(String query){
        mRecyclerView.smoothScrollToPosition(0);
        mRecipeListViewModel.searchRecipesApi(query, 1);
        mSearchView.clearFocus();
    }

    private void displayCategory() {
        mAdapter.displaySearchCategories();

    }

    private void initSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchRecipesApi(s);
                  return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", mAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        searchRecipesApi(category);
        
    }

    @Override
    public void onBackPressed() {
        if(mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.CATEGORIES) {
            super.onBackPressed();
        }else {
            mRecipeListViewModel.cancelSearchRequest();
            mRecipeListViewModel.setViewStateCategoryState();
        }
    }
}

















