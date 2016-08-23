package com.example.android.sunshine.app;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sunshine.app.data.WeatherContract;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public  class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, LocationListener {
    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private String provider = "";
    private String city_name = "";



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    public static final int LOADER_ID=0;
    public ForecastAdapter mWeatheradapter;
    private int mPosition = ListView.INVALID_POSITION;
    Cursor cur;
    private ListView listView;
    protected static String myLocation  = "Kolkata";
    boolean MYLOCATION_ENABLED = false;
    boolean mobileDataEnabled =false; // Assume disabled
    private static final String[] FORECAST_COLUMNS={WeatherContract.WeatherEntry.TABLE_NAME + "." +WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
            WeatherContract.LocationEntry.COLUMN_CITY_NAME
    };
     static final int COL_WEATHER_ID=0;
    static final int COL_WEATHER_DATE=1;
    static final int COL_WEATHER_DESC=2;
    static final int COL_WEATHER_MAX_TEMP=3;
    static final int COL_WEATHER_MIN_TEMP=4;
    static final int COL_LOCATION_SETTING=5;
    static final int COL_WEATHER_CONDITION_ID=6;
    static final int COL_COORD_LAT=7;
    static final int COL_COORD_LONG=8;
    static final int COL_CITY_NAME=9;
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public ForecastFragment() {
    }

    public int a;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /// Finding the current location of the user ///////////////

        findmylocation();

        //new MyLocationFinder(getActivity(),myLocation).execute();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mWeatheradapter = new ForecastAdapter(getActivity(),null,0);

        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mWeatheradapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                        String locationSetting = Utility.getPreferredLocation(getActivity());

                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                            intent.setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                    startActivity(intent);
                }
            }
        });
        getLoaderManager().restartLoader(LOADER_ID,null,this);
        return rootView;
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        ////// Get location and fetch from the database the set of cursors to be populated in the
        //  cursoradapter.
        String locationSetting ="";
        if(MYLOCATION_ENABLED && mobileDataEnabled) {
            locationSetting = myLocation;

        }
        //Log.d(LOG_TAG,"inside mylocation enabled");
        else {
            locationSetting = Utility.getPreferredLocation(getActivity());
        }
        Log.d(LOG_TAG,"loader restart check");

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());
        return new CursorLoader(getActivity(),weatherForLocationUri,FORECAST_COLUMNS,null,null,sortOrder);

    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      mWeatheradapter.swapCursor(data);
        Log.d(LOG_TAG,"new data fetched in cursor");
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            listView.smoothScrollToPosition(mPosition);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mWeatheradapter.swapCursor(null);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
        SpannableString span = new SpannableString("This City:OFF");
        span.setSpan(new ForegroundColorSpan(Color.WHITE),0,span.length(),0);
        menu.getItem(Utility.GET_MY_LOCATION_MENU_VALUE()).setTitle(span);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);               // This line is to let this fragment handle menu options.
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuItem item_selected = item;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateweather();
                getLoaderManager().restartLoader(LOADER_ID,null,this);
                return true;
           case R.id.action_map :
                openPreferredLocationInMap();
                return true;
            case R.id.action_my_location :

                    MYLOCATION_ENABLED = !MYLOCATION_ENABLED;
                    findmylocation();
                    updateweather();
                    Timer timer = new Timer();
                    new CountDownTimer(500, 1000) {
                        @Override
                        public void onFinish() {
                            SpannableString span;
                            getLoaderManager().restartLoader(LOADER_ID, null, ForecastFragment.this);
                            if (MYLOCATION_ENABLED ) {
                                if (mobileDataEnabled) {

                                    span = new SpannableString("This City: ON");
                                    span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, span.length(), 0);
                                } else {
                                    MYLOCATION_ENABLED = !MYLOCATION_ENABLED;
                                    span = new SpannableString("This City: OFF");
                                    span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, span.length(), 0);
                                    CharSequence text = "Service not available,check Background data";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(getActivity(), text, duration);
                                    toast.show();
                                }
                            }

                            else {
                                span = new SpannableString("This City:OFF");
                                span.setSpan(new ForegroundColorSpan(Color.WHITE), 0, span.length(), 0);
                            }
                            item_selected.setTitle(span);
                        }

                        @Override
                        public void onTick(long l) {

                        }
                    }.start();

                return true;
            case R.id.action_settings_main:
                Intent intent=new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void openPreferredLocationInMap() {
        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        if ( null != mWeatheradapter ) {
            Cursor c = mWeatheradapter.getCursor();
            if ( null != c ) {
                c.moveToPosition(0);
                String posLat = c.getString(COL_COORD_LAT);
                String posLong = c.getString(COL_COORD_LONG);
                Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);
                Log.d(LOG_TAG,"LAtitude is: "+posLat +" and longitude is: "+posLong);
                Intent intent = new Intent(Intent.ACTION_VIEW,geoLocation);
                intent.setPackage("com.google.android.apps.maps");

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
                }
            }

        }
    }

    public void updateweather() {
        String location = "";
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        if(MYLOCATION_ENABLED && mobileDataEnabled){
            location = myLocation;
        }
        else location =Utility.getPreferredLocation(getActivity());
         //location ="845401";
        Log.v("In update weather","City is: "+ location);
        weatherTask.execute(location);
//        Log.v(LOG_TAG,"in updateweather");
//        SunshineSyncAdapter.syncImmediately(getActivity());

    }
    public void findmylocation(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        Geocoder geoCoder = new Geocoder(getActivity(), Locale.ENGLISH);
        List<Address> listAddr = null;
        if(location != null ) {
            try {
                listAddr = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error in finding location:  " + e.getMessage());
            }
            if (listAddr != null) {
                city_name = listAddr.get(0).getLocality();
                myLocation = city_name;
                mobileDataEnabled=true;
            }

        }
    }
    public void settingsChange() throws InterruptedException{
        Log.v(LOG_TAG,"location_change being called");
        //MYLOCATION_ENABLED = false;
        updateweather();

        Timer timer = new Timer();

        new CountDownTimer(500, 1000){
            @Override
            public void onFinish() {
                getLoaderManager().restartLoader(LOADER_ID,null,ForecastFragment.this);
            }

            @Override
            public void onTick(long l) {

            }
        }.start();

    }

}





