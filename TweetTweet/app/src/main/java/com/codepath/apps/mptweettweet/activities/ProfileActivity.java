package com.codepath.apps.mptweettweet.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.codepath.apps.mptweettweet.fragments.UserTimelineFragment;
import com.codepath.apps.mptweettweet.models.User;
import com.codepath.apps.restclienttemplate.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @Bind(R.id.flContainer)
    FrameLayout container;

    @Bind(R.id.ivBackground)
    ImageView ivBackground;

    @Bind(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @Bind(R.id.tvHandle)
    TextView tvHandle;

    @Bind(R.id.tvUsername)
    TextView tvUsername;

    @Bind(R.id.tvFollower)
    TextView tvFollower;

    @Bind(R.id.tvFollowing)
    TextView tvFollowing;

    @Bind(R.id.tvTagline)
    TextView tvTagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        Long userId = getIntent().getLongExtra("uid", 0);

        User user = new Select().from(User.class)
                .where("uid = ?", userId)
                .executeSingle();

        String screenName = user.screenName;
        getSupportActionBar().setTitle(user.name);

        if (savedInstanceState == null) {
            UserTimelineFragment fragment = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragment);
            ft.commit();
        }

        configureViewWithUser(user);
    }

    private void configureViewWithUser(User user) {
        ivProfileImage.setImageResource(0);
        ivBackground.setImageResource(0);

        Glide.with(this).load(user.profileImageUrl).placeholder(R.drawable.profile_photo_placeholder).into(ivProfileImage);
        Glide.with(this).load(user.backgroundImageUrl).into(ivBackground);

        tvHandle.setText("@" + user.screenName);
        tvUsername.setText(user.name);
        tvTagline.setText(user.tagline);
        tvFollower.setText(user.followersCount + "");
        tvFollowing.setText(user.friendsCount + "");
    }
}
