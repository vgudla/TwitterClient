package com.groupon.vgudla.tclient.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Parcelable{
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageUrl);
        dest.writeString(this.name);
        dest.writeLong(this.id);
        dest.writeString(this.screenName);
    }

    private User(Parcel in) {
        this.imageUrl = in.readString();
        this.name = in.readString();
        this.id = in.readLong();
        this.screenName = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
