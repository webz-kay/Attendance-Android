package com.example.nanoatt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.nanoatt.utils.Urls;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    LocationRequest mLocationRequest;
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    GoogleMap gMap;
    FancyButton checkin_btn, cancel_btn, btn_checkout;
    TextView txtNames,txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        checkLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkin_btn = findViewById(R.id.btn_checkin);
        cancel_btn = findViewById(R.id.btn_attendance);
        btn_checkout = findViewById(R.id.btn_checkout);
        txtEmail = findViewById(R.id.txtEmail);
        txtNames = findViewById(R.id.txtName);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestForGPSUpdates();

        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        int id = prefs.getInt("checkin_id", 0);
        txtNames.setText(prefs.getString("name",""));
        txtEmail.setText(prefs.getString("email",""));
        if (id == 0) {
            checkin_btn.setVisibility(View.VISIBLE);
            btn_checkout.setVisibility(View.GONE);
        } else {
            checkin_btn.setVisibility(View.GONE);
            btn_checkout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        gMap.setMyLocationEnabled(true);
        LatLng nairobi = new LatLng(-1.266730, 36.805440);
        gMap.addMarker(new MarkerOptions().position(nairobi).title("Kipro Center"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nairobi, 16));
        // Zoom in, animating the camera.
        gMap.animateCamera(CameraUpdateFactory.newLatLng(nairobi));

        /*//trial
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(nairobi)
                .zoom(50)
                .bearing(70)
                .tilt(25)
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                cameraPosition));*/
    }

    private void requestForGPSUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            //mMap.setMyLocationEnabled(true);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

    }

    /**
     * Core of the app. The location callback handles location updates. If The GPS location changes, that GPS data is brought to This function
     */
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (gMap != null) {
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));
                }

                //Do something with coordinates
//                txtLati.setText(mLastLocation.getLatitude()+"");
//                txtLong.setText(mLastLocation.getLongitude()+"");
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void check_in_btn(View view) {
        if (mLastLocation != null) {
            Toast.makeText(this, "Lat " + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
            int id = prefs.getInt("id", 0);
            String name = prefs.getString("name", "");
            String email = prefs.getString("email", "");

            AndroidNetworking.post(Urls.CHECKIN_URL)
                    .addBodyParameter("user_id", String.valueOf(id))
                    .addBodyParameter("lat", mLastLocation.getLatitude()+"")
                    .addBodyParameter("lng", mLastLocation.getLongitude()+"")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: "+response.toString());
                            try {
                                if (response.getBoolean("success")) {
                                    checkin_btn.setVisibility(View.GONE);
                                    btn_checkout.setVisibility(View.VISIBLE);
                                    int checkin_id = response.getJSONObject("attendance").getInt("id");
                                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                    editor.putInt("checkin_id", checkin_id);
                                    editor.apply();
                                } else {
                                    Toast.makeText(MapActivity.this, "Sorry could not checkin. Try Again", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.d(TAG, "onError: "+anError.getErrorBody());
                            Log.d(TAG, "onError: "+anError.getMessage());
                            Log.d(TAG, "onError: "+anError.getResponse());
                            Toast.makeText(MapActivity.this, "Error. Sorry could not checkin. Try Again. ", Toast.LENGTH_SHORT).show();

                        }
                    });


        } else {
            Toast.makeText(this, "Setting Up GPS Location", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel_btn(View view) {

    }
    String TAG="MAPS_ACTIVITY";

    public void checkout_btn(View view) {
        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        int checkin_id = prefs.getInt("checkin_id", 0);
        int user_id = prefs.getInt("id", 0);
        if (checkin_id>0 && user_id>0){
            AndroidNetworking.post(Urls.CHECKOUT_URL)
                    .addBodyParameter("user_id", String.valueOf(user_id))
                    .addBodyParameter("attendance_id", String.valueOf(checkin_id))
                    .addBodyParameter("lat_out", mLastLocation.getLatitude()+"")
                    .addBodyParameter("long_out", mLastLocation.getLongitude()+"")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    checkin_btn.setVisibility(View.VISIBLE);
                                    btn_checkout.setVisibility(View.GONE);
                                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                    editor.remove("checkin_id");
                                    editor.apply();
                                    Toast.makeText(MapActivity.this, "Checked Out Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "onResponse: "+response.toString());
                                    Toast.makeText(MapActivity.this, "Sorry could not checkout. Try Again", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.d(TAG, "onError: "+anError.getErrorBody());
                            Log.d(TAG, "onError: "+anError.getMessage());
                            Log.d(TAG, "onError: "+anError.getResponse());
                            Toast.makeText(MapActivity.this, "Error. Sorry could not checkout. Try Again. ", Toast.LENGTH_SHORT).show();

                        }
                    });

        }

    }
    //comment
    public  void attendance_btn(View v){
        Intent intent=new Intent(MapActivity.this, AttendanceActivity.class);
        startActivity(intent);

    }
}
