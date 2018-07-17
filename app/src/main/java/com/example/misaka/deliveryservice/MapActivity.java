package com.example.misaka.deliveryservice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.misaka.deliveryservice.Consts.ADDRESS;
import static com.example.misaka.deliveryservice.Consts.COORDINATES;
import static com.example.misaka.deliveryservice.Consts.COORDINATES_DELIMITER;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean isLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST = 1212;
    private static final String IS_RECYCLER_INTENT = "isRecyclerIntent";

    GoogleMap mMap;
    Button btn;
    String coordinates;
    String address;
    ActionBar actionBar;
    LatLng markerLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarMap);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.app_bar_map);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }

        getLocationPermission();

        // TODO: Починить вылет
        btn = findViewById(R.id.mapBtn);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent();
            if(coordinates != null && !coordinates.isEmpty()) {
                intent.putExtra(COORDINATES,coordinates);
                intent.putExtra(ADDRESS, address);
                setResult(RESULT_OK,intent);
            }
            else setResult(RESULT_CANCELED);
            finish();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.Mapfragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();

        // Если activity запущена из recyclerView нам не нужно обрабатывать нажатия на карту
        if(intent.hasExtra(IS_RECYCLER_INTENT)) {
            btn.setVisibility(View.INVISIBLE);
            if(actionBar!=null) actionBar.setDisplayHomeAsUpEnabled(true);

        }
        else {
            mMap.setOnMapClickListener(latLng -> {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
                coordinates = String.valueOf(latLng.latitude) + COORDINATES_DELIMITER + String.valueOf(latLng.longitude);
                getAddress(latLng.latitude, latLng.longitude);
                if(address != null) {Toast.makeText(this, address, Toast.LENGTH_SHORT).show();}
               // Toast.makeText(this, latLng.toString(), Toast.LENGTH_SHORT).show();
            });
        }

        if(intent.hasExtra(COORDINATES)) {
            String[] markerCoordinates = intent.getStringExtra(COORDINATES).split(COORDINATES_DELIMITER);
            markerLatLng = new LatLng(Double.valueOf(markerCoordinates[0]), Double.valueOf(markerCoordinates[1]));
            mMap.addMarker(new MarkerOptions().position(markerLatLng));
            moveCamera(markerLatLng);
        }
        else {
            getDeviceLocation();
        }

    }

    private void getLocationPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this,FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this,COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,permission, LOCATION_PERMISSION_REQUEST);
            }
        }else {
            ActivityCompat.requestPermissions(this,permission, LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       isLocationPermissionGranted = false;
       switch (requestCode) {
           case LOCATION_PERMISSION_REQUEST : {
               if(grantResults.length > 0) {
                   for (int grantResult : grantResults) {
                       if (grantResult != PackageManager.PERMISSION_GRANTED) {
                           isLocationPermissionGranted = false;
                           return;
                       }
                   }
                    isLocationPermissionGranted = true;
               }
           }
       }
    }

    private void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if(isLocationPermissionGranted) {
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Location currentLocation = task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    }
                    else{
                        Toast.makeText(this, R.string.unseccessful_call, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (SecurityException e) {
            Toast.makeText(this, R.string.permissions_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    private void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder stringBuilderReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    stringBuilderReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                }
                address = stringBuilderReturnedAddress.toString();

            } else {
                Toast.makeText(this, R.string.address_error, Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(this, R.string.ethernet_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
