package com.groupon.vgudla.tclient.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable{
    private String imageUrl;
    private String name;
    private long id;
    private String screenName;

    public User(String imageUrl, String name, long id, String screenName) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.screenName = screenName;
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }

    public static User fromJSONObject(JSONObject jsonObject) {
        String imageUrl = null;
        String name = null;
        String screenName = null;
        long id = 0;
        try {
            imageUrl = jsonObject.getString("profile_image_url");
            name = jsonObject.getString("name");
            screenName = jsonObject.getString("screen_name");
            id = jsonObject.getLong("id");
        } catch (JSONException e) {
            Log.e("User", e.getMessage());
        }
        return new User(imageUrl, name, id, screenName);
    }
}
