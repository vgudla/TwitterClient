package com.groupon.vgudla.tclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupon.vgudla.tclient.R;
import com.groupon.vgudla.tclient.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileAdapter extends ArrayAdapter<User> {

    public class ProfileViewHolder {
        ImageView itProfileView;
        TextView itUserName;
        TextView itScreenName;
        TextView itFollowers;
        TextView itFollowing;
        TextView itStatuses;
    }

    public ProfileAdapter(Context context, List<User> tweets) {
        super(context, R.layout.item_profile, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        ProfileViewHolder profileViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_profile, parent, false);
            profileViewHolder = new ProfileViewHolder();
            profileViewHolder.itProfileView = (ImageView) convertView.findViewById(R.id.itProfileView);
            profileViewHolder.itUserName = (TextView) convertView.findViewById(R.id.itUserName);
            profileViewHolder.itScreenName = (TextView) convertView.findViewById(R.id.itScreenName);
            profileViewHolder.itFollowers = (TextView) convertView.findViewById(R.id.itFollowers);
            profileViewHolder.itFollowing = (TextView) convertView.findViewById(R.id.itFollowing);
            profileViewHolder.itStatuses = (TextView) convertView.findViewById(R.id.itStatuses);
            convertView.setTag(profileViewHolder);
        } else {
            profileViewHolder = (ProfileViewHolder) convertView.getTag();
        }
        if (user.getUserProfileUrl() != null) {
            Picasso.with(getContext()).load(user.getUserProfileUrl()).into(profileViewHolder.itProfileView);
        }
        profileViewHolder.itUserName.setText(user.getUserName());
        profileViewHolder.itScreenName.setText("@" + user.getUserScreenName());
        profileViewHolder.itFollowers.setText(user.getFollowersCount() + " followers");
        profileViewHolder.itFollowing.setText(user.getFollowing() + " following");
        profileViewHolder.itStatuses.setText(user.getTweetCount() + " tweets");
        return convertView;
    }
}
