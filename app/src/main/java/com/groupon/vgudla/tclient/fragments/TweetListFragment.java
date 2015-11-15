package com.groupon.vgudla.tclient.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.groupon.vgudla.tclient.R;
import com.groupon.vgudla.tclient.TwitterApp;
import com.groupon.vgudla.tclient.activity.TweetActivity;
import com.groupon.vgudla.tclient.adapters.TweetAdapter;
import com.groupon.vgudla.tclient.listeners.EndlessScrollListener;
import com.groupon.vgudla.tclient.listeners.OnComposeListener;
import com.groupon.vgudla.tclient.models.Tweet;
import com.groupon.vgudla.tclient.util.TwitterClient;
import com.groupon.vgudla.tclient.util.TwitterErrorMessageHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TweetListFragment extends Fragment implements OnComposeListener {
    private static final String TAG = "TweetListFragment";
    private ListView lvTimeline;
    private List<Tweet> tweets = new ArrayList<>();
    protected TweetAdapter tweetAdapter;
    protected SwipeRefreshLayout swipeContainer;
    protected ProgressBar progressBarFooter;
    protected TwitterClient twitterClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        twitterClient = TwitterApp.getRestClient();
        //setHasOptionsMenu(true);
        tweetAdapter = new TweetAdapter(getActivity(), tweets);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweetlist, container, false);
        setupTimelineView(view);
        setupSwipeContainer(view);
        return view;
    }

    public void addAll(List<Tweet> tweets) {
        tweetAdapter.addAll(tweets);
    }

    public void clearAll() {
        tweetAdapter.clear();
    }

    private void setupSwipeContainer(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTweetsList(true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupTimelineView(View view) {
        lvTimeline = (ListView) view.findViewById(R.id.lvTimeline);
        setupFooterProgressBar(lvTimeline);

        lvTimeline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = (Tweet) parent.getItemAtPosition(position);
                Intent tweetIntent = new Intent(getActivity(), TweetActivity.class);
                tweetIntent.putExtra("tweet", tweet);
                startActivity(tweetIntent);
            }
        });

        lvTimeline.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                refreshTweetsList(false);
                return true;
            }
        });
        lvTimeline.setItemsCanFocus(true);
    }

    protected void refreshTweetsList(boolean refresh) {
        getTimeline(refresh);
    }

    // Adds footer to the list default hidden progress
    protected void setupFooterProgressBar(ListView lvItems) {
        // Inflate the footer
        View footer = getActivity().getLayoutInflater().inflate(R.layout.footer_progress, null);
        // Find the progressbar within footer
        progressBarFooter = (ProgressBar) footer.findViewById(R.id.pbFooterLoading);
        // Add footer to ListView before setting adapter
        lvItems.addFooterView(footer);
        // Set the adapter AFTER adding footer
        lvTimeline.setAdapter(tweetAdapter);
    }

    // Show progress
    protected void showProgressBar() {
        progressBarFooter.setVisibility(View.VISIBLE);
    }

    // Hide progress
    protected void hideProgressBar() {
        progressBarFooter.setVisibility(View.GONE);
    }

    //Check to see if network is available before making external service calls
    protected Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onFinishCompose(String inputText) {
        twitterClient.postTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                getTimeline(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.e(TAG, TwitterErrorMessageHelper.getErrorMessage(errorResponse));
                Log.e(TAG, Log.getStackTraceString(throwable));
            }
        }, inputText);
    }

    protected void getTimeline(boolean refresh) {

    }
}
