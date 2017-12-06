package com.example.hp.httpfetching;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Parking extends AppCompatActivity
{
    Button getLoc;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);
        getLoc=(Button)findViewById(R.id.button7);
        getLoc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(Parking.this,placePicker.class);
                startActivity(intent);
            }
        });
    }
}
