package com.amsavarthan.dude.nearby.ui;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.nearby.adapter.HomeScreenItemListAdapter;
import com.amsavarthan.dude.nearby.utils.PlaceDetailProvider;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class HomeScreenActivity extends AppCompatActivity {

    //View Reference Variable
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private HomeScreenItemListAdapter mHomeScreenItemListAdapter;
    private String[] itemString;


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
        setContentView(R.layout.activity_home_screen);

        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("DUDE - Nearby places");
        actionBar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));



        itemString = PlaceDetailProvider.popularPlaceTagName;
        mHomeScreenItemListAdapter = new HomeScreenItemListAdapter(this, itemString);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mGridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(36);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mRecyclerView.setAdapter(mHomeScreenItemListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu to add items to action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.removeItem(R.id.share_icon);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, PlaceSearchResultActivity.class)));

        MenuItem item=menu.findItem(R.id.fav);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(HomeScreenActivity.this, FavouritePlaceListActivity.class));
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}


