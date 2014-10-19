package com.c13.devin.mapadventure;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GoogleMap googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        googleMap.setMyLocationEnabled(true);

    }
}