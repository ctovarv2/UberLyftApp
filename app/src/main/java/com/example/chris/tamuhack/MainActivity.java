package com.example.chris.tamuhack;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "";
    public static String INPUT_ADDRESS = " ";

    public static Double userLongitude = 0.0;
    public static Double userLatitude = 0.0;

    GoogleMap mMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Confirm Ride Selection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*List<String> responses = UberUtils.makeRequests("https://api.uber.com/v1.2/estimates/time?start_latitude=30.615011&start_longitude=-96.342476", "https://api.uber.com/v1.2/estimates/price?start_latitude=30.615011&start_longitude=-96.342476&end_latitude=30.591330&end_longitude=-96.344744");
        if(responses != null) {
            String uberTimes = responses.get(0);
            String uberPrices = responses.get(1);
            try {
                List<Uber> ubers = JsonParser.getAvailableUbers(uberTimes, uberPrices);
                System.out.println("--- Uber Info ---");
                for (Uber uber: ubers) {
                    System.out.println("Type: " + uber.getVehicleType());
                    System.out.println("Time: " + uber.getTimeEstimate());
                    System.out.println("Price: " + uber.getPriceEstimate());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                System.out.println("Place: " + place.getAddress());
                System.out.println("Place: " + place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                System.out.println("An error occurred: " + status);
            }
        });

        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location;

        if(network_enabled == true){
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(location != null){
                userLongitude = location.getLongitude();
                userLatitude = location.getLatitude();

                System.out.println("----------------------------");
                System.out.println("User Lat/Long: " + userLatitude + ", " + userLongitude);
                System.out.println("----------------------------");


            }
        }

        //For Map



//        setContentView(R.layout.activity_maps);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

    }

    public void onMapReady(GoogleMap map){
        mMap = map;

        onComplete();
    }

    public void onComplete(){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(userLatitude, userLongitude), 15));
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
