package com.codepath.apps.mptweettweet.fragments;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.mptweettweet.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mprice on 2/27/16.
 */
public class UserTimelineFragment extends TweetListFragment {

    String mScreenName;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragmentDemo = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);


        Log.e("nameasdfa", "Storing=" + screenName);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScreenName = getArguments().getString("screenName", "");
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

//        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
//            //NetworkUtils.showNetworkError(getApplicationContext());
//
//            if (refresh) {
//                List<Tweet> queryResults = new Select().from(Tweet.class)
//                        .limit(100).execute();
//                tweets.addAll(queryResults);
//
//                int curSize = adapter.getItemCount();
//                adapter.notifyItemRangeInserted(curSize, queryResults.size() - 1);
//            }
//
//            swipeContainer.setRefreshing(false);
//            return;
//        }
        Log.e("Timeline!!!","sdfsdfsdf");

        twitterClient.getUserTimeline(mScreenName, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                ArrayList<Tweet> list = Tweet.fromJson(jsonArray);
                tweets.addAll(list);
                Log.e("Timeline!!!", jsonArray.toString());
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
