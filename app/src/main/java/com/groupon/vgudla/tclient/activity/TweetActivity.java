package com.groupon.vgudla.tclient.activity;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupon.vgudla.tclient.R;
import com.groupon.vgudla.tclient.TwitterApp;
import com.groupon.vgudla.tclient.dialogs.ComposeDialog;
import com.groupon.vgudla.tclient.listeners.OnComposeListener;
import com.groupon.vgudla.tclient.models.Tweet;
import com.groupon.vgudla.tclient.util.TimeHelper;
import com.groupon.vgudla.tclient.util.TwitterClient;
import com.groupon.vgudla.tclient.util.TwitterErrorMessageHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class TweetActivity extends AppCompatActivity implements OnComposeListener {

    private static final String TAG = "TweetActivity";

    private TwitterClient twitterClient;
    private Tweet tweet;
    private TextView tvTweet;
    private TextView tvTimestamp;
    private TextView tvUserName;
    private TextView tvScreenName;
    private TextView tvRetweetCount;
    private TextView tvFavoriteCount;
    private ImageView profileView;
    private ImageView ivRetweet;
    private ImageView ivFavorite;
    private ImageView ivReply;
    private ImageView ivDetailedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        setupViews();
    }

    private void setupViews() {
        tweet = getIntent().getParcelableExtra("tweet");
        twitterClient = TwitterApp.getRestClient();
        profileView = (ImageView) findViewById(R.id.ivProfile);
        tvTweet = (TextView) findViewById(R.id.tvTweetText);
        tvTimestamp = (TextView) findViewById(R.id.tvTimestamp);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvRetweetCount = (TextView) findViewById(R.id.tvRetweetCount);
        tvFavoriteCount = (TextView) findViewById(R.id.tvFavoriteCount);
        ivFavorite = (ImageView) findViewById(R.id.ivFavorite);
        ivRetweet = (ImageView) findViewById(R.id.ivRetweet);
        ivReply = (ImageView) findViewById(R.id.ivReplyButton);
        ivDetailedView = (ImageView) findViewById(R.id.ivDetailedView);

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeDialog composeDialog = ComposeDialog.newInstance(TweetActivity.class,
                        tweet.getUserScreenName());
                composeDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                composeDialog.show(fm, "fragment_edit_name");
            }
        });


        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterClient.postReTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                        if (tweet.isRetweeted()) {
                            tweet.setRetweetCount(tweet.getRetweetCount() - 1);
                            tweet.setRetweeted(false);
                        } else {
                            tweet.setRetweetCount(tweet.getRetweetCount() + 1);
                            tweet.setRetweeted(true);
                        }
                        setRetweetImage();
                        Log.i(TAG, "Retweeted for:" + tweet.getTweetId());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, TwitterErrorMessageHelper.getErrorMessage(errorResponse));
                        Log.e(TAG, Log.getStackTraceString(throwable));
                    }
                }, tweet.getTweetId());
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterClient.postFavorite(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                        if (tweet.isFavorited()) {
                            tweet.setFavoriteCount(tweet.getFavoriteCount() - 1);
                            tweet.setFavorited(false);
                        } else {
                            tweet.setFavoriteCount(tweet.getFavoriteCount() + 1);
                            tweet.setFavorited(true);
                        }
                        setFavoriteImage();
                        Log.i(TAG, "Favorited:" + tweet.getTweetId());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                        Log.e(TAG, TwitterErrorMessageHelper.getErrorMessage(responseBody));
                        Log.e(TAG, Log.getStackTraceString(error));
                    }
                }, tweet.getTweetId(), tweet.isFavorited());
            }
        });

        setFavoriteImage();
        setRetweetImage();
        if (tweet.getUserProfileUrl() != null) {
            Picasso.with(this).load(tweet.getUserProfileUrl()).into(profileView);
        }
        tvTweet.setText(tweet.getTweet());
        tvTimestamp.setText(TimeHelper.getAbbreviatedTimeSpan(tweet.getTimestamp()));
        tvUserName.setText(tweet.getUserName());
        tvScreenName.setText("@" + tweet.getUserScreenName());
        if (tweet.getMediaUrl() != null) {
            Picasso.with(this).load(tweet.getMediaUrl()).into(ivDetailedView);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishCompose(String inputText) {
        twitterClient.postReplyTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i(TAG, "Replied to tweet" + tweet.getTweetId());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, TwitterErrorMessageHelper.getErrorMessage(errorResponse));
                Log.e(TAG, Log.getStackTraceString(throwable));
            }
        }, inputText, tweet.getTweetId());
    }

    private void setFavoriteImage() {
        if (tweet.isFavorited()) {
            ivFavorite.setImageResource(R.drawable.favorite);
        } else {
            ivFavorite.setImageResource(R.drawable.star);
        }
        tvFavoriteCount.setText("" + tweet.getFavoriteCount());
    }

    private void setRetweetImage() {
        if (!tweet.isRetweeted()) {
            ivRetweet.setImageResource(R.drawable.retweet);
        } else {
            ivRetweet.setImageResource(R.drawable.retweeted);
        }
        tvRetweetCount.setText("" + tweet.getRetweetCount());
    }
}
