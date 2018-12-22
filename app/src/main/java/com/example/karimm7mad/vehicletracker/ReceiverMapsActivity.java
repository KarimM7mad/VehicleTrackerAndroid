package com.example.karimm7mad.vehicletracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReceiverMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    public static final String TAG = "asdasd";
    private GoogleMap mMap;
    public String firebaseKeyToUse;
    public DatabaseReference firebaseDBman;
    public LatLng obtained;
    public LocationManager lMan;
    public Criteria currLocCriteria;

    public final static int ReqNo = 900;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_maps);
        this.firebaseKeyToUse = this.getIntent().getStringExtra("userkey");
        this.firebaseDBman = FirebaseDatabase.getInstance().getReference("Cars").child(this.firebaseKeyToUse);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.lMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.defineCurrLocCriteria();
        this.getThePermission();


    }

    @Override
    protected void onStart() {
        super.onStart();
        this.firebaseDBman.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location currLoc = lMan.getLastKnownLocation(lMan.getBestProvider(currLocCriteria, false));
                for (DataSnapshot dn : dataSnapshot.getChildren()) {
                    Car c = dn.getValue(Car.class);
                    Log.d(TAG, "onDataChange:\n" + c);
                    // Add a marker in Sydney and move the camera
                    obtained = new LatLng(c.currentLatitude, c.currentLongitude);
                    mMap.addMarker(new MarkerOptions().position(obtained).title(c.name));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(obtained));
                }


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void defineCurrLocCriteria() {
        this.currLocCriteria = new Criteria();
        this.currLocCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        this.currLocCriteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        this.currLocCriteria.setAltitudeRequired(false);
        this.currLocCriteria.setBearingRequired(true);
        this.currLocCriteria.setSpeedRequired(true);
        this.currLocCriteria.setCostAllowed(false);
    }


    public void getThePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ReceiverMapsActivity.ReqNo);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ReceiverMapsActivity.ReqNo:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getBaseContext(), "ACCESS GRANTED, It's OK now", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getBaseContext(), "ACCESS DENIED, Allow Location Access", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // ON MAP READY CALL BACK INTERFACE FUNCTION
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    //LOCATION LISTENER INTERFACE FUNCTIONS
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}