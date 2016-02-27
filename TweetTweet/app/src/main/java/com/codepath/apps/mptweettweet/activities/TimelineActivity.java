package com.codepath.apps.mptweettweet.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.activeandroid.query.Select;
import com.codepath.apps.mptweettweet.RestApplication;
import com.codepath.apps.mptweettweet.adapters.TweetArrayAdapter;
import com.codepath.apps.mptweettweet.auth.TwitterClient;
import com.codepath.apps.mptweettweet.models.Tweet;
import com.codepath.apps.mptweettweet.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mptweettweet.utils.NetworkUtils;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class TimelineActivity extends AppCompatActivity implements ComposeDialogFragment.OnFragmentInteractionListener, TweetArrayAdapter.ITweetInteractionListener {

    @Bind(R.id.lvTweets)
    RecyclerView lvTweets;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private String currentUserName;
    private String currentUserUrl;

    private TwitterClient twitterClient;
    private ArrayList<Tweet> tweets;
    private TweetArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        twitterClient = RestApplication.getRestClient();
        tweets = new ArrayList<>();
        adapter = new TweetArrayAdapter(tweets, this);

        setupRecyclerView();
        populateTimeline(0, true);
        populateCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkUtils.isNetworkAvailable(TimelineActivity.this)) {
                    NetworkUtils.showNetworkError(getApplicationContext());
                    return;
                }

                FragmentManager fm = getSupportFragmentManager();
                ComposeDialogFragment editNameDialog = ComposeDialogFragment.newInstance(currentUserName, currentUserUrl);
                editNameDialog.show(fm, "fragment_edit_name");
            }
        });

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(0, true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void populateCurrentUser() {
        SharedPreferences prefs = getSharedPreferences("hi", MODE_PRIVATE);
        currentUserName = prefs.getString("name", "No name defined");//"No name defined" is the default value.
        currentUserUrl = prefs.getString("profileUrl", "0"); //0 is the default value.
    }

    private void populateTimeline(int page, boolean refresh) {
        if (refresh) {
            int start = tweets.size();
            tweets.clear();
            if (start > 0) {
                adapter.notifyItemRangeRemoved(0, start);
            }
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showNetworkError(getApplicationContext());

            if (refresh) {
                List<Tweet> queryResults = new Select().from(Tweet.class)
                        .limit(100).execute();
                tweets.addAll(queryResults);

                int curSize = adapter.getItemCount();
                adapter.notifyItemRangeInserted(curSize, queryResults.size() - 1);
            }

            swipeContainer.setRefreshing(false);
            return;
        }


        twitterClient.getHomeTimeline(page, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                ArrayList<Tweet> list = Tweet.fromJson(jsonArray);
                tweets.addAll(list);

                int curSize = adapter.getItemCount();
                adapter.notifyItemRangeInserted(curSize, list.size() - 1);

                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                swipeContainer.setRefreshing(false);
            }
        });
    }


    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lvTweets.setLayoutManager(layoutManager);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setFirstOnly(false);

        lvTweets.setAdapter(alphaAdapter);
        lvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline(page, false);
            }
        });
    }

    @Override
    public void openDetailView(Tweet tweet) {
        Intent i = new Intent(getApplicationContext(), DetailActivity.class);
        i.putExtra("tweet", tweet.tweetId);
        startActivity(i);
    }

    @Override
    public void retweet(Tweet tweet) {
        twitterClient.retweet(tweet.tweetId, new JsonHttpResponseHandler());

        // Optimistically increment favorite count
        tweet.retweetCount++;
        tweet.retweeted = true;
    }

    @Override
    public void reply(Tweet tweet) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment editNameDialog = ComposeDialogFragment.newInstance(currentUserName, currentUserUrl, tweet.user.uid);
        editNameDialog.show(fm, "fragment_edit_name");

    }

    @Override
    public void favorite(Tweet tweet) {
        twitterClient.favorite(tweet.tweetId, new JsonHttpResponseHandler());

        // Optimistically increment favorite count
        tweet.favoriteCount++;
        tweet.favorited = true;
    }

    @Override
    public void onTweetSelected(String tweet) {
        twitterClient.postTweet(tweet, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Tweet newTweet = new Tweet(jsonObject);
                tweets.add(0, newTweet);
                adapter.notifyItemInserted(0);
                lvTweets.scrollToPosition(0);
            }
        });
    }
}
