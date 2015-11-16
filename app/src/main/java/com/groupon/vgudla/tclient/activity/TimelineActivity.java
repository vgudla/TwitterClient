package com.groupon.vgudla.tclient.activity;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.groupon.vgudla.tclient.R;
import com.groupon.vgudla.tclient.adapters.SmartFragmentStatePagerAdapter;
import com.groupon.vgudla.tclient.dialogs.ComposeDialog;
import com.groupon.vgudla.tclient.fragments.HomeTimelineFragment;
import com.groupon.vgudla.tclient.fragments.MentionsTimelineFragment;
import com.groupon.vgudla.tclient.fragments.TweetListFragment;
import com.groupon.vgudla.tclient.listeners.OnComposeListener;

public class TimelineActivity extends AppCompatActivity implements OnComposeListener {
    private static final String TAG = "TimelineActivity";
    private SmartFragmentStatePagerAdapter tweetPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tweetPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tweetPagerAdapter);
        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pagerSlidingTabStrip.setViewPager(viewPager);

    }

    public void onCompose(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialog composeDialog = ComposeDialog.newInstance(TimelineActivity.class, null);
        composeDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        composeDialog.show(fm, "fragment_edit_name");
    }

    public void onProfileView(MenuItem item) {
        Intent profileIntent = new Intent(this, UserProfileActivity.class);
        startActivity(profileIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.miProfile) {
            return true;
        }

        if (id == R.id.miCompose) {
            return true;
        }

        if (id == R.id.miSearch) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle action bar text search here.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);

        MenuItem searchItem = menu.findItem(R.id.miSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent searchIntent = new Intent(TimelineActivity.this, SearchActivity.class);
                searchIntent.putExtra("query", query);
                startActivity(searchIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFinishCompose(String inputText) {
        TweetListFragment tweetListFragment =
                (TweetListFragment)tweetPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        tweetListFragment.onFinishCompose(inputText);
    }

    public static class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        private final String[] TAB_TITLES = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return new HomeTimelineFragment();
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return new MentionsTimelineFragment();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }

    }
}
