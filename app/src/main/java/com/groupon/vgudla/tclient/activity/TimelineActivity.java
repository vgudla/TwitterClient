package com.groupon.vgudla.tclient.activity;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.groupon.vgudla.tclient.R;
import com.groupon.vgudla.tclient.TwitterApp;
import com.groupon.vgudla.tclient.adapters.TweetAdapter;
import com.groupon.vgudla.tclient.dialogs.ComposeDialog;
import com.groupon.vgudla.tclient.listeners.EndlessScrollListener;
import com.groupon.vgudla.tclient.listeners.OnComposeListener;
import com.groupon.vgudla.tclient.models.Tweet;
import com.groupon.vgudla.tclient.util.TwitterClient;
import com.groupon.vgudla.tclient.util.TwitterErrorMessageHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements OnComposeListener {
    private static final String TAG = "TimelineActivity";
    private static final int TWEET_REQUEST_COUNT = 100;
    private TwitterClient twitterClient;
    private ListView lvTimeline;
    private List<Tweet> tweets = new ArrayList<>();
    private TweetAdapter tweetAdapter;
    private SwipeRefreshLayout swipeContainer;
    private long maxId=-1;
    private long sinceId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        lvTimeline = (ListView) findViewById(R.id.lvTimeline);
        twitterClient = TwitterApp.getRestClient();
        tweetAdapter = new TweetAdapter(this, tweets);
        lvTimeline.setAdapter(tweetAdapter);
        lvTimeline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = (Tweet) parent.getItemAtPosition(position);
                Intent tweetIntent = new Intent(TimelineActivity.this, TweetActivity.class);
                tweetIntent.putExtra("tweet", tweet);
                startActivity(tweetIntent);
            }
        });

        lvTimeline.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                getTimeline(false);
                return true;
            }
        });

        lvTimeline.setItemsCanFocus(true);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTimeline(true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getTimeline(false);

    }

    private void getTimeline(final boolean refresh) {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> tweetList = Tweet.fromJSONArray(response);
                if (refresh) {
                    for (int i = tweetList.size()-1; i >= 0; i--) {
                        tweetAdapter.insert(tweetList.get(i), 0);
                    }
                } else {
                    tweetAdapter.addAll(tweetList);
                }
                swipeContainer.setRefreshing(false);
                sinceId = tweetAdapter.getItem(0).getId();
                maxId = tweetAdapter.getItem(tweetAdapter.getCount()-1).getId();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, "Error while retrieving timeline" + throwable.getStackTrace());
            }
        };

        //We use the maxId when retrieving older tweets or when a user scrolls down. On refresh,
        //we need to use the sinceId to get latest tweets.
        if (maxId == -1) {
            tweetAdapter.clear();
        } else if (refresh) {
            Tweet newestTweet = tweetAdapter.getItem(0);
            sinceId = newestTweet.getId();
        } else {
            Tweet oldestTweet = tweetAdapter.getItem(tweetAdapter.getCount()-1);
            maxId = oldestTweet.getId();
        }
        twitterClient.getTimeLine(responseHandler, TWEET_REQUEST_COUNT, maxId, sinceId, refresh);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.miCompose) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCompose(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialog composeDialog = ComposeDialog.newInstance(TimelineActivity.class, null);
        composeDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        composeDialog.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishCompose(String inputText) {
        twitterClient.postTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                getTimeline(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, TwitterErrorMessageHelper.getErrorMessage(errorResponse));
                Log.e(TAG, Log.getStackTraceString(throwable));
            }
        }, inputText);
    }
}
