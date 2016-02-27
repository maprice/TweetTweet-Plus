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

    public User(){
    }

    public User(JSONObject json) {
        super();

        try {
            this.name = json.getString("name");
            this.uid = json.getLong("id");
            this.profileImageUrl = json.getString("profile_image_url");
            this.screenName = json.getString("screen_name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
