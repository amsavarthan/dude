package com.amsavarthan.dude.nearby.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;


import com.amsavarthan.dude.R;
import com.amsavarthan.dude.nearby.adapter.PlaceListAdapter;
import com.amsavarthan.dude.nearby.model.Place;
import com.amsavarthan.dude.nearby.utils.GoogleApiUrl;
import com.amsavarthan.dude.utils.Utils;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class PlaceListActivity extends AppCompatActivity {

    public static final String TAG = PlaceListActivity.class.getSimpleName();

    /**
     * ArrayList to store the Near By Place List
     */
    private ArrayList<Place> mNearByPlaceArrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private PlaceListAdapter mPlaceListAdapter;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(base));
    }

    private void initializeActivity() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/bold.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initializeActivity();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        /**
         * get the intent and get the location Tag
         */

        SharedPreferences sharedpreferences = getSharedPreferences(Utils.RADIUS, Context.MODE_PRIVATE);

        String locationTag = getIntent().getStringExtra(GoogleApiUrl.LOCATION_TYPE_EXTRA_TEXT);
        String locationName = getIntent().getStringExtra(GoogleApiUrl.LOCATION_NAME_EXTRA_TEXT);
        String currentLocation = getSharedPreferences(
                GoogleApiUrl.CURRENT_LOCATION_SHARED_PREFERENCE_KEY, 0)
                .getString(GoogleApiUrl.CURRENT_LOCATION_DATA_KEY, null);

        String locationQueryStringUrl = GoogleApiUrl.BASE_URL + GoogleApiUrl.NEARBY_SEARCH_TAG + "/" +
                GoogleApiUrl.JSON_FORMAT_TAG + "?" + GoogleApiUrl.LOCATION_TAG + "=" +
                currentLocation + "&" + GoogleApiUrl.RADIUS_TAG + "=" +
                sharedpreferences.getString("radius",GoogleApiUrl.RADIUS_VALUE) + "&" + GoogleApiUrl.PLACE_TYPE_TAG + "=" + locationTag +
                "&" + GoogleApiUrl.API_KEY_TAG + "=" + GoogleApiUrl.API_KEY;

        Log.d(TAG, locationQueryStringUrl);

        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        String actionBarTitleText = getResources().getString(R.string.near_by_tag) +
                " " + locationName + " " + getString(R.string.list_tag);
        setTitle(actionBarTitleText);
        actionBar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNearByPlaceArrayList = getIntent()
                .getParcelableArrayListExtra(GoogleApiUrl.ALL_NEARBY_LOCATION_KEY);
        mRecyclerView = (RecyclerView) findViewById(R.id.place_list_recycler_view);

        if (mNearByPlaceArrayList.size() == 0) {
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            findViewById(R.id.empty_view).setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mGridLayoutManager = new GridLayoutManager(this, 1);
            mPlaceListAdapter = new PlaceListAdapter(this, mNearByPlaceArrayList);
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            mRecyclerView.setAdapter(mPlaceListAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
