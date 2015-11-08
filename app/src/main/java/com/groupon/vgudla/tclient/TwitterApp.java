package com.groupon.vgudla.tclient;

import android.content.Context;

import com.groupon.vgudla.tclient.util.TwitterClient;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = TwitterApp.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TwitterApp extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		TwitterApp.context = this;
	}

	public static TwitterClient getRestClient() {
		TwitterClient client = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApp.context);
		return client;
	}
}