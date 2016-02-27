package com.codepath.apps.mptweettweet.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.codepath.apps.mptweettweet.models.Tweet;
import com.codepath.apps.mptweettweet.models.User;
import com.codepath.apps.restclienttemplate.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    Tweet mTweet;

    @Bind(R.id.tvBody)
    TextView tvBody;

    @Bind(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @Bind(R.id.tvUsername)
    TextView tvUsername;

    @Bind(R.id.tvHandle)
    TextView tvHandle;

    @Bind(R.id.ivMedia)
    ImageView ivMedia;

    @Bind(R.id.tvInfo)
    TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        long tweetId = getIntent().getLongExtra("tweet", -1);
        mTweet = new Select().from(Tweet.class)
                .where("tweetId = ?", tweetId)
                .executeSingle();

        configureView();
    }

    private void configureView() {
        tvBody.setText(mTweet.body);
        User user = mTweet.user;
        if (user != null) {
            tvUsername.setText(user.name);
            tvHandle.setText("@" + user.screenName);

            ivProfileImage.setImageResource(0);
            Glide.with(ivProfileImage.getContext()).load(user.profileImageUrl).placeholder(R.drawable.profile_photo_placeholder).into(ivProfileImage);
        }

        if (mTweet.imageUrl != null) {
            ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this).load(mTweet.imageUrl).into(ivMedia);
        }

        String likeText = mTweet.retweetCount + " RETWEETS " + mTweet.favoriteCount + " LIKES";
        tvInfo.setText(likeText);
    }
}
