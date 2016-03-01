package com.haraj.mersal.cobalocation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvLocation;

    private Button btnStart;
    private Button btnStop;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);

        tvLocation = (TextView) findViewById(R.id.tv_location);
        setNotListeningText();

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);

        Button.OnClickListener btnStartListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setupLocationListener();
            }
        };

        Button.OnClickListener btnStopListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.removeLocationListener();
            }
        };

        btnStart.setOnClickListener(btnStartListener);
        btnStop.setOnClickListener(btnStopListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        presenter.handleRequestPermissionResult(requestCode, permissions, grantResults);
    }

    public void setLocation(double latitude, double longitude) {
        tvLocation.setText("latitude: " + latitude + ", longitude: " + longitude);
    }

    public void setListeningText()
    {
        tvLocation.setText("Listening to location updates...");
    }

    public void setNotListeningText()
    {
        tvLocation.setText("Location listener turned off");
    }
}
