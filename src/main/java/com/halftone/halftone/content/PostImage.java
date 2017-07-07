package com.halftone.halftone.content;

import com.google.gson.annotations.SerializedName;

/**
 * Created by david on 29/06/2017.
 */

public class PostImage {
    @SerializedName("post") private Post post;
    @SerializedName("post_bitmap") private String postBitmap;

    public PostImage(){}

    public PostImage(Post post, String postBitmap){
        this.post = post;
        this.postBitmap = postBitmap;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getPostBitmap() {
        return postBitmap;
    }

    public void setPostBitmap(String postBitmap) {
        this.postBitmap = postBitmap;
    }
}
