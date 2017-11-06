package com.example.tawin.testmapgps;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private String path = "http://192.168.1.30/ProjectJ/test.php";
    Marker mMarker;
    LocationManager lm;
    double lat, lng;
    ArrayList<Data_Event> countries = new ArrayList<Data_Event>();
    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;

    /**
     * A {@link LocationSource} which reports a new location whenever a user long presses the map
     * at
     * the point at which a user long pressed the map.
     */
    private static class LongPressLocationSource implements LocationSource, GoogleMap.OnMapLongClickListener {

        private OnLocationChangedListener mListener;

        /**
         * Flag to keep track of the activity's lifecycle. This is not strictly necessary in this
         * case because onMapLongPress events don't occur while the activity containing the map is
         * paused but is included to demonstrate best practices (e.g., if a background service were
         * to be used).
         */
        private boolean mPaused;

        @Override
        public void activate(OnLocationChangedListener listener) {
            mListener = listener;
        }

        @Override
        public void deactivate() {
            mListener = null;
        }

        @Override
        public void onMapLongClick(LatLng point) {
            if (mListener != null && !mPaused) {
                Location location = new Location("LongPressLocationProvider");
                location.setLatitude(point.latitude);
                location.setLongitude(point.longitude);
                location.setAccuracy(100);
                mListener.onLocationChanged(location);
            }
        }

        public void onPause() {
            mPaused = true;
        }

        public void onResume() {
            mPaused = false;
        }
    }


    private EditText locationSearch;

    private Geocoder geocoder;

    private LocationManager locationManager;

    private LongPressLocationSource mLocationSouce;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        Button alertbutton = (Button)findViewById(R.id.PutAlert);
//        alertbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent r = new Intent(getApplicationContext(), putAlert.class);
//                startActivity(r);
//
//
//            }
//        });
//
//        Button logout = (Button)findViewById(R.id.logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                Intent r = new Intent(getApplicationContext(), putAlert.class);
////                startActivity(r);
//                firebaseAuth = FirebaseAuth.getInstance();
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                firebaseAuth.signOut();
//                finish();
//
//            }
//        });

        locationSearch = (EditText) findViewById(R.id.editText);

        geocoder = new Geocoder(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        final LatLng[] sydney = new LatLng[1];
//        enableMyLocation();
//
//        mMap.setLocationSource(mLocationSouce);
//        mMap = googleMap;
//        final LatLng[] sydney2 = new LatLng[1];
//        GpsTracker gt = new GpsTracker(getApplicationContext());
//        MapsActivity callGPS = new MapsActivity();
//        Location l = gt.getLocation();
////        LatLng sydney3 = new LatLng(lat, lng);
////        mMap.addMarker(new MarkerOptions().position(sydney3).title(" You are here "));
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney3));
////        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney3,18));
//
//
//
//        if( l == null){
//            Toast.makeText(getApplicationContext(),"GPS unable to get Value",Toast.LENGTH_SHORT).show();
//        }else {
//
//            double lat = l.getLatitude();
//            double lng = l.getLongitude();
//            Toast.makeText(getApplicationContext(),"GPS Lat = "+lat+"\n lon = "+lng,Toast.LENGTH_SHORT).show();
//
//            // Add a marker in Sydney and move the camera
//
//
//            JsonArrayRequest req = new JsonArrayRequest(path,
//                    new Response.Listener<JSONArray>() {
//                        @Override
//                        public void onResponse(JSONArray response) {
//                            Log.d(TAG, response.toString());
//                            try {
//                                for (int i = 0; i < response.length(); i++) {
//                                    JSONObject jsonobject = response.getJSONObject(i);
//                                    String title = jsonobject.getString("EventName");
//                                    double longitude = jsonobject.getDouble("lng");
//                                    double latitude = jsonobject.getDouble("lat");
//                                    Log.d(TAG, "EventName:" + title);
//                                    Log.d(TAG, "lng" + longitude);
//                                    Log.d(TAG, "lat" + latitude);
//                                    sydney2[0] = new LatLng(latitude, longitude);
//                                    mMap.addMarker(new MarkerOptions().position(sydney2[0]).title(title));
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney2[0]));
//                                    Data_Event countryObj = new Data_Event();
//                                    countries.add(countryObj);
//                                }
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney2[0],15));
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Toast.makeText(getApplicationContext(),
//                                        "Error: " + e.getMessage(),
//                                        Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            });
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            requestQueue.add(req);
//
//
//
////            Intent r = new Intent(getApplicationContext(), MapsActivity.class);
////            startActivity(r);
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final LatLng[] sydney = new LatLng[1];
        final JsonArrayRequest req = new JsonArrayRequest(path,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonobject = response.getJSONObject(i);
                                final int id = jsonobject.getInt("event_id");
                                final String title = jsonobject.getString("event_title");
                                final String description = jsonobject.getString("event_description");
                                final double longitude = jsonobject.getDouble("event_longitude");
                                final double latitude = jsonobject.getDouble("event_latitude");
                                sydney[0] = new LatLng(latitude, longitude);
                                LatLng latLng = new LatLng(latitude, longitude);

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(title)
                                        .snippet(description)
                                );


                                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                    @Override
                                    public View getInfoWindow(Marker marker) {
                                        return null;
                                    }

                                    @Override
                                    public View getInfoContents(Marker marker) {
                                        View v = null;
                                        try {

                                            v = getLayoutInflater().inflate(R.layout.windowlayout, null);
                                            TextView title = v.findViewById(R.id.tv_title);
                                            title.setText(marker.getTitle());
                                            TextView description = v.findViewById(R.id.tv_description);
                                            description.setText(marker.getSnippet());


                                        } catch (Exception ev) {

                                        }
                                        return v;
                                    }
                                });

                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {

//                                        Intent intent = new Intent(MapsActivity.this, ShowDetail.class);
//                                        intent.putExtra("Event", marker.getTitle());
//                                        startActivity(intent);

                                    }
                                });
                            }
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney[0], 6));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }


        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.resetMinMaxZoomPreference();

            return;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }


    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
////                != PackageManager.PERMISSION_GRANTED) {
////            // Permission to access the location is missing.
////            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
////                    Manifest.permission.ACCESS_FINE_LOCATION, true);
//        } else if (mMap != null) {
//            // Access to the location has been granted to the app.
//            mMap.setMyLocationEnabled(true);
//
//            Criteria criteria = new Criteria();
//            String provider = locationManager.getBestProvider(criteria, true);
//            Location location = locationManager.getLastKnownLocation(provider);
//
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//
//
//            mMap.animateCamera(CameraUpdateFactory
//                    .newLatLngZoom(new LatLng(latitude, longitude), 18));
//
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils
                .PermissionDeniedDialog
                .newInstance(true)
                .show(getSupportFragmentManager(), "dialog");
    }
    private FirebaseAuth firebaseAuth;
    public void onMapSearch(View view) {
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location.isEmpty())
            return;
        try {
            addressList = geocoder.getFromLocationName(location, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addressList == null || addressList.isEmpty())
            return;

        Address address = addressList.get(0);


        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


//        firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        firebaseAuth.signOut();
//        finish();
    }
/////////

}
