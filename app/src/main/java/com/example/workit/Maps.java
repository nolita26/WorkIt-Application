package com.example.workit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class Maps extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


        private GoogleMap mMap;
        private GoogleApiClient client;
        private LocationRequest locationRequest;
        private Location lastlocation;
        private Marker currentLocationmMarker;
        public static final int REQUEST_LOCATION_CODE = 99;
        int PROXIMITY_RADIUS = 10000;
        double latitude, longitude;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_maps);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkLocationPermission();

                }
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                switch (requestCode) {
                        case REQUEST_LOCATION_CODE:
                                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                if (client == null) {
                                                        bulidGoogleApiClient();
                                                }
                                                mMap.setMyLocationEnabled(true);
                                        }
                                } else {
                                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();

                                }
                }
        }

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        bulidGoogleApiClient();
                        mMap.setMyLocationEnabled(true);

                }
        }


        protected synchronized void bulidGoogleApiClient() {
                client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
                client.connect();

        }

        @Override
        public void onLocationChanged(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                lastlocation = location;
                //getDataGym();
                if (currentLocationmMarker != null) {
                        currentLocationmMarker.remove();

                }
                Log.d("lat = ", "" + latitude);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                currentLocationmMarker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

                if (client != null) {
                        LocationServices.FusedLocationApi.removeLocationUpdates(client, (com.google.android.gms.location.LocationListener) this);
                }
        }

        public void getDataGym() {
                Object dataTransfer[] = new Object[2];
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                Log.e("LOCATION",""+latitude+""+longitude);
                mMap.clear();
                String url = getUrl(latitude, longitude, "gym");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(this, "Showing Nearby gym", Toast.LENGTH_SHORT).show();
        }

        private String getUrl(double latitude, double longitude, String nearbyPlace) {

                StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                googlePlaceUrl.append("location=" + latitude + "," + longitude);
                googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
                googlePlaceUrl.append("&type=" + nearbyPlace);
                googlePlaceUrl.append("&sensor=true");
                googlePlaceUrl.append("&key=" + getString(R.string.map_key));
                Log.d("MapsActivity", "url = " + googlePlaceUrl.toString());
                return googlePlaceUrl.toString();
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {

                locationRequest = new LocationRequest();
                locationRequest.setInterval(100);
                locationRequest.setFastestInterval(1000);
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
                        client.getLastLocation().addOnSuccessListener(Maps.this, location -> {
                                if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                        getDataGym();
                                } else {
                                        // Handle null case or Request periodic location update https://developer.android.com/training/location/receive-location-updates
                                }
                        });
                }
        }


        public boolean checkLocationPermission() {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
                        } else {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
                        }
                        return false;

                } else
                        return true;
        }


        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        }

        public void back(View view) {
                finish();
        }
}