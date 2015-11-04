package com.groupon.vgudla.tclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.tclient.R;
import com.groupon.vgudla.tclient.models.Tweet;
import com.groupon.vgudla.tclient.util.TimeHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TweetAdapter extends ArrayAdapter<Tweet> {

    public class TweetViewHolder {
        ImageView profileView;
        TextView tvTweet;
        TextView tvTimestamp;
        TextView tvUserName;
        TextView tvScreenName;
    }

    public TweetAdapter(Context context, List<Tweet> tweets) {
        super(context, R.layout.item_tweet, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        TweetViewHolder tweetViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            tweetViewHolder = new TweetViewHolder();
            tweetViewHolder.profileView = (ImageView) convertView.findViewById(R.id.ivProfile);
            tweetViewHolder.tvTweet = (TextView) convertView.findViewById(R.id.tvTweetText);
            tweetViewHolder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
            tweetViewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            tweetViewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            convertView.setTag(tweetViewHolder);
        } else {
            tweetViewHolder = (TweetViewHolder) convertView.getTag();
        }
        if (tweet.getUser().getImageUrl() != null) {
            Picasso.with(getContext()).load(tweet.getUser().getImageUrl()).into(tweetViewHolder.profileView);
        }
        tweetViewHolder.tvTweet.setText(tweet.getTweet());
        tweetViewHolder.tvTimestamp.setText(TimeHelper.getAbbreviatedTimeSpan(tweet.getTimestamp()));
        tweetViewHolder.tvUserName.setText(tweet.getUser().getName());
        tweetViewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        return convertView;
    }
}
