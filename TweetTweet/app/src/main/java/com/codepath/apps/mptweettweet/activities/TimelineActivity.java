package com.codepath.apps.mptweettweet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mptweettweet.RestApplication;
import com.codepath.apps.mptweettweet.auth.TwitterClient;
import com.codepath.apps.mptweettweet.fragments.HomeTimelineListFragment;
import com.codepath.apps.mptweettweet.fragments.MentionsTimelineFragment;
import com.codepath.apps.mptweettweet.models.Tweet;
import com.codepath.apps.mptweettweet.utils.NetworkUtils;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity implements ComposeDialogFragment.OnFragmentInteractionListener {

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.tabs)
    PagerSlidingTabStrip slidingTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));



        slidingTabStrip.setViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkUtils.isNetworkAvailable(TimelineActivity.this)) {
                    NetworkUtils.showNetworkError(getApplicationContext());
                    return;
                }

                FragmentManager fm = getSupportFragmentManager();
                ComposeDialogFragment editNameDialog = ComposeDialogFragment.newInstance("TODO", "TODO");
                editNameDialog.show(fm, "fragment_edit_name");
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);
        return true;
    }


    public void onProfileViewOnClick(MenuItem item) {
Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }



    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String[] pageTitles = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
           switch (position) {
               case 0:
                   return new HomeTimelineListFragment();
               case 1:
                   return new MentionsTimelineFragment();
           }
            return null;
        }

        @Override
        public int getCount() {
            return pageTitles.length;
        }
    }


    @Override
    public void onTweetSelected(String tweet) {
        TwitterClient twitterClient = RestApplication.getRestClient();
        twitterClient.postTweet(tweet, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Tweet newTweet = new Tweet(jsonObject);
//                tweets.add(0, newTweet);
//                adapter.notifyItemInserted(0);
//                lvTweets.scrollToPosition(0);
            }
        });
    }



}
