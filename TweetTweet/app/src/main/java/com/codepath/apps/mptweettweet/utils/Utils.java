package com.codepath.apps.mptweettweet.utils;

import com.ocpsoft.pretty.time.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mprice on 2/20/16.
 */
public class Utils {

    public static String getRelativeTime(String date) {
        Date d = getTwitterDate(date);
        if (d.compareTo(new Date()) == 1) {
            return "Just now";
        }
        PrettyTime p = new PrettyTime();
        return p.format(d);
    }

    public static Date getTwitterDate(String date) {

        final String TWITTER="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER);
        sf.setLenient(true);
        try {
            return sf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
