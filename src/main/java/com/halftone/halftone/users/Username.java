package com.halftone.halftone.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by david on 05/07/2017.
 */

public class Username {
    @SerializedName("uid") @Expose private String uid;
    @SerializedName("username") @Expose private String username;

    public Username() {
    }

    public Username(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
