package com.groupon.vgudla.tclient.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tweet implements Serializable {
    private String tweet;
    private long id;
    private String timestamp;
    private User user;
    private int retweetCount;

    public Tweet(String tweet, long id, String timestamp, User user, int retweetCount) {
        this.tweet = tweet;
        this.id = id;
        this.timestamp = timestamp;
        this.user = user;
        this.retweetCount = retweetCount;
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

    public static Tweet fromJSONObject(JSONObject jsonObject) {
        String tweet = null;
        String timestamp = null;
        long id = 0;
        User user = null;
        int retweetCount = 0;
        try {
            tweet = jsonObject.getString("text");
            timestamp = jsonObject.getString("created_at");
            id = jsonObject.getLong("id");
            retweetCount = jsonObject.getInt("retweet_count");
            user = User.fromJSONObject(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            Log.e("Tweet", e.getMessage());
        }
        return new Tweet(tweet, id, timestamp, user, retweetCount);
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
}
