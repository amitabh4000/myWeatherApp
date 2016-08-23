package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends ActionBarActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    static String displayString;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings_detail) {
            Intent intent = new Intent(this, SettingsActivity.class);
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

public void shareAction(){
    Intent shareIntent=new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    shareIntent.setType("text/plain");
    String toBeShared=displayString + " " +"#MySunshine";
    shareIntent.putExtra(Intent.EXTRA_TEXT,toBeShared);
    if(getIntent().resolveActivity(getPackageManager())!= null){
        startActivity(shareIntent.createChooser(shareIntent, getResources().getText(R.string.to_share)));
    }
}


}