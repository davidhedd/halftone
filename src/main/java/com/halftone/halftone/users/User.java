package com.halftone.halftone.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by david on 02/07/2017.
 */

public class User {
    @SerializedName("post_id") @Expose private String userId;
    @SerializedName("post_id") @Expose private String username;

    public User(){}

    public User(String userId, String username){
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
