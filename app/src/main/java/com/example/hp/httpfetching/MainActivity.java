package com.example.hp.httpfetching;
import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class MainActivity extends AppCompatActivity
{
    GPSTracker gps;
    double lat=0.0,longit=0.0;
    double dist=0.0,bear=0.0;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button my,car,call,park;
    TextView myLat,myLong,carLat,carLong;
    Bundle extras;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission= Manifest.permission.ACCESS_FINE_LOCATION;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        setContentView(R.layout.activity_main);
        my= (Button) findViewById(R.id.button2);
        car= (Button) findViewById(R.id.button3);
        park= (Button) findViewById(R.id.button4);
        call= (Button) findViewById(R.id.button5);
        myLat= (TextView) findViewById(R.id.textView);
        myLong= (TextView) findViewById(R.id.textView2);
        carLat= (TextView) findViewById(R.id.textView3);
        carLong= (TextView) findViewById(R.id.textView4);
        extras=new Bundle();
        try
        {
            if (ActivityCompat.checkSelfPermission(this, mPermission) != MockPackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{mPermission},REQUEST_CODE_PERMISSION);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        my.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gps=new GPSTracker(MainActivity.this);
                if(gps.canGetLocation())
                {
                    double latitude=gps.latitude;
                    double longitude=gps.longitude;
                    myLat.setText("Latitude = "+latitude);
                    myLong.setText("Longitude = "+longitude);
                    saveLoc();
                }
                else
                {
                    gps.showSettingsAlert();
                }
            }
        });
        car.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getdata();
            }
        });
        call.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i1=new Intent(MainActivity.this,Calling.class);
                i1.putExtras(extras);
                startActivity(i1);
            }
        });
        park.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,Parking.class);
                startActivity(intent);
            }
        });
    }
    public void saveLoc()
    {
        String lattitude =""+gps.latitude;
        String longitude =""+gps.longitude;
        MyInformation myInformation = new MyInformation(lattitude,longitude);
        databaseReference.child("User").setValue(myInformation);
    }
    public void getdata()
    {
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String A=dataSnapshot.child("Car").child("Lattitude").getValue(String.class);
                String B=dataSnapshot.child("Car").child("Longitude").getValue(String.class);
                if(A.length()!=0&&B.length()!=0)
                {
                    lat = Double.parseDouble(A);
                    longit = Double.parseDouble(B);
                }
                else
                {
                    lat=lat;
                    longit=longit;
                }
                carLat.setText("Car's Latitude = "+lat);
                carLong.setText("Car's Longitude = "+longit);
                extras.putDouble("latitude",lat);
                extras.putDouble("longitude",longit);
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        System.exit(0);
        finish();
    }
}