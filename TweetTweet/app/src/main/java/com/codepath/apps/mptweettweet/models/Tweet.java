package com.codepath.apps.mptweettweet.models;

/**
 * Created by mprice on 2/18/16.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@Table(name = "Tweets")
public class Tweet extends Model {
    // Define database columns and associated fields
    @Column(name = "tweetId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long tweetId;
    @Column(name = "createdAt")
    public String createdAt;
    @Column(name = "body")
    public String body;
    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public User user;
    @Column(name = "retweetCount")
    public int retweetCount;
    @Column(name = "favoriteCount")
    public int favoriteCount;
    @Column(name = "imageUrl")
    public String imageUrl;

    @Column(name = "retweeted")
    public boolean retweeted;
    @Column(name = "favorited")
    public boolean favorited;

    public Tweet() {
    }


    public Tweet(JSONObject object) {
        super();

        try {
            this.tweetId = object.getLong("id");

            this.createdAt = object.getString("created_at");
            this.body = object.getString("text");
            this.user = new User(object.getJSONObject("user"));

            this.retweetCount = object.getInt("retweet_count");
            this.favoriteCount = object.getInt("favorite_count");

            this.retweeted = object.getBoolean("retweeted");
            this.favorited = object.getBoolean("favorited");


            JSONObject entities = object.optJSONObject("entities");
            if (entities != null) {
                JSONArray media = entities.optJSONArray("media");
                if (media != null) {
                    this.imageUrl = media.optJSONObject(0).optString("media_url");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweet.user.save();
            tweet.save();
            tweets.add(tweet);
        }

        return tweets;
    }
}
