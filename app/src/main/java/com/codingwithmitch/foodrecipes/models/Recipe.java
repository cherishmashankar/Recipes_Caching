package com.codingwithmitch.foodrecipes.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Arrays;

@Entity(tableName = "recipes")
public class Recipe implements Parcelable{

    @ColumnInfo(name = "recipe_id")
    private String recipe_id; // get query

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "publisher")
    private String publisher;

    @ColumnInfo(name = "image_url")
    private String image_url; // get query

    @ColumnInfo(name = "socialUrl")
    private float socialUrl;

    @ColumnInfo(name = "ingredients")
    private String[] ingredients;

    @ColumnInfo(name = "imageUrl")
    private String imageUrl; //search query

    @PrimaryKey
    @NonNull
    private String id;     //search query

    @ColumnInfo(name = "timeStamp")
    private int timeStamp;

    public Recipe(String recipe_id, String title, String publisher, String image_url, float socialUrl, String[] ingredients, String imageUrl, @NonNull String id, int timeStamp) {
        this.recipe_id = recipe_id;
        this.title = title;
        this.publisher = publisher;
        this.image_url = image_url;
        this.socialUrl = socialUrl;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.id = id;
        this.timeStamp = timeStamp;
    }

    @Ignore
    public Recipe() {
    }

    protected Recipe(Parcel in) {
        recipe_id = in.readString();
        title = in.readString();
        publisher = in.readString();
        image_url = in.readString();
        socialUrl = in.readFloat();
        ingredients = in.createStringArray();
        imageUrl = in.readString();
        id = in.readString();
        timeStamp = in.readInt();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public float getSocialUrl() {
        return socialUrl;
    }

    public void setSocial_rank(float social_rank) {
        this.socialUrl = social_rank;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipe_id);
        dest.writeString(title);
        dest.writeString(publisher);
        dest.writeString(image_url);
        dest.writeFloat(socialUrl);
        dest.writeStringArray(ingredients);
        dest.writeString(imageUrl);
        dest.writeString(id);
        dest.writeInt(timeStamp);
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "recipe_id='" + recipe_id + '\'' +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", image_url='" + image_url + '\'' +
                ", socialUrl=" + socialUrl +
                ", ingredients=" + Arrays.toString(ingredients) +
                ", imageUrl='" + imageUrl + '\'' +
                ", id='" + id + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}














