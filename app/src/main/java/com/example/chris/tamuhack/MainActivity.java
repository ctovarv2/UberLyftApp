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
import android.content.pm.PackageManager;
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
import android.preference.DialogPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.ArrayList;
import java.util.List;

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

        LinearLayout uberLayout = (LinearLayout) findViewById(R.id.uber_selections_layout);
        LinearLayout lyftLayout = (LinearLayout) findViewById(R.id.lyft_selections_layout);
        uberLayout.removeAllViews();
        lyftLayout.removeAllViews();

        // Action Button (Bottom Left Corner)
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showing == true){
                    findViewById(R.id.uber_selections).setVisibility(view.INVISIBLE);
                    findViewById(R.id.lyft_selections).setVisibility(view.INVISIBLE);
//                    Snackbar.make(view, "Minimized Rides.", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    fab.setImageResource(android.R.drawable.arrow_up_float);
                    showing = false;
                } else {
                    findViewById(R.id.uber_selections).setVisibility(view.VISIBLE);
                    /*Snackbar.make(view, "Confirm Ride Selection.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/
                    findViewById(R.id.lyft_selections).setVisibility(view.VISIBLE);
                    /*Snackbar.make(view, "Confirm Ride Selection.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/
                    fab.setImageResource(android.R.drawable.arrow_down_float);
                    showing = true;
                }
            }
        });

        // Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
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

                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(midpoint, zoom));

                // if necessary adjust zoom to encompass the two markers
                /*while(!gMap.getProjection().getVisibleRegion().latLngBounds.contains(userCoords) ||
                        !gMap.getProjection().getVisibleRegion().latLngBounds.contains(destinationCoords)){

                    System.out.println("NOT INCLUDED.");
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(midpoint, zoom - 1));
                }*/

                // Get the available ubers and lyfts & filtered
                List<Uber> availableUbers = RideUtils.getAvailableUbers(userCoords.latitude, userCoords.longitude, destinationCoords.latitude, destinationCoords.longitude);
                List<Lyft> availableLyfts = RideUtils.getAvailableLyfts(userCoords.latitude, userCoords.longitude, destinationCoords.latitude, destinationCoords.longitude);
                Uber shortestUber = RideUtils.getShortestUber(availableUbers);
                Uber cheapestUber = RideUtils.getCheapestUber(availableUbers);
                Lyft shortestLyft = RideUtils.getShortestLyft(availableLyfts);
                Lyft cheapestLyft = RideUtils.getCheapestLyft(availableLyfts);

                // Create buttons appropriately for ubers and lyfts
                LinearLayout uberLayout = (LinearLayout)findViewById(R.id.uber_selections_layout);
                uberLayout.removeAllViews();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                for(Uber uber : availableUbers) {
                    Button uberButton = new Button(MainActivity.this);
                    String timeEstimate = Integer.toString(uber.getTimeEstimate());
                    if (timeEstimate.equals("-1")) {
                        timeEstimate = "N/A";
                    } else {
                        timeEstimate += " Minutes";
                    }
                    String priceEstimate = uber.getPriceEstimate();
                    if (priceEstimate == null) {
                        priceEstimate = "N/A";
                    }
                    uberButton.setText(uber.getVehicleType() + "\n" + timeEstimate + "\n" + priceEstimate);
                    uberButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RedirectUtils.deepLinkIntoUber(destinationCoords.latitude, destinationCoords.longitude, MainActivity.this, MainActivity.this);
                        }
                    });
                    uberLayout.addView(uberButton, layoutParams);
                }

                LinearLayout lyftLayout = (LinearLayout)findViewById(R.id.lyft_selections_layout);
                lyftLayout.removeAllViews();
                for(Lyft lyft : availableLyfts) {
                    Button lyftButton = new Button(MainActivity.this);
                    String timeEstimate = Integer.toString(lyft.getTimeEstimate());
                    if (timeEstimate.equals("-1")) {
                        timeEstimate = "N/A";
                    } else {
                        timeEstimate += " Minutes";
                    }
                    String priceEstimate = lyft.getPriceEstimate();
                    if (priceEstimate == null) {
                        priceEstimate = "N/A";
                    }
                    lyftButton.setText(lyft.getVehicleType() + "\n" + timeEstimate + "\n" + priceEstimate);
                    lyftButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RedirectUtils.deepLinkIntoLyft(userCoords.latitude, userCoords.longitude, destinationCoords.latitude, destinationCoords.longitude, MainActivity.this, MainActivity.this);
                        }
                    });
                    lyftLayout.addView(lyftButton, layoutParams);
                }
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

        // Once We Have Location Services Enabled By Default, Get Location
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

        //gMap.setTrafficEnabled(true);

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCoords, zoom));

        gMap.addMarker(new MarkerOptions().position(userCoords)
            .title(userCoords + "Your Location."));
    }

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
            System.out.println("hi");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("TAMUHack 2018. \n\nTeam Members: \n\t\t\t\tTyler Carlson \n\t\t\t\tChristian Tovar \n\t\t\t\tJuan Vasquez")
                    .setTitle("About")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            // handle the clicking
                        }
                    });
            builder.create();
            builder.show();
        } else if (id == R.id.nav_settings) {
            //            MAP_TYPE_NORMAL: Basic map.
            //            MAP_TYPE_SATELLITE: Satellite imagery.
            //            MAP_TYPE_HYBRID: Satellite imagery with roads and labels.
            //            MAP_TYPE_TERRAIN: Topographic data.
            //            MAP_TYPE_NONE: No base map tiles.

            final CharSequence[] modes = {
                    "Normal", "Satellite", "Hybrid", "Topological", "None"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Map Style")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            // handle the clicking
                        }
                    })
                    .setItems(modes, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                    break;
                                case 1:
                                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                    break;
                                case 2:
                                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                    break;
                                case 3:
                                    gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                    break;
                                case 4:
                                    gMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                                    break;
                                default:
                                    gMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                                    break;
                            }
                        }
                    });

            builder.create();
            builder.show();
        } else if (id == R.id.nav_traffic) {
            final CharSequence[] traffic = {
                    "Traffic"
            };

            final ArrayList seletedItems = new ArrayList();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable Traffic Data")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            // handle the clicking
                        }
                    })
                    .setMultiChoiceItems(traffic, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                seletedItems.add(indexSelected);
                                gMap.setTrafficEnabled(true);
                            } else if (seletedItems.contains(indexSelected)) {
                                // Else, if the item is already in the array, remove it
                                seletedItems.remove(Integer.valueOf(indexSelected));
                                gMap.setTrafficEnabled(false);
                            }
                        }
                    });
            builder.create();
            builder.show();
        } /*else if (id == R.id.nav_manage) {

        }*/ else if (id == R.id.nav_share) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Share your insightful information with us (kipvasq9@tamu.edu) or to " +
                    "your friends!")
                    .setTitle("Share")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            // handle the clicking
                        }
                    });
            builder.create();
            builder.show();
        } else if (id == R.id.nav_send) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Share your harsh experiences with us! (kipvasq9@tamu.edu)")
                    .setTitle("Report Problem")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            // handle the clicking
                        }
                    });
            builder.create();
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
