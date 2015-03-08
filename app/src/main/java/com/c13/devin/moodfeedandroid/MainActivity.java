package com.c13.devin.moodfeedandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.c13.devin.moodfeedandroid.Activities.StreamActivity;
import com.c13.devin.moodfeedandroid.MoodFeedModels.MoodFeedOpenStreamRequest;
import com.c13.devin.moodfeedandroid.MoodFeedModels.MoodFeedService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

import retrofit.RestAdapter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    private GoogleMap googleMap;
    private MoodFeedService moodFeedService;
    private Subscription subscription;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://moodfeedapi.herokuapp.com")
                .build();

        moodFeedService = restAdapter.create(MoodFeedService.class);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStream();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        super.onDestroy();
    }


    public void startStream() {

        LatLngBounds curScreen = googleMap.getProjection()
                .getVisibleRegion().latLngBounds;

        double[] northeast = {curScreen.northeast.latitude, curScreen.northeast.longitude};
        double[] southwest = {curScreen.southwest.latitude, curScreen.southwest.longitude};

        MoodFeedOpenStreamRequest moodFeedOpenStreamRequest = new MoodFeedOpenStreamRequest(northeast, southwest);

        Log.d("MOODFEED - northeast", String.valueOf(northeast[0]) + ", " + String.valueOf(northeast[1]));
        Log.d("MOODFEED - southwest", String.valueOf(southwest[0]) + ", " + String.valueOf(southwest[1]));

        progressDialog = ProgressDialog.show(this, null, getString(R.string.sending_location_data));
        if (subscription == null || subscription.isUnsubscribed()) {
            subscription = moodFeedService.startStream(moodFeedOpenStreamRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            Log.d("MOODFEED - Completed: ", "?");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("MOODFEED - Error!", e.getCause().toString() + " - " + e.toString());
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onNext(String id) {
                            Log.e("MOODFEED - Next - Id = ", id);
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), StreamActivity.class);
                            intent.putExtra(StreamActivity.MESSAGE_ID, id);
                            startActivity(intent);
                        }
                    });
        }
    }
}