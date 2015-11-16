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
    @Column(name = "followers_count")
    private int followersCount;
    @Column(name = "following")
    private int following;
    @Column(name = "description")
    private String description;
    @Column(name = "tweet_count")
    private int tweetCount;

    //default constructor needed for Active Android
    public User() {
        super();
    }

    public User(String userProfileUrl, String userName, long userId, String userScreenName,
                int followersCount, int following, String description, int tweetCount) {
        super();
        this.userProfileUrl = userProfileUrl;
        this.userName = userName;
        this.userScreenName = userScreenName;
        this.userId = userId;
        this.followersCount = followersCount;
        this.following = following;
        this.description = description;
        this.tweetCount = tweetCount;
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

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowing() {
        return following;
    }

    public String getDescription() {
        return description;
    }

    public int getTweetCount() {
        return tweetCount;
    }

    public static User fromJSONObject(JSONObject userObject) {
        String userProfileUrl = null;
        String userName = null;
        String userScreenName = null;
        long userId = 0;
        int followerCount = 0;
        int following = 0;
        String description = null;
        int tweetCount = 0;;
        try {
            userProfileUrl = userObject.getString("profile_image_url");
            userName = userObject.getString("name");
            userScreenName = userObject.getString("screen_name");
            userId = userObject.getLong("id");
            followerCount = userObject.getInt("followers_count");
            following = userObject.getInt("friends_count");
            description = userObject.getString("description");
            tweetCount = userObject.getInt("statuses_count");

        } catch (JSONException e) {
            Log.e("User", e.getMessage());
        }
        User user = new User(userProfileUrl, userName, userId, userScreenName,
                followerCount, following, description, tweetCount);
        user.save();
        return user;
    }

    public List<Tweet> getAll() {
        return getMany(Tweet.class, "User");
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
        dest.writeInt(this.followersCount);
        dest.writeInt(this.following);
        dest.writeString(this.description);
        dest.writeInt(this.tweetCount);
    }

    private User(Parcel in) {
        this.userProfileUrl = in.readString();
        this.userName = in.readString();
        this.userId = in.readLong();
        this.userScreenName = in.readString();
        this.followersCount = in.readInt();
        this.following = in.readInt();
        this.description = in.readString();
        this.tweetCount = in.readInt();
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
