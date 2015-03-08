package com.c13.devin.moodfeedandroid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

import retrofit.RestAdapter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends FragmentActivity implements TouchableWrapper.UpdateMapAfterUserInterection {

    GoogleMap googleMap;
    MoodFeedService moodFeedService;
    Subscription createStreamsubscription;
    Subscription getStreamDataSubscription;
    String streamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMap();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://moodfeedapi.herokuapp.com")
                .build();

        moodFeedService = restAdapter.create(MoodFeedService.class);
    }

    @Override
    public void onUpdateMapAfterUserInterection() {

        LatLngBounds curScreen = googleMap.getProjection()
                .getVisibleRegion().latLngBounds;

        double[] northeast = {curScreen.northeast.latitude, curScreen.northeast.longitude};
        double[] southwest = {curScreen.southwest.latitude, curScreen.southwest.longitude};

        MoodFeedOpenStreamRequest moodFeedOpenStreamRequest = new MoodFeedOpenStreamRequest(northeast, southwest);

        if (createStreamsubscription == null || createStreamsubscription.isUnsubscribed()) {
            createStreamsubscription = moodFeedService.startStream(moodFeedOpenStreamRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Error!", e.toString());
                        }

                        @Override
                        public void onNext(String id) {
                            streamId = id;
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        if (createStreamsubscription != null && !createStreamsubscription.isUnsubscribed()) {
            createStreamsubscription.unsubscribe();
        }

        if (getStreamDataSubscription != null && !getStreamDataSubscription.isUnsubscribed()) {
            getStreamDataSubscription.unsubscribe();
        }

        super.onDestroy();
    }
}