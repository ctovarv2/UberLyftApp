package com.example.chris.tamuhack;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    OnMapReadyCallback {

    // Default Coordinates in College Station
    public static LatLng userCoords = new LatLng (30.581999499156245, -96.33768982383424);

    // Uber/Lyft Information
    private static final String TAG = "";
    public static String INPUT_ADDRESS = " ";
    public static LatLng destinationCoords = new LatLng(0, 0);

    // Google Map Reference
    GoogleMap gMap;
    int zoom = 7;

    // Information Presentation
    boolean showing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Action Button (Bottom Left Corner)
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(showing == true){
//                    findViewById(R.id.selections).setVisibility(view.INVISIBLE);
//                    /*Snackbar.make(view, "Minimized Rides.", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();*/
//                    showing = false;
//                } else {
                    findViewById(R.id.selections).setVisibility(view.VISIBLE);
                    Snackbar.make(view, "Confirm Ride Selection.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    showing = true;
                    Button myButton = new Button(MainActivity.this);
                    myButton.setText("Push Me");

                    LinearLayout ll = (LinearLayout)findViewById(R.id.horizLayout);
//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    ll.addView(myButton);
//                }
            }
        });

        // Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Search Bar
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // handle selected location
                System.out.println("Location: " + place.getAddress());
                System.out.println("\t\t" + place.getLatLng());

                // clear all existing markers
                gMap.clear();

                // add current location to map
                gMap.addMarker(new MarkerOptions().position(userCoords)
                        .title("Your Location."));

                // add destination to map
                destinationCoords = place.getLatLng();
                gMap.addMarker(new MarkerOptions().position(place.getLatLng())
                        .title("Destination."));

                // new camera placement
                LatLng midpoint = new LatLng((destinationCoords.latitude + userCoords.latitude) / 2.0,
                        (destinationCoords.longitude + userCoords.longitude) / 2.0);

                //System.out.println("Dest: " + destinationCoords + "\n, User:" + userCoords + "\n, Midpoint: " + midpoint);

                // move camera to midpoint with default zoom
                // zoom = 7;
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(midpoint, zoom));

                // if necessary adjust zoom to encompass the two markers

                /*while(!gMap.getProjection().getVisibleRegion().latLngBounds.contains(userCoords) ||
                        !gMap.getProjection().getVisibleRegion().latLngBounds.contains(destinationCoords)){

                    System.out.println("NOT INCLUDED.");
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(midpoint, zoom - 1));
                }*/

                System.out.println("ZOOM: " + zoom);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                System.out.println("No location found. Error: " + status);
            }
        });

        // User Location Request (If Necessary)
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permissions Already Given");
        } else{
            // ask for permissions
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location;

        if (network_enabled == true) {
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                userCoords = new LatLng(location.getLatitude(), location.getLongitude());

                System.out.println("----------------------------");
                System.out.println("User Lat/Long: " + userCoords);
                System.out.println("----------------------------");
            }
        }

        // Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        // If locations are not initially enabled, the result of the user's decision to enable
        // location services is reported by this function.

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                System.out.println("Location Permission Granted.");
            }
            else{
                System.out.println("Location Permission Denied.");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        gMap = googleMap;

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCoords, zoom));

        googleMap.addMarker(new MarkerOptions().position(userCoords)
            .title(userCoords + "Your Location."));
    }

//    public void onMapReady(GoogleMap map){
//        mMap = map;
//
//        onComplete();
//    }

//    public void onComplete(){
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                new LatLng(userLatitude, userLongitude), 15));
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void saveAddress(View view){


//        TextView tv1 = (TextView)findViewById(R.id.textView3);
//        EditText inputField = (EditText) findViewById(R.id.editText);
//        String address = inputField.getText().toString();
//
//        INPUT_ADDRESS = address;
//
//        tv1.setText(INPUT_ADDRESS);

        //Fragment fragmentById = getFragmentManager().findFragmentById(R.id.place_autocomplet‌​e_fragment);


    }
}
