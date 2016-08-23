package com.example.android.sunshine.app;

/**
 * Created by SAmitabh on 13-06-2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
     public static final String LOG_TAG=ForecastAdapter.class.getSimpleName();
    public final int VIEW_TYPE_TODAY=0;
    public final int VIEW_TYPE_FUTURE=1;
    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext,high, isMetric) + "/" + Utility.formatTemperature(mContext,low, isMetric);
        return highLowStr;
    }



    @Override
    public int getItemViewType(int position) {
        return (position==0)?VIEW_TYPE_TODAY:VIEW_TYPE_FUTURE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewtype = getItemViewType(cursor.getPosition());
        Log.d(LOG_TAG,"in new view");
        int layoutID=0;
        if(viewtype == VIEW_TYPE_TODAY)  {
            layoutID=R.layout.list_item_forecast_today;
        }
        else if(viewtype == VIEW_TYPE_FUTURE)
            layoutID = R.layout.list_item_forecast;
        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        int layoutID=0;

        ViewHolder viewHolder=(ViewHolder) view.getTag();

        ////////////Taking data from cursor///////////
        String date_string=Utility.getFriendlyDayString(mContext,cursor.getLong(ForecastFragment.COL_WEATHER_DATE));
        String description_string=cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        boolean isMetric=Utility.isMetric(mContext);
        String high_temp_string=Utility.formatTemperature(mContext,cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),isMetric);
        String low_temp_string=Utility.formatTemperature(mContext,cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP),isMetric);
        String city_name_string = cursor.getString(ForecastFragment.COL_CITY_NAME).toUpperCase();
        Log.d(LOG_TAG,"city name is : " +city_name_string);

        ////////Setting data to the views//////////
        viewHolder.date_view.setText(date_string);
        Log.d(LOG_TAG,"the date_string"+date_string);
        viewHolder.description_view.setText(description_string);

        viewHolder.high_temp_view.setText(high_temp_string);
        viewHolder.low_temp_view.setText(low_temp_string);

        Double max_temp = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        Double min_temp =cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        Utility.setcolor(viewHolder.high_temp_view , max_temp, mContext);
        Utility.setcolor(viewHolder.low_temp_view , min_temp, mContext);


        if ( getItemViewType (cursor.getPosition()) == VIEW_TYPE_TODAY)
                  viewHolder.city_name_view.setText(city_name_string);

        /////////////   Setting icons for the weather condition/////////////
        int viewtype=getItemViewType(cursor.getPosition());
        int weatherid=cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);

        switch(viewtype) {
            case VIEW_TYPE_TODAY:
                viewHolder.icon_view.setImageResource(Utility.getArtResourceForWeatherCondition(weatherid));
                break;
            case VIEW_TYPE_FUTURE:
                viewHolder.icon_view.setImageResource(Utility.getIconResourceForWeatherCondition(weatherid));
                break;
        }

    }




    public static class ViewHolder{
        public final ImageView icon_view;
        public final TextView description_view;
        public final TextView date_view;
        public final TextView high_temp_view;
        public final TextView low_temp_view;
        public final TextView city_name_view;

        public ViewHolder(View view){
            icon_view=(ImageView) view.findViewById(R.id.list_item_icon);
            description_view=(TextView) view.findViewById(R.id.list_item_forecast_textview);
            date_view=(TextView) view.findViewById(R.id.list_item_date_textview);
            high_temp_view=(TextView) view.findViewById(R.id.list_item_high_textview);
            low_temp_view=(TextView) view.findViewById(R.id.list_item_low_textview);
            city_name_view= (TextView) view.findViewById(R.id.list_item_city_textview);
        }
    }
}