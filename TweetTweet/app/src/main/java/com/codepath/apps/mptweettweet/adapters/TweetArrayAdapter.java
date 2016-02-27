package com.codepath.apps.mptweettweet.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mptweettweet.models.Tweet;
import com.codepath.apps.mptweettweet.models.User;
import com.codepath.apps.mptweettweet.utils.Utils;
import com.codepath.apps.restclienttemplate.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mprice on 2/18/16.
 */
public class TweetArrayAdapter extends RecyclerView.Adapter<TweetArrayAdapter.TweetViewHolder> {

    public interface ITweetInteractionListener {
        void openDetailView(Tweet tweet);
        void retweet(Tweet tweet);
        void reply(Tweet tweet);
        void favorite(Tweet tweet);
    }

    private ITweetInteractionListener mListener;
    private List<Tweet> mTweets;

    public static class TweetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.tvBody)
        TextView tvBody;

        @Bind(R.id.ivProfileImage)
        ImageView ivProfileImage;

        @Bind(R.id.tvUsername)
        TextView tvUsername;

        @Bind(R.id.tvHandle)
        TextView tvHandle;

        @Bind(R.id.btnRetweet)
        Button btnRetweet;

        @Bind(R.id.btnReply)
        Button btnReply;

        @Bind(R.id.btnLike)
        Button btnLike;

        @Bind(R.id.tvTimestamp)
        TextView tvTimestamp;

        @Bind(R.id.ivMedia)
        ImageView ivMedia;

        IMyViewHolderClicks mListener;

        public TweetViewHolder(View itemView, IMyViewHolderClicks listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.onTweetClicked(v, getAdapterPosition());
        }

        public interface IMyViewHolderClicks {
            void onTweetClicked(View caller, int position);
        }
    }


    public TweetArrayAdapter(List<Tweet> tweets, ITweetInteractionListener listener) {
        mTweets = tweets;
        mListener = listener;
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(parent.getContext());
        View view = inflator.inflate(R.layout.item_tweet, parent, false);
        return new TweetViewHolder(view, new TweetViewHolder.IMyViewHolderClicks() {
            @Override
            public void onTweetClicked(View caller, int position) {
                Tweet model = mTweets.get(position);
                mListener.openDetailView(model);
            }
        });
    }

    @Override
    public void onBindViewHolder(final TweetViewHolder holder, int position) {
        final Tweet tweet = mTweets.get(position);

        holder.btnRetweet.setText(String.valueOf(tweet.retweetCount));
        holder.btnLike.setText(String.valueOf(tweet.favoriteCount));
        holder.tvBody.setText(tweet.body);
        holder.tvTimestamp.setText(Utils.getRelativeTime(tweet.createdAt));

        User user = tweet.user;
        if (user != null) {
            holder.tvUsername.setText(user.name);
            holder.tvHandle.setText("@" + user.screenName);

            holder.ivProfileImage.setImageResource(0);
            Glide.with(holder.ivProfileImage.getContext()).load(user.profileImageUrl).placeholder(R.drawable.profile_photo_placeholder).into(holder.ivProfileImage);
        }

        holder.ivMedia.setImageResource(0);
        holder.ivMedia.setVisibility(View.GONE);
        if (tweet.imageUrl != null) {
            holder.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(holder.ivMedia.getContext()).load(tweet.imageUrl).into(holder.ivMedia);

        }

        holder.btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnRetweet.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.retweet_on, 0);
                holder.btnRetweet.setText(String.valueOf(tweet.retweetCount + 1));
                mListener.retweet(tweet);

            }
        });

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.heart_red, 0);
                holder.btnLike.setText(String.valueOf(tweet.favoriteCount + 1));
                mListener.favorite(tweet);
            }
        });

        holder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.reply(tweet);
            }
        });

        if (tweet.favorited) {
            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.heart_red, 0);
        } else {
            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.heart, 0);
        }

        if (tweet.retweeted) {
            holder.btnRetweet.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.retweet_on, 0);
        } else {
            holder.btnRetweet.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.retweet, 0);
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}


