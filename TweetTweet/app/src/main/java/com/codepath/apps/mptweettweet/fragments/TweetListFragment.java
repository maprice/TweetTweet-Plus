package com.codepath.apps.mptweettweet.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mptweettweet.RestApplication;
import com.codepath.apps.mptweettweet.activities.ComposeDialogFragment;
import com.codepath.apps.mptweettweet.activities.DetailActivity;
import com.codepath.apps.mptweettweet.activities.ProfileActivity;
import com.codepath.apps.mptweettweet.adapters.TweetArrayAdapter;
import com.codepath.apps.mptweettweet.auth.TwitterClient;
import com.codepath.apps.mptweettweet.models.Tweet;
import com.codepath.apps.mptweettweet.models.User;
import com.codepath.apps.mptweettweet.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public abstract class TweetListFragment extends Fragment implements TweetArrayAdapter.ITweetInteractionListener {

    @Bind(R.id.lvTweets)
    RecyclerView lvTweets;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    protected String currentUserName;
    protected String currentUserUrl;


    protected ArrayList<Tweet> tweets;
    protected TweetArrayAdapter adapter;
    protected LinearLayoutManager layoutManager;

    protected TwitterClient twitterClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();

        adapter = new TweetArrayAdapter(tweets, this);

        twitterClient = RestApplication.getRestClient();
    }

    protected abstract void populateTimeline(int page, boolean refresh);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        ButterKnife.bind(this, v);
        setupRecyclerView();

        lvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline(page, false);
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



        return v;
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        lvTweets.setLayoutManager(layoutManager);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setFirstOnly(false);

        lvTweets.setAdapter(alphaAdapter);

    }

    private void populateCurrentUser() {
        SharedPreferences prefs = getContext().getSharedPreferences("hi", Context.MODE_PRIVATE);
        currentUserName = prefs.getString("name", "No name defined");//"No name defined" is the default value.
        currentUserUrl = prefs.getString("profileUrl", "0"); //0 is the default value.
    }

    @Override
    public void openDetailView(Tweet tweet) {
        Intent i = new Intent(getContext(), DetailActivity.class);
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
        FragmentManager fm = getActivity().getSupportFragmentManager();
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
    public void openProfile(User user) {
        Intent i = new Intent(getActivity(), ProfileActivity.class);

        i.putExtra("uid", user.uid);

        startActivity(i);
    }
}
