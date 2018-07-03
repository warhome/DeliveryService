package com.example.misaka.deliveryservice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Button btn;
    String coordinates;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean isLocationPermissionGranted = false;
    public static final int LOCATION_PERMISSION_REQUEST = 1212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();

        btn = findViewById(R.id.mapBtn);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("coordinates",coordinates);
            setResult(RESULT_OK,intent);
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
        if(intent.hasExtra("isRecyclerIntent")) {
            btn.setVisibility(View.INVISIBLE);
        }
        else {
            mMap.setOnMapClickListener(latLng -> {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
                coordinates = String.valueOf(latLng.latitude) + ',' + String.valueOf(latLng.longitude);
                Toast.makeText(this, latLng.toString(), Toast.LENGTH_SHORT).show();
            });
        }

        if(intent.hasExtra("coordinates")) {
            String[] markerCoordinates = intent.getStringExtra("coordinates").split(",");
            LatLng latLng = new LatLng(Double.valueOf(markerCoordinates[0]), Double.valueOf(markerCoordinates[1]));
            mMap.addMarker(new MarkerOptions().position(latLng));
            moveCamera(latLng, 15f);
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
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f);
                    }
                    else{
                        Toast.makeText(this, "Unsuccessful call", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (SecurityException e) {
            Toast.makeText(this, "Не предоставлены права доступа", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

}
