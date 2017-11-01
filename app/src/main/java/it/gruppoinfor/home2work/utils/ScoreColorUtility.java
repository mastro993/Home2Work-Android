package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import it.gruppoinfor.home2work.R;

public class ScoreColorUtility {

    public static int getScoreColor(Context context, int score) {
        if (score < 50) {
            return ContextCompat.getColor(context, R.color.red_500);
        } else if (score < 60) {
            return ContextCompat.getColor(context, R.color.orange_600);
        } else if (score < 70) {
            return ContextCompat.getColor(context, R.color.amber_400);
        } else if (score < 80) {
            return ContextCompat.getColor(context, R.color.lime_500);
        } else if (score < 90) {
            return ContextCompat.getColor(context, R.color.light_green_500);
        } else {
            return ContextCompat.getColor(context, R.color.green_500);
        }
    }
}
