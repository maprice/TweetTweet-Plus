package com.codepath.apps.mptweettweet.fragments;

import android.os.Bundle;
import android.util.Log;

import com.activeandroid.query.Select;
import com.codepath.apps.mptweettweet.models.Tweet;
import com.codepath.apps.mptweettweet.utils.NetworkUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mprice on 2/27/16.
 */
public class MentionsTimelineFragment extends TweetListFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateTimeline(0, true);
    }

    @Override
    protected void populateTimeline(int page, boolean refresh) {
        if (refresh) {
            int start = tweets.size();
            tweets.clear();
            if (start > 0) {
                adapter.notifyItemRangeRemoved(0, start);
            }
        }

        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            NetworkUtils.showNetworkError(getActivity());

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


        twitterClient.getMentionsTimeline(page, new JsonHttpResponseHandler() {
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

}
