package com.agmohammed.smarttaxi;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.TimeZoneFormat;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

import javax.xml.transform.Source;

public class ScheduleRide extends AppCompatActivity  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private Button mCancel, mNext, mSchedule;
    private DatePicker mDate;
    private TimePicker mTime;
    private Spinner sAvailableSeats;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;

    private String source, destination;

    private SupportMapFragment mapFragment;

    private LatLng SourceLatLng, DestinationLatLng;

    private LinearLayout timeLayout;
    private LinearLayout placeLayout;

    private DatabaseReference mDriverDatabase;

    private static final String TAG = "ScheduleRide";

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    public static ScheduleRide newInstance() {
        ScheduleRide fragment = new ScheduleRide();
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_ride);


        mCancel = (Button) findViewById(R.id.cancel);
        mNext = (Button) findViewById(R.id.next);
        mSchedule = (Button) findViewById(R.id.btnSchedule);
        mDate = (DatePicker) findViewById(R.id.scheduleDate);
        mTime = (TimePicker) findViewById(R.id.scheduleTime);

        timeLayout = (LinearLayout) findViewById(R.id.timeLayout);
        placeLayout = (LinearLayout) findViewById(R.id.placeLayout);

        sAvailableSeats = (Spinner) findViewById(R.id.availableSeats);

        String[] availableSeatsItems = new String[]{"1", "2", "3", "4"};
        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, availableSeatsItems);
        sAvailableSeats.setAdapter(adapter5);

        polylines = new ArrayList<>();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("RideInit").child("Scheduled").child(userId);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScheduleRide.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mapFragment.getMapAsync(this);
        }

        PlaceAutocompleteFragment autocompleteFragmentSource = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_source);

        PlaceAutocompleteFragment autocompleteFragmentDestination = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_destination);

        autocompleteFragmentSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                source = place.getName().toString();
                SourceLatLng = place.getLatLng();
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination = place.getName().toString();
                DestinationLatLng = place.getLatLng();
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent m = new Intent(ScheduleRide.this, DriverMapActivity.class);
                startActivity(m);
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timeLayout.setVisibility(View.GONE);
            }
        });



        mSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();

                calendar.set(
                        mDate.getYear(),
                        mDate.getMonth(),
                        mDate.getDayOfMonth(),
                        mTime.getHour(),
                        mTime.getMinute()-15,
                        0
                );

                setAlarm(calendar.getTimeInMillis());


                Date date = new Date();
                date.setMonth(mDate.getMonth());
                date.setYear(mDate.getYear()-1900);
                date.setDate(mDate.getDayOfMonth());

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                String dateString = format.format(date);


                Date time = new Date();
                time.setHours(mTime.getCurrentHour());
                time.setMinutes(mTime.getCurrentMinute());

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("RideInit");
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("RideInit").child("Scheduled").child(userId);
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation("SourceLatLng", new GeoLocation(SourceLatLng.latitude, SourceLatLng.longitude));

                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("RideInit").child("Scheduled").child(userId);
                GeoFire geoFire2 = new GeoFire(ref2);
                geoFire2.setLocation("DestinationLatLng", new GeoLocation(DestinationLatLng.latitude, DestinationLatLng.longitude));


                String timeString = timeFormat.format(time);

                String availableSeats = sAvailableSeats.getSelectedItem().toString();

                Map rideInfo = new HashMap();
                rideInfo.put("Date", dateString);
                rideInfo.put("Time", timeString);
                rideInfo.put("Available Seats", availableSeats);
                mDriverDatabase.updateChildren(rideInfo);

                getRouteToDestination();
                zoomLocationCamera();

            }
        });

    }

    public void zoomLocationCamera(){

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SourceLatLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(8));
    }

    private void setAlarm(long timeInMillis) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyBroadcastReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    mapFragment.getMapAsync(ScheduleRide.this);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }

        }
    }

    private void getRouteToDestination() {

        Routing routing = new Routing.Builder()
                .key("AIzaSyAFTpZoPPk_mUg74fKzqTAN9qVlP15RDhw")
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(SourceLatLng, DestinationLatLng)
                .build();
        routing.execute();
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolylines(){
        for (Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

}
