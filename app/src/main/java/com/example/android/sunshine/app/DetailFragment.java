package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by SAmitabh on 15-06-2016.
 */
public  class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();


    public DetailFragment() {
    }
    private String displayString="";
    public static final int LOADER_ID=0;
    private static final String[] FORECAST_COLUMNS={WeatherContract.WeatherEntry.TABLE_NAME + "." +WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.LocationEntry.COLUMN_CITY_NAME
    };
    static final int COL_WEATHER_ID=0;
    static final int COL_WEATHER_DATE=1;
    static final int COL_WEATHER_DESC=2;
    static final int COL_WEATHER_MAX_TEMP=3;
    static final int COL_WEATHER_MIN_TEMP=4;
    static final int COL_LOCATION_SETTING=5;
    static final int COL_WEATHER_CONDITION_ID=6;
    static final int COL_HUMIDITY=7;
    static final int COL_PRESSURE=8;
    static final int COL_WIND_SPEED=9;
    static final int COL_DEGREES=10;
    static final int COL_CITY_NAME=11;

    /////// Views declaration//////////////////
    public  ImageView icon_view;
    public  TextView description_view;
    public  TextView date_view;
    public  TextView day_view;
    public  TextView high_temp_view;
    public  TextView low_temp_view;
    public  TextView humidity__view;
    public  TextView wind_speed_view;
    public  TextView pressure_view;
    public  TextView city_view;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG,"In onLoad finished");
        if(!data.moveToFirst()) return;

                                 ////// ICON ////////
        int weather_id=data.getInt(COL_WEATHER_CONDITION_ID);
        icon_view.setImageResource(Utility.getArtResourceForWeatherCondition(weather_id));
                                 ////// DAte //////
        String date_string=Utility.getFormattedMonthDay(getActivity(),data.getLong(COL_WEATHER_DATE));
        date_view.setText(date_string);
                               ////////DAY/////////////
        String day_string=Utility.getDayName(getActivity(),data.getLong(COL_WEATHER_DATE));
        day_view.setText(day_string);
                          ////// Description//////
        String descrip_weath_string=data.getString(COL_WEATHER_DESC);
        description_view.setText(descrip_weath_string);
                      ////////High and Low temperature get from loader and set to views///////////
        boolean isMetric=Utility.isMetric(getActivity());

                               ///// High temp./////
        String high_temp_string=Utility.formatTemperature(getActivity(),data.getDouble(COL_WEATHER_MAX_TEMP),isMetric);
        high_temp_view.setText(high_temp_string);
                             ///// Low temp.////////
        String low_temp_string=Utility.formatTemperature(getActivity(),data.getDouble(COL_WEATHER_MIN_TEMP),isMetric);
        low_temp_view.setText(low_temp_string);

                               ///////Humidity///////
        String humidity_string=String.format(getActivity().getString(R.string.format_humidity),data.getFloat(COL_HUMIDITY));
        humidity__view.setText(humidity_string);
                               /////Pressure///////
        String pressure_string=String.format(getActivity().getString(R.string.format_pressure),data.getFloat(COL_PRESSURE));
        pressure_view.setText(pressure_string);
                             ///// Wind Speed/////
        float wind_speed=data.getFloat(COL_WIND_SPEED);
        float degree=data.getFloat(COL_DEGREES);
        String wind_speed_string=Utility.getFormattedWind(getActivity(),wind_speed,degree);
        wind_speed_view.setText(wind_speed_string);


    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        ////// Get location and fetch from the database the cursor for the data to be fetched from DB
        Intent intent=getActivity().getIntent();
        if(intent ==null || intent.getData() == null){
            return null;
        }
        return new CursorLoader(getActivity(),intent.getData(),FORECAST_COLUMNS,null,null,null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings_detail) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.menu_item_share) {
            if(displayString!=null)
                shareAction();

            return true;
        } else {
            Log.v("IN DEtail activity", "item not getting selected");
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        icon_view = (ImageView) rootView.findViewById(R.id.detail_icon);
        description_view = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        date_view = (TextView) rootView.findViewById(R.id.detail_date_textview);
        day_view = (TextView) rootView.findViewById(R.id.detail_day_textview);
        high_temp_view = (TextView) rootView.findViewById(R.id.detail_high_textview);
        low_temp_view = (TextView) rootView.findViewById(R.id.detail_low_textview);
        humidity__view= (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        wind_speed_view=(TextView) rootView.findViewById(R.id.detail_wind_textview);
        pressure_view=  (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        city_view = (TextView) rootView.findViewById(R.id.detail_city_textview);
        return rootView;
    }




    public void shareAction(){
        Intent shareIntent=new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        String toBeShared=displayString + " " +"#MySunshine";
        shareIntent.putExtra(Intent.EXTRA_TEXT,toBeShared);
        Intent intent=getActivity().getIntent();
        if(intent.resolveActivity(getActivity().getPackageManager())!= null){
            startActivity(shareIntent.createChooser(shareIntent, getResources().getText(R.string.to_share)));
        }
    }
}