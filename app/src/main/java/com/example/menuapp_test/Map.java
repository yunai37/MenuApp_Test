package com.example.menuapp_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private Button list;
    private String token, address;
    private double latitude, longitude;
    private FloatingActionButton home;
    private ArrayList<ListItem> listItems = new ArrayList<ListItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        address = getIntent.getStringExtra("address");
        latitude = Double.parseDouble(getIntent.getStringExtra("latitude"));
        longitude = Double.parseDouble(getIntent.getStringExtra("longitude"));
        listItems = (ArrayList<ListItem>) getIntent.getSerializableExtra("listItems");

        home = findViewById(R.id.fab);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        list = findViewById(R.id.btn_list);
        list.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("address", address);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng my = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(my);
        markerOptions.title("현재 위치");
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my, 10));
    }
}
