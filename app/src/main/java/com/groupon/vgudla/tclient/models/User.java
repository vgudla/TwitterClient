package com.groupon.vgudla.tclient.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Table(name = "Users")
public class User extends Model implements Parcelable{
    @Column(name = "user_profile_url")
    private String userProfileUrl;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "user_screen_name")
    private String userScreenName;

    //default constructor needed for Active Android
    public User() {
        super();
    }

    public User(String userProfileUrl, String userName, long userId, String userScreenName) {
        super();
        this.userProfileUrl = userProfileUrl;
        this.userName = userName;
        this.userScreenName = userScreenName;
        this.userId = userId;
    }


    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public String getUserName() {
        return userName;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public static User fromJSONObject(JSONObject userObject) {
        String userProfileUrl = null;
        String userName = null;
        String userScreenName = null;
        long userId = 0;
        try {
            userProfileUrl = userObject.getString("profile_image_url");
            userName = userObject.getString("name");
            userScreenName = userObject.getString("screen_name");
            userId = userObject.getLong("id");

        } catch (JSONException e) {
            Log.e("User", e.getMessage());
        }
        User user = new User(userProfileUrl, userName, userId, userScreenName);
        user.save();
        return user;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userProfileUrl);
        dest.writeString(this.userName);
        dest.writeLong(this.userId);
        dest.writeString(this.userScreenName);
    }

    private User(Parcel in) {
        this.userProfileUrl = in.readString();
        this.userName = in.readString();
        this.userId = in.readLong();
        this.userScreenName = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public List<Tweet> getAll() {
        return getMany(Tweet.class, "User");
    }
}
