package com.fabcuts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener

{

    RelativeLayout uplayout;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    ProgressBar progressBar;
    private Marker currentlocmarker;
    public static final int REQUEST_LOCATION_CODE=99;
    List<Address> addressList=null;

    DatabaseReference refsaloon,refwaittimeofbarbers;

    Double currentLatitude,currentLongitude;
    //TextView tv_currentLoc;
    String currentLocation;
    LatLng latLng,m;
    MarkerOptions markerOptions;

    int count=0,check=0;
    AlertDialog alert;
    private DatabaseReference mDatabase;
    CardView cvdetails;
    TextView nameofsalon,waittimeofsalon,addressofsalon;
    Button proceed;
    int waittime;
    int mintimeofbarber=0,i=0,index=0;
    String[] names,phones;                   // <--declared statement


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            checkLocationPermission();
        }


        names = new String[1000];
        phones=new String[1000];

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cvdetails=findViewById(R.id.cvdetails);
        nameofsalon=findViewById(R.id.tvnamemap);
        waittimeofsalon=findViewById(R.id.tvwaitmap);
        addressofsalon=findViewById(R.id.tvaddressmap);
        proceed=findViewById(R.id.bgotosalon);

    }

    private void checkwaittime(String mob) {
        FirebaseDatabase.getInstance().getReference().child("saloon").child(mob).child("waittime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    waittime=dataSnapshot.getValue(Integer.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSaloonData() {

        refsaloon=FirebaseDatabase.getInstance().getReference().child("saloons");
       refsaloon.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   //Toast.makeText(MapsActivity.this, snapshot.child("latitude").getValue(Double.class).toString(), Toast.LENGTH_SHORT).show();

                   float[] results = new float[1];
                   Location.distanceBetween(latLng.latitude, latLng.longitude, snapshot.child("latitude").getValue(Double.class),
                           snapshot.child("latitude").getValue(Double.class), results);
                   int distance = (int) results[0];
                       //Toast.makeText(MapsActivity.this, distance+"", Toast.LENGTH_SHORT).show();

                   if (snapshot.child("status").exists())
 //                      if (distance<10)
                       placeMarkerForSaloon(snapshot.child("latitude").getValue(Double.class),snapshot.child("longitude").getValue(Double.class),
                               snapshot.child("name").getValue(String.class),snapshot.child("mob").getValue(String.class));
                       //names[index]=snapshot.child("name").getValue(String.class);


               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void placeMarkerForSaloon(Double latitude, Double longitude,String name,String phone) {

        /*LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_layout_marker, null);*/

        markerOptions=new MarkerOptions();
        LatLng ll=new LatLng(latitude,longitude);
        markerOptions.position(ll);
        markerOptions.title(name);
        markerOptions.snippet(phone);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMap.addMarker(markerOptions);
        //Toast.makeText(this, "placed "+name, Toast.LENGTH_SHORT).show();


        /*LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_custom_marker, null);

        m = mMap
                .addMarker(new MarkerOptions()
                        .position(ll)
                        .title(name)
                        .icon(BitmapDescriptorFactory.fromBitmap();*/

    }




    @Override
    protected void onPause() {
        super.onPause();
        if (alert!=null)
            alert.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //NetworkCheck();
        displayLocationSettingsRequest(this);

    }


    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo()!=null
                &&connectivityManager.getActiveNetworkInfo().isConnected();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {
                        if (client==null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                }
                else
                {
                    Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient(){
        client=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest=new LocationRequest();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onLocationChanged(final Location location) {
        mMap.clear();
        count++;
        if (count==1)
        {

        if (currentlocmarker != null) {
            currentlocmarker.remove();
        }


        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(this);
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 1200, null);
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Your Location");
            //mMap.addMarker(markerOptions);
            //drawCircle(latLng);
            addressList = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
            currentLocation = addressList.get(0).getAddressLine(0);
            //Toast.makeText(this, addressList.get(0).getLocality()+"", Toast.LENGTH_SHORT).show();
            //tv_currentLoc.setText(currentLocation);
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("city", addressList.get(0).getLocality());
            editor.putString("address", currentLocation);
            editor.apply();

            getSaloonData();
            locationRequest.setInterval(100000);
            client.disconnect();
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {

                    /*    startActivity(new Intent(getApplicationContext(),SalonServices.class)
                                .putExtra("mob",marker.getSnippet())
                                .putExtra("waittime",waittime)
                                .putExtra("name",marker.getTitle()));
*/

                    //marker.hideInfoWindow();
                    initializecard(marker.getSnippet(), marker.getTitle());
                        /*FirebaseDatabase.getInstance().getReference().child("saloons").child(marker.getSnippet()).child("waittime")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            waittime=dataSnapshot.getValue(Integer.class);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });*/

                    return false;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    else {
            client.disconnect();
        }

    }

    private void initializecard(final String mob , final String name) {
        waittimeofsalon.setText("");
        cvdetails.setVisibility(View.VISIBLE);
        nameofsalon.setText(name);
        FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    addressofsalon.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SalonServices.class)
                        .putExtra("mob",mob)
                        .putExtra("name",name));
            }
        });
        findwaittimesofbarbers(mob);

    }

    private void findwaittimesofbarbers(String mob) {

        refwaittimeofbarbers=FirebaseDatabase.getInstance().getReference().child("saloons").child(mob)
                .child("barbers");
        refwaittimeofbarbers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if (snapshot.child("status").getValue(Integer.class)==1){
                        i++;
                        if (i==1){
                            mintimeofbarber=snapshot.child("time").getValue(Integer.class);
                            //nameofbarber=snapshot.child("name").getValue(String.class);
                        }
                        else if (i!=1){

                            if (mintimeofbarber>=snapshot.child("time").getValue(Integer.class))
                            {
                                mintimeofbarber=snapshot.child("time").getValue(Integer.class);
                                //nameofbarber=snapshot.child("name").getValue(String.class);
                            }
                        }
                    }


                }

                waittimeofsalon.setText(mintimeofbarber+" min.");

                //checkmein.setVisibility(View.VISIBLE);
                //tvwait.setText(tvwait.getText().toString()+""+mintimeofbarber+" minutes");
                //Toast.makeText(AboutSalon.this, mintimeofbarber+" min", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(1000);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }


    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }



    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        /*locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(50000);
        locationRequest.setFastestInterval(50000 / 2);*/

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            status.startResolutionForResult(MapsActivity.this, REQUEST_LOCATION_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            return;
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(MapsActivity.this,
                FirstActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}