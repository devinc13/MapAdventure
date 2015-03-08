package com.c13.devin.moodfeedandroid;

import org.json.JSONObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface MoodFeedService {
    @POST("/location/start_stream")
    Observable<String> startStream(@Body MoodFeedOpenStreamRequest moodFeedOpenStreamRequest);

    @GET("/location/get_data/{locationId}")
    Observable<JSONObject> getStreamData(@Path("locationId") String locationId);
}
