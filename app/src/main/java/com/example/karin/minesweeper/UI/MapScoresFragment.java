package com.example.karin.minesweeper.UI;

import android.Manifest;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.karin.minesweeper.R;
import com.example.karin.minesweeper.logic.DbSingleton;
import com.example.karin.minesweeper.logic.PlayerScore;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Avi on 07/01/2017.
 */

public class MapScoresFragment extends Fragment implements OnMapReadyCallback
{
    private DbSingleton dbs;
    private LocationManager locationManager;
    private final String TAG = MapScoresFragment.class.getSimpleName();
    private Application activity;
    private MapView mMapView;
    private String level;
    private ArrayList<PlayerScore> arr;
    private Double counter = 0.0;
    private Double midianLat = 0.0;
    private Double midianLan = 0.0;
    private GoogleMap map;

    Handler markerHandler = new Handler();
    Runnable markerRunable = new Runnable() {
        @Override
        public void run() {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(activity, Locale.getDefault());

            for (final PlayerScore p: arr)
            {
                final Double longitude = p.getPlayerLongitude();
                final Double altitude = p.getPlayerAltitude();
                if(altitude != 0 && longitude != 0)
                {
                    try {
                        addresses = geocoder.getFromLocation(altitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addresses != null && addresses.size() > 0) {
                        final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        final String city = addresses.get(0).getLocality();
                        String country = addresses.get(0).getCountryName();
                        midianLan += longitude;
                        midianLat += altitude;
                        counter++;

                        markerHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                map.addMarker(new MarkerOptions()
                                        .title(p.getPlayerName() + ": " + p.getPlayerTime())
                                        .snippet(address + ", " + city)
                                        .position(new LatLng(altitude, longitude)));
                            }
                        });
                    }
                }
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        dbs = DbSingleton.getInstance(getActivity().getApplicationContext());
        activity = this.getActivity().getApplication();
        level = getArguments().getString("Level");
        arr = dbs.getPlayerScoresByLevel(level);

        locationManager = (LocationManager) this.getActivity().getApplication().getSystemService(Context.LOCATION_SERVICE);
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        mMapView = (MapView) v.findViewById(R.id.mapF);
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap map1)
    {
        map = map1;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(activity, Locale.getDefault());

                for (final PlayerScore p: arr)
                {
                    final Double longitude = p.getPlayerLongitude();
                    final Double altitude = p.getPlayerAltitude();
                    midianLan += longitude;
                    midianLat += altitude;

                    if(altitude != 0 && longitude != 0)
                    {
                        counter++;
                        try {
                            addresses = geocoder.getFromLocation(altitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(addresses != null && addresses.size() > 0) {
                            final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            final String city = addresses.get(0).getLocality();
                            final String country = addresses.get(0).getCountryName();

                            markerHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    map.addMarker(new MarkerOptions()
                                            .title(p.getPlayerName() + ": " + p.getPlayerTime())
                                            .snippet(address + ", " + city)
                                            .position(new LatLng(altitude, longitude)));

                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(midianLat/counter, midianLan/counter)).zoom(12).build();
                                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                }
                            });
                        }
                    }
                }
                markerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(midianLat/counter, midianLan/counter)).zoom(12).build();
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });
            }
        });
        t.start();
        //markerHandler.postDelayed(markerRunable,0);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
    }
}