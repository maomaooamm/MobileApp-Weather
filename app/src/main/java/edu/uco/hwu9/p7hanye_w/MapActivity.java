package edu.uco.hwu9.p7hanye_w;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap map;
    private LatLng cityPosition;
    private String city;
    private Double temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        temperature = intent.getDoubleExtra("temperature", 0.0);
        double a = intent.getDoubleExtra("a", 0.0);
        double b = intent.getDoubleExtra("b", 0.0);
        cityPosition = new LatLng(a,b);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        String msg = city + " " + temperature + "°C";
        CameraPosition camera = new CameraPosition.Builder()
                .target(cityPosition).zoom(12).build();
        map.getUiSettings().setZoomControlsEnabled(true);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        map.clear();
        map.addMarker(
                new MarkerOptions().position(cityPosition).title(msg));
    }
}

