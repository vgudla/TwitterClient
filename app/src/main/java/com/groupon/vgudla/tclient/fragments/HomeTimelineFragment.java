package com.groupon.vgudla.tclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.groupon.vgudla.tclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.List;

public class HomeTimelineFragment extends TweetListFragment {
    private static final String TAG = "HomeTimelineFragment";
    private static final int TWEET_REQUEST_COUNT = 100;

    private long maxId=-1;
    private long sinceId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getTimeline(false);
        return view;
    }

    protected void getTimeline(final boolean refresh) {
        if (!isNetworkAvailable()) {
            Toast.makeText(getActivity(), "Network not available", Toast.LENGTH_LONG).show();
            addAll(Tweet.getAll());
            return;
        }
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> tweetList = Tweet.fromJSONArray(response);
                if (refresh) {
                    for (int i = tweetList.size()-1; i >= 0; i--) {
                        tweetAdapter.insert(tweetList.get(i), 0);
                    }
                } else {
                    addAll(tweetList);
                }
                swipeContainer.setRefreshing(false);
                sinceId = tweetAdapter.getItem(0).getTweetId();
                maxId = tweetAdapter.getItem(tweetAdapter.getCount()-1).getTweetId();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, "Error while retrieving timeline" + throwable.getStackTrace());
                hideProgressBar();
            }
        };

        //We use the maxId when retrieving older tweets or when a user scrolls down. On refresh,
        //we need to use the sinceId to get latest tweets.
        if (maxId == -1) {
            clearAll();
        } else if (refresh) {
            Tweet newestTweet = tweetAdapter.getItem(0);
            sinceId = newestTweet.getTweetId();
        } else {
            Tweet oldestTweet = tweetAdapter.getItem(tweetAdapter.getCount()-1);
            maxId = oldestTweet.getTweetId();
        }
        twitterClient.getHomeTimeLine(responseHandler, TWEET_REQUEST_COUNT, maxId, sinceId, refresh);
        showProgressBar();
    }
}
