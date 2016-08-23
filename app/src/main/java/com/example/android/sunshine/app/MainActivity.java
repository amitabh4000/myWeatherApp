package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//import com.example.android.sunshine.app.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity implements LocationListener{


    private boolean mTwoPane;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private String mLocation="845401";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean isMetric = true;
    private LocationManager locationManager;@Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);





        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        //SunshineSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String newLocation=Utility.getPreferredLocation(this);
        boolean isNewUnitMetric = Utility.isMetric(this);
        if(isMetric != isNewUnitMetric){
            isMetric = isNewUnitMetric;
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            try {
                ff.settingsChange();
            }
            catch(InterruptedException w){

            }
        }
        if(!mLocation.equals(newLocation)) {
            mLocation=newLocation;
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            try {
                ff.settingsChange();
            }
            catch(InterruptedException w){

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_main) {
                Intent intent=new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }



        return super.onOptionsItemSelected(item);
    }

public void openPreferredLocationMap(){
    Log.v(LOG_TAG,"IN loction map opening function");
    String location=Utility.getPreferredLocation(this);
    Log.v(LOG_TAG,"LOcation: "+location);
    Uri geoLocation=Uri.parse("geo:0,0?").buildUpon()
                                         .appendQueryParameter("q",location).build();

    Intent intent=new Intent(Intent.CATEGORY_APP_MAPS);


    //intent.setData(geoLocation);
    if(intent.resolveActivity(getPackageManager())!=null){
        startActivity(intent);
    }
    else{
        Log.d(LOG_TAG,"error in opening the map for location: "+location);
    }
    }



}


