package com.codepath.apps.mptweettweet.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mprice on 2/18/16.
 */

@Table(name = "Users")
public class User extends Model {
    @Column(name = "name")
    public String name;
    @Column(name = "uid")
    public Long uid;
    @Column(name = "profileImageUrl")
    public String profileImageUrl;

    @Column(name = "screenName")
    public String screenName;

    @Column(name = "profile_background_image_url")
    public String backgroundImageUrl;

    @Column(name = "friends_count")
    public int friendsCount;

    @Column(name = "followers_count")
    public int followersCount;

    @Column(name = "tagline")
    public String tagline;


    public User() {
    }

    public User(JSONObject json) {
        super();

        try {
            this.name = json.getString("name");
            this.uid = json.getLong("id");
            this.profileImageUrl = json.getString("profile_image_url");
            this.tagline = json.getString("description");
            this.screenName = json.getString("screen_name");
            this.backgroundImageUrl = json.getString("profile_background_image_url");
            this.friendsCount = json.getInt("friends_count");
            this.followersCount = json.getInt("followers_count");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
