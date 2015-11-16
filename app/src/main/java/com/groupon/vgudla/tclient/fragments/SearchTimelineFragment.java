package com.groupon.vgudla.tclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.groupon.vgudla.tclient.TwitterApp;
import com.groupon.vgudla.tclient.models.Tweet;
import com.groupon.vgudla.tclient.util.TwitterErrorMessageHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SearchTimelineFragment extends TweetListFragment {
    private static final String TAG = "SearchTimelineFragment";

    private String query;

    public static SearchTimelineFragment getInstance(String query) {
        SearchTimelineFragment fragment = new SearchTimelineFragment();
        fragment.query = query;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        twitterClient = TwitterApp.getRestClient();
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
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray statuses = null;
                try {
                    statuses = response.getJSONArray("statuses");
                } catch (JSONException e) {
                    Log.e(TAG, "Error while parsing search response" + Log.getStackTraceString(e));
                }
                List<Tweet> tweetList = Tweet.fromJSONArray(statuses);
                if (refresh) {
                    for (int i = tweetList.size()-1; i >= 0; i--) {
                        tweetAdapter.insert(tweetList.get(i), 0);
                    }
                } else {
                    addAll(tweetList);
                }
                swipeContainer.setRefreshing(false);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, "Error while retrieving tweets" + throwable.getStackTrace());
                hideProgressBar();
            }
        };

        twitterClient.searchTweets(responseHandler, query);
        showProgressBar();
    }
}
