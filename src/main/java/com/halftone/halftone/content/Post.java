package com.halftone.halftone.content;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by david on 24/06/2017.
 */

public class Post {
    @SerializedName("uid") @Expose private String uid;
    @SerializedName("username") @Expose private String username;
    // @SerializedName("name") @Expose private String name;
    // @SerializedName("description") @Expose private String description; // Description will either be custom / taken from google place API json response
    @SerializedName("post_lat") @Expose private PostImage postImage; // Post location lat
    @SerializedName("post_lat") @Expose private String postLat; // Post location lat
    @SerializedName("post_lng") @Expose private String postLng; // Post location lng
    @SerializedName("tag") @Expose private List<String> tags;
    // @SerializedName("star_rating") @Expose private int starRating; // number out of 5
    @SerializedName("post_date") @Expose private Date postDate;

    public Post(){}

    public Post(String uid, /*String username,*/ List<String> tags ) {
        this.uid = uid;
        //this.username = username;
        this.tags = tags;
        // this.postDate = new Date();
    }

    /*public Post(int postId, FirebaseUser user, String name, String description, String postLat, String postLng, List<Tag> tags, int starRating){
        this.postId = postId;
        this.user = user;
        this.name = name;
        this.description = description;
        this.postLat = postLat;
        this.postLng = postLng;
        this.tags = tags;
        this.starRating = starRating;
        this.postDate = new Date();
    }*/

    /*public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }*/

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid(){
        return this.uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }
    /*public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }*/

    public String getPostLat() {
        return postLat;
    }

    public void setPostLat(String postLat) {
        this.postLat = postLat;
    }

    public String getPostLng() {
        return postLng;
    }

    public void setPostLng(String postLng) {
        this.postLng = postLng;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        for( String tag : tags ){
            this.tags.add( tag );
        }
    }

    /*public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }*/

    public Date getTips() {
        return postDate;
    }

    public void setTips(String tips) {
        this.postDate = postDate;
    }
}
