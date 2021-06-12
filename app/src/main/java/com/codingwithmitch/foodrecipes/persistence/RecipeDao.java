package com.codingwithmitch.foodrecipes.persistence;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;



import com.codingwithmitch.foodrecipes.models.Recipe;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeDao {
    //Conflict is considered and list of recipes are inserted
    @Insert(onConflict = IGNORE)
    long[] insertRecipes(Recipe... recipes);

    //Conflict is not considered and single recipe is inserted
    @Insert(onConflict = REPLACE)
    void insertRecipe(Recipe recipe);

    @Query("UPDATE recipes SET title = :title, publisher = :publisher, id = :id, imageUrl = :imageUrl, socialUrl = :social_rank " + "WHERE id = :id")
    void updateRecipe(String id, String title, String publisher, float social_rank, String imageUrl);

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%' " +
            "ORDER BY socialUrl DESC LIMIT (:pageNumber * 30)")
    LiveData<List<Recipe>> searchRecipes(String query, int pageNumber);

    @Query("SELECT * FROM recipes WHERE id = :id")
    LiveData<Recipe> getRecipe(String id);



}
