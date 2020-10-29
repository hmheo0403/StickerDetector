package com.cfd.map.mohit.locationalarm.locationalarm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cfd.map.mohit.locationalarm.locationalarm.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomPlacePicker extends AppCompatActivity implements OnMapReadyCallback {

    MapFragment mapFragment;
    private GoogleMap mMap;
    PlaceAutocompleteFragment autocompleteFragment;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_place_picker);
        //sets volume controls to handle alarm volume
        this.setVolumeControlStream(AudioManager.STREAM_ALARM);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                marker.setPosition(place.getLatLng());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }
            @Override
            public void onError(Status status) {

            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(-34, 151))
                .title("Marker"));
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng lat = map.getCameraPosition().target;
                marker.setPosition(lat);
            }
        });
        checkAndSetLocation();
        Log.d("permission", "already have permission");

    }

    public void setLocation(View view) {
        LatLng pos = marker.getPosition();
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = "";
        if(!addresses.isEmpty()){
            address = addresses.get(0).getAddressLine(0);
        }
        Intent intent = new Intent();
        intent.putExtra("latitude", pos.latitude);
        intent.putExtra("longitude", pos.longitude);
        intent.putExtra("address", address);
        setResult(RESULT_OK, intent);
        finish();
    }
    public void checkAndSetLocation(){
        LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Toast.makeText(this, "Please Enable Your GPS", Toast.LENGTH_SHORT).show();
        }else {
            if (ActivityCompat.checkSelfPermission(CustomPlacePicker.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
            }
        }
    }
}
