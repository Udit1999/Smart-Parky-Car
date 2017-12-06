package com.example.hp.httpfetching;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class Calling extends AppCompatActivity
{
    ListView lv;
    public String jsonStr="";
    GPSTracker gps;
    TextView distance;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button map;
    Bundle bundle;
    double dist=0.0,bear=0.0,lat=0.0,longit=0.0,wayLat=0.0,wayLon=0.0;
    double cLatitude=0.0,cLongitude=0.0;
    public List<Location> loc;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        Intent intent=getIntent();
        bundle=intent.getExtras();
        loc=new ArrayList<>();
        gps=new GPSTracker(Calling.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        distance=(TextView)findViewById(R.id.textView8);
        map=(Button)findViewById(R.id.button6);
        getdata();
//        Toast.makeText(Calling.this,genUrl(),Toast.LENGTH_SHORT).show();
//        lv=(ListView)findViewById(R.id.listView);
//        Execution sh=new Execution();
//        String url="https://maps.googleapis.com/maps/api/directions/json?origin=28.751849,77.498897&destination=28.752420,77.498628&mode=walking&key=AIzaSyBsy2Hk_dqB5x78ke3dU-HyfoQ01Pd01O8";
//        String url1="https://maps.googleapispis.com/maps/api/directions/json?origin=28.975207,77.7011134&destination=28.752568,77.497681&mode=walking&key=AIzaSyBsy2Hk_dqB5x78ke3dU-HyfoQ01Pd01O8";
//        jsonStr=sh.makeServiceCall(genUrl());
//        abc();
//        ArrayAdapter ad=new ArrayAdapter(Calling.this,android.R.layout.simple_expandable_list_item_1,loc);
//        lv.setAdapter(ad);
        map.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle1=new Bundle();
                bundle1.putDouble("latitude",lat);
                bundle1.putDouble("longitude",longit);
                Intent intent=new Intent(Calling.this,MapsActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
    }
    public void saveDist()
    {
        calcDist();
        SDistance sDistance=new SDistance(""+dist,""+bear);
        databaseReference.child("Others").setValue(sDistance);
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
                String C=dataSnapshot.child("Waypoint").child("Lattitude").getValue(String.class);
                String D=dataSnapshot.child("Waypoint").child("Longitude").getValue(String.class);
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
                wayLat=Double.parseDouble(C);
                wayLon=Double.parseDouble(D);
                saveDist();
                distance.setText("Distance = "+(int)dist+"cm");
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void calcDist()
    {
        Location waypo=new Location("");
        waypo.setLatitude(wayLat);
        waypo.setLongitude(wayLon);
        Location user=new Location("");
        user.setLatitude(gps.latitude);
        user.setLongitude(gps.longitude);
        Location cars=new Location("");
        cars.setLatitude(lat);
        cars.setLongitude(longit);
        dist= user.distanceTo(cars)*100;
        bear=cars.bearingTo(waypo);
        if(bear<0)
            bear=bear+360;
        double dist1= cars.distanceTo(waypo)*100;
        if(dist1<500.0)
        {
            bear=cars.bearingTo(user);
            if(bear<0)
                bear+=360;
        }
    }
//    private final String genUrl()
//    {
//        String origin="origin="+gps.latitude+","+gps.longitude;
//        String destination="destination="+cLatitude+","+cLongitude;
//        String output="json";
//        String key="mode=walking&key=AIzaSyBsy2Hk_dqB5x78ke3dU-HyfoQ01Pd01O8";
//        String parameter=origin+"&"+destination+"&"+key;
//        final String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameter;
//        return url;
//    }
//    void abc()
//    {
//        if (jsonStr != null)
//        {
//            try
//            {
//                JSONObject jsonObj = new JSONObject(jsonStr);
//                JSONArray routes = jsonObj.getJSONArray("routes");
//                for (int i = 0; i < routes.length(); i++)
//                {
//                    JSONObject len = routes.getJSONObject(i);
//                    JSONArray legs = len.getJSONArray("legs");
//                    for (int j = 0; j < legs.length(); j++)
//                    {
//                        JSONObject obj1 = legs.getJSONObject(j);
//                        JSONArray steps = obj1.getJSONArray("steps");
//                        for (int k = 0; k < steps.length(); k++)
//                        {
//                            JSONObject con = steps.getJSONObject(k);
//                            JSONObject start = con.getJSONObject("start_location");
//                            Location l = new Location("");
//                            l.setLatitude(start.getDouble("lat"));
//                            l.setLongitude(start.getDouble("lng"));
//                            loc.add(l);
//                        }
//                    }
//                }
//            }
//            catch (JSONException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Calling.this,MainActivity.class));
        finish();
    }
}
