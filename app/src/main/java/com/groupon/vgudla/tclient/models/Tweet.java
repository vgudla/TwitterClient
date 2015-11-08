package com.groupon.vgudla.tclient.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pair;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet implements Parcelable {
    private String tweet;
    private long id;
    private String timestamp;
    private User user;
    private int retweetCount;
    private int favoriteCount;
    private String mediaUrl;
    private String textUrl;
    private boolean favorited;
    private boolean retweeted;

    public Tweet(String tweet, long id, String timestamp, User user, int retweetCount,
                 String mediaUrl, String textUrl, int favoriteCount, boolean favorited,
                 boolean retweeted) {
        this.tweet = tweet;
        this.id = id;
        this.timestamp = timestamp;
        this.user = user;
        this.retweetCount = retweetCount;
        this.mediaUrl = mediaUrl;
        this.textUrl = textUrl;
        this.favoriteCount = favoriteCount;
        this.favorited = favorited;
        this.retweeted = retweeted;
    }

    public String getTweet() {
        return tweet;
    }

    public long getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getTextUrl() {
        return textUrl;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public static Tweet fromJSONObject(JSONObject jsonObject) {
        String tweet = null;
        String timestamp = null;
        long id = 0;
        User user = null;
        int retweetCount = 0;
        int favoriteCount = 0;
        Pair<String, String> mediaUrl = null;
        Pair<String, String> textUrl = null;
        boolean favorited = false;
        boolean retweeted = false;
        try {
            tweet = jsonObject.getString("text");
            timestamp = jsonObject.getString("created_at");
            id = jsonObject.getLong("id");
            retweetCount = jsonObject.getInt("retweet_count");
            favoriteCount = jsonObject.getInt("favorite_count");
            user = User.fromJSONObject(jsonObject.getJSONObject("user"));
            favorited = jsonObject.getBoolean("favorited");
            retweeted = jsonObject.getBoolean("retweeted");

            //The tweet text contains tiny urls which need to be resolved by looking up the
            //display urls from the entities object
            if (jsonObject.has("entities")) {
                JSONObject entities = jsonObject.getJSONObject("entities");
                if (entities.has("media")) {
                    JSONArray media = entities.getJSONArray("media");
                    if (media.length() > 0) {
                        JSONObject mediaObject = media.getJSONObject(0);
                        mediaUrl = new Pair(mediaObject.getString("url"), mediaObject.getString("media_url"));
                        //Remove the mediaUrl. We will load an image instead in the detailed view
                        tweet = tweet.replace(mediaUrl.first, "");
                    }
                }
                if (entities.has("urls")) {
                    JSONArray urls = entities.getJSONArray("urls");
                    if (urls.length() > 0) {
                        JSONObject urlObject = urls.getJSONObject(0);
                        textUrl = new Pair(urlObject.getString("url"), urlObject.getString("display_url"));
                        //Replace the twitter tiny url with the actual display url so browser doesnt redirect
                        tweet = tweet.replace(textUrl.first, textUrl.second);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e("Tweet", e.getMessage());
        }
        return new Tweet(tweet, id, timestamp, user, retweetCount,
                         mediaUrl != null ? mediaUrl.second : null,
                         textUrl != null ? textUrl.second : null,
                         favoriteCount, favorited, retweeted);
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Tweet tweet = fromJSONObject(jsonObject);
                tweets.add(tweet);
            } catch (JSONException e) {
                Log.e("Tweet", "Error while parsing json response:" + e.getMessage());
            }
        }
        return tweets;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tweet);
        dest.writeLong(this.id);
        dest.writeString(this.timestamp);
        dest.writeParcelable(this.user, 0);
        dest.writeInt(this.retweetCount);
        dest.writeInt(this.favoriteCount);
        dest.writeString(this.mediaUrl);
        dest.writeString(this.textUrl);
        dest.writeByte(favorited ? (byte) 1 : (byte) 0);
        dest.writeByte(retweeted ? (byte) 1 : (byte) 0);
    }

    private Tweet(Parcel in) {
        this.tweet = in.readString();
        this.id = in.readLong();
        this.timestamp = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.retweetCount = in.readInt();
        this.favoriteCount = in.readInt();
        this.mediaUrl = in.readString();
        this.textUrl = in.readString();
        this.favorited = in.readByte() != 0;
        this.retweeted = in.readByte() != 0;
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
