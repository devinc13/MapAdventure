package com.c13.devin.moodfeedandroid;

public class MoodFeedOpenStreamRequest {
    double[] ne;
    double[] sw;

    public MoodFeedOpenStreamRequest(double[] northeast, double[] southwest) {
        this.ne = northeast;
        this.sw = southwest;
    }
}
