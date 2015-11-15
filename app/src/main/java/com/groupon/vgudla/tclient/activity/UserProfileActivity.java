package com.groupon.vgudla.tclient.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupon.vgudla.tclient.R;
import com.groupon.vgudla.tclient.TwitterApp;
import com.groupon.vgudla.tclient.fragments.UserTimelineFragment;
import com.groupon.vgudla.tclient.models.User;
import com.groupon.vgudla.tclient.util.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvFollowers;
    private TextView tvFollowing;
    private ImageView rvProfileView;
    private TextView tvDescription;
    private TextView tvUserName;
    private TwitterClient twitterClient;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        twitterClient = TwitterApp.getRestClient();
        twitterClient.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSONObject(response);
                tvFollowers.setText(user.getFollowersCount() + " followers");
                tvFollowing.setText(user.getFollowing() + " following");
                tvDescription.setText(user.getDescription());
                tvUserName.setText(user.getUserName());
                Picasso.with(UserProfileActivity.this).load(user.getUserProfileUrl()).into(rvProfileView);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
        String screenName = getIntent().getStringExtra("screen_name");
        getSupportActionBar().setTitle("@" + screenName);
        setupViews();
        if (savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.getInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flUserTimeline, userTimelineFragment);
            ft.commit();
        }
    }

    private void setupViews() {
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        rvProfileView = (ImageView) findViewById(R.id.rvProfileView);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvUserName = (TextView) findViewById(R.id.rvUserName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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
}
