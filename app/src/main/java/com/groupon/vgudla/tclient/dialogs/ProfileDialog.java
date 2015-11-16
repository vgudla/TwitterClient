package com.groupon.vgudla.tclient.dialogs;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.groupon.vgudla.tclient.R;
import com.groupon.vgudla.tclient.TwitterApp;
import com.groupon.vgudla.tclient.adapters.ProfileAdapter;
import com.groupon.vgudla.tclient.models.User;
import com.groupon.vgudla.tclient.util.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileDialog extends DialogFragment {
    
    private ListView dvProfile;
    private List<User> users;
    private ProfileAdapter profileAdapter;
    private TwitterClient twitterClient;
    private boolean followers;

    public ProfileDialog() {
        // Empty constructor is required for DialogFragment
    }

    public static ProfileDialog newInstance(String screenName, boolean followers) {
        ProfileDialog profileDialog = new ProfileDialog();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        profileDialog.setArguments(args);
        profileDialog.users = new ArrayList<>();
        profileDialog.twitterClient = TwitterApp.getRestClient();
        profileDialog.followers = followers;
        return profileDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileAdapter = new ProfileAdapter(getActivity(), users);
        return inflater.inflate(R.layout.dialog_profile, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (followers) {
            getDialog().setTitle("Friends");
        } else {
            getDialog().setTitle("Following");
        }
        String screenName = getArguments().getString("screen_name");
        dvProfile = (ListView) view.findViewById(R.id.dvProfile);
        dvProfile.setAdapter(profileAdapter);
        profileAdapter.clear();

        if (followers) {
            twitterClient.getFollowersList(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray userArray = response.getJSONArray("users");
                        for (int i = 0; i < userArray.length(); i++) {
                            User user = User.fromJSONObject(userArray.getJSONObject(i));
                            profileAdapter.add(user);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            }, screenName);
        } else {
            twitterClient.getFriendsList(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray userArray = response.getJSONArray("users");
                        for (int i = 0; i < userArray.length(); i++) {
                            User user = User.fromJSONObject(userArray.getJSONObject(i));
                            profileAdapter.add(user);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            }, screenName);
        }
    }
}
