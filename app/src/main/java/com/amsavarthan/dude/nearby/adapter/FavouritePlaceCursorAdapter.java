package com.amsavarthan.dude.nearby.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.nearby.data.PlaceDetailContract;
import com.amsavarthan.dude.nearby.ui.PlaceDetailActivity;
import com.amsavarthan.dude.nearby.utils.CursorRecyclerViewAdapter;
import com.amsavarthan.dude.nearby.utils.GoogleApiUrl;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class FavouritePlaceCursorAdapter extends CursorRecyclerViewAdapter {

    public FavouritePlaceCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FavouritePlaceCursorAdapterViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.place_list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        ((FavouritePlaceCursorAdapterViewHolder) viewHolder).bindView(cursor);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private class FavouritePlaceCursorAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Cursor mCurrentDataCursor;
        //reference of the views
        private TextView mPlaceNameTextView;
        private TextView mPlaceAddressTextView;
        private TextView mPlaceOpenStatusTextView;
        private MaterialRatingBar mPlaceRating;
        private ImageView mLocationIcon;

        private FavouritePlaceCursorAdapterViewHolder(View itemView) {
            super(itemView);

            mPlaceNameTextView = (TextView) itemView.findViewById(R.id.place_name);
            mPlaceAddressTextView = (TextView) itemView.findViewById(R.id.place_address);
            mPlaceOpenStatusTextView = (TextView) itemView.findViewById(R.id.place_open_status);
            mPlaceRating = (MaterialRatingBar) itemView.findViewById(R.id.place_rating);
            mLocationIcon = (ImageView) itemView.findViewById(R.id.location_icon);

            itemView.setOnClickListener(this);
        }

        private void bindView(Cursor currentPlaceDataCursor) {

            mCurrentDataCursor = currentPlaceDataCursor;

            mPlaceNameTextView.setText(currentPlaceDataCursor.getString(
                    currentPlaceDataCursor.getColumnIndex(PlaceDetailContract.PlaceDetailEntry.COLUMN_PLACE_NAME)));


            mPlaceAddressTextView.setText(currentPlaceDataCursor.getString(
                    currentPlaceDataCursor.getColumnIndex(PlaceDetailContract.PlaceDetailEntry.COLUMN_PLACE_ADDRESS)));


            if (currentPlaceDataCursor.getString(currentPlaceDataCursor
                    .getColumnIndex(PlaceDetailContract.PlaceDetailEntry.COLUMN_PLACE_OPENING_HOUR_STATUS)).equals("true")) {
                mPlaceOpenStatusTextView.setText(R.string.open_now);

            } else if (currentPlaceDataCursor.getString(currentPlaceDataCursor
                    .getColumnIndex(PlaceDetailContract.PlaceDetailEntry.COLUMN_PLACE_OPENING_HOUR_STATUS)).equals("false")) {
                mPlaceOpenStatusTextView.setText(R.string.closed);

            } else {
                mPlaceOpenStatusTextView.setText(currentPlaceDataCursor.getString(currentPlaceDataCursor
                        .getColumnIndex(PlaceDetailContract.PlaceDetailEntry.COLUMN_PLACE_OPENING_HOUR_STATUS)));

            }
            mPlaceRating.setRating(Float.parseFloat(String.valueOf(currentPlaceDataCursor
                    .getDouble(currentPlaceDataCursor
                            .getColumnIndex(PlaceDetailContract.PlaceDetailEntry.COLUMN_PLACE_RATING)))));

            mLocationIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.color_divider));
        }

        @Override
        public void onClick(View v) {
            if (isNetworkAvailable()) {
                Intent currentLocationDetailIntent = new Intent(mContext, PlaceDetailActivity.class);
                currentLocationDetailIntent.putExtra(GoogleApiUrl.LOCATION_ID_EXTRA_TEXT,
                        mCurrentDataCursor.getString(
                                mCurrentDataCursor.getColumnIndex(PlaceDetailContract.PlaceDetailEntry.COLUMN_PLACE_ID)));
                mContext.startActivity(currentLocationDetailIntent);

            } else
                Snackbar.make(mLocationIcon, R.string.no_connection_string,
                        Snackbar.LENGTH_SHORT).show();
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }
    }
}