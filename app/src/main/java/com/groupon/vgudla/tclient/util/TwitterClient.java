package com.groupon.vgudla.tclient.util;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API.
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String TAG = "TwitterClient";
	public static final String REST_URL = "https://api.twitter.com/1.1/";
	public static final String REST_CONSUMER_KEY = "AkJafU5JGmSnjEIiRitd6tw4M";
	public static final String REST_CONSUMER_SECRET = "tvk1o4B8wdS7JiJRP29UxFicfHxShchIyFAMWl2QFlc2n9rmiZ";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/**
	 * Method to post tweets to the user/home timeline
	 * POST /statuses/update.json
	 * @param handler Response handler for the client
	 * @param tweet The tweet to post
	 */
	public void postTweet(AsyncHttpResponseHandler handler, String tweet) {
		String apiUrl = getApiUrl("statuses/update.json?status=");
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.post(apiUrl + tweet, params, handler);
	}

	/**
	 * Method to retrieve the home timeline for a specific user
	 * @param responseHandler Response Handler for the client
	 * @param tweetCount Number of tweets to retrieve
	 * @param maxId Retrieves tweets older than this id
	 * @param sinceId Retrieves tweets newer than this id
	 * @param refresh A flag used to indicate what type of request is to be made
	 */
	public void getTimeLine(AsyncHttpResponseHandler responseHandler, int tweetCount, long maxId,
							long sinceId, boolean refresh) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("format", "json");
		params.put("count", tweetCount);
		if (!refresh && maxId != -1) {
			params.put("max_id", maxId);
		}
		if (refresh && sinceId != -1) {
			params.put("since_id", sinceId);
		}
		Log.d(TAG, "Requested URL:" + params.toString());
		client.get(apiUrl, params, responseHandler);
	}
}