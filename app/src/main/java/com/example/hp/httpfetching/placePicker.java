package com.example.hp.httpfetching;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlacePicker;


public class placePicker extends FragmentActivity implements OnMapReadyCallback
{
    GPSTracker gps;
    int PLACE_PICKER_REQUEST=101;
    private GoogleMap mMap;
    double latitude,longitude;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == PLACE_PICKER_REQUEST) {
//            {
//                Place place = PlacePicker.getPlace(data, this);
//                double lattitude = place.getLatLng().latitude;
//                double longitude = place.getLatLng().longitude;
//                String p = (String) place.getName();
//                LatLng ltlng = new LatLng(lattitude,longitude);
//                mMap.addMarker(new MarkerOptions().position(ltlng));
//                String toastMsg = String.format("Place: %s", place.getName());
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//            }
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
//        try {
//            startActivityForResult(intentBuilder.build(placePicker.this),PLACE_PICKER_REQUEST);
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
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
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        gps=new GPSTracker(placePicker.this);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(gps.latitude, gps.longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                LatLng loc= marker.getPosition();
                latitude = loc.latitude;
                longitude = loc.longitude;
                Toast.makeText(getApplicationContext(),""+latitude,Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}