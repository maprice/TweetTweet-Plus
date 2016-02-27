package com.codepath.apps.mptweettweet.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.codepath.apps.mptweettweet.RestApplication;
import com.codepath.apps.mptweettweet.auth.TwitterClient;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {

		TwitterClient twitterClient = RestApplication.getRestClient();
		twitterClient.getCurrentUser(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				SharedPreferences prefs = getSharedPreferences("hi", MODE_PRIVATE);
				String restoredText = prefs.getString("name", null);
				if (restoredText != null) {
					return;
				}
				String name;
				String profileUrl;
				try {
					 name = response.getString("name");
					 profileUrl = response.getString("profile_image_url");

					SharedPreferences.Editor editor = getSharedPreferences("hi", MODE_PRIVATE).edit();
					editor.putString("name", name);
					editor.putString("profileUrl", profileUrl);
					editor.commit();

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});


		Intent i = new Intent(this, TimelineActivity.class);
		startActivity(i);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
