package com.example.android.sunshine.app;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by SAmitabh on 11-07-2016.
 */
public class MyLocationFinder extends AsyncTask<Void,Void,String> implements LocationListener {
    Context mContext;
    String myLocation;
    private static final String LOG_TAG = MyLocationFinder.class.getSimpleName();
    public MyLocationFinder(Context context , String myLocation){
        mContext = context;
        this.myLocation = myLocation;
    }
    private String provider = "";
    private String city_name = "";
    @Override
    protected String doInBackground(Void... voids) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if(location == null) Log.d(LOG_TAG,"MyLocation is null");
        else Log.d(LOG_TAG,"MyLocation not null");
        Log.d(LOG_TAG,"Latitude is: "+location.getLatitude()+"Longitude is: "+location.getLongitude());
        Geocoder geoCoder = new Geocoder(mContext, Locale.ENGLISH);
        List<Address> listAddr = null;
        try{
           listAddr = geoCoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
        }
        catch(IOException e){
           Log.e(LOG_TAG , "Error in finding location:  "+e.getMessage());
        }
        if(listAddr != null) {
            city_name = listAddr.get(0).getLocality();
        }
        return city_name;
    }

    @Override
    protected void onPostExecute(String city_name) {
        if( city_name.length() >0)
        ForecastFragment.myLocation = city_name;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
