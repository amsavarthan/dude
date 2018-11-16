package com.amsavarthan.dude.fragment;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.activities.MainActivity;
import com.amsavarthan.dude.utils.UserDatabase;
import com.amsavarthan.dude.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class FriendLocation extends Fragment {

    View view;
    MapView mapView;
    EditText friend_id;
    Button locate;
    TextView loc_id;
    FirebaseUser mCurrentUser;
    FirebaseFirestore mFirestore;
    private ProgressDialog mDialog;
    private GoogleMap mGoogleMap;

    public static FriendLocation newInstance(){
        return new FriendLocation();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.frag_location, container, false);
        return view;
    }

    private void initMap() {
        int googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(view.getContext());
        if (googlePlayStatus != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, getActivity(), -1).show();
            getActivity().finish();
        } else {
            if (mGoogleMap != null) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
            }
        }
    }

    private void drawMarker(double latitude,double longitude,String name) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            LatLng gps = new LatLng(latitude, longitude);
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title(name));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 16));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        mFirestore=FirebaseFirestore.getInstance();
        friend_id=view.findViewById(R.id.friend_id);
        loc_id=view.findViewById(R.id.location_id);
        locate=view.findViewById(R.id.find_location);
        mapView=view.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap=googleMap;
            }
        });

        initMap();
        initDialog();


       if(!Utils.isOnline(view.getContext())){

           UserDatabase userDatabase = new UserDatabase(view.getContext());

           Cursor rs = userDatabase.getData(1);
           rs.moveToFirst();

           String locc_id = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_LID));

           if (!rs.isClosed()) {
               rs.close();
           }

           loc_id.setText(locc_id);

           locate.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (TextUtils.isEmpty(friend_id.getText().toString())) {
                       Toast.makeText(view.getContext(), "Invalid id", Toast.LENGTH_SHORT).show();
                   }else {
                       Toast.makeText(view.getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                   }
               }
           });

       }else {

           mFirestore.collection("Users")
                   .document(mCurrentUser.getUid())
                   .get()
                   .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                           loc_id.setText(documentSnapshot.getString("location_id"));
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           e.printStackTrace();
                       }
                   });


           locate.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   if (TextUtils.isEmpty(friend_id.getText().toString())) {
                       Toast.makeText(view.getContext(), "Invalid id", Toast.LENGTH_SHORT).show();
                   } else {

                       mDialog.show();

                       try {

                           mFirestore.collection("Users")
                                   .document(mCurrentUser.getUid())
                                   .get()
                                   .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                       @Override
                                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                                           if (friend_id.getText().toString().equals(documentSnapshot.getString("location_id"))) {
                                               Toast.makeText(view.getContext(), "That's your location id :p", Toast.LENGTH_SHORT).show();
                                               mDialog.dismiss();
                                           } else {

                                               mFirestore.collection("Users")
                                                       .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                           @Override
                                                           public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {


                                                               if (e != null) {
                                                                   Toast.makeText(view.getContext(), "Some technical error occured : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                   e.printStackTrace();
                                                                   try {
                                                                       mDialog.dismiss();
                                                                   }catch (Exception exx){
                                                                       exx.printStackTrace();
                                                                   }
                                                                   return;
                                                               }

                                                               for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                                                                   try {
                                                                       mDialog.show();
                                                                   }catch (Exception ex){
                                                                       ex.printStackTrace();
                                                                   }
                                                                   if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                                                       if (documentChange.getDocument().getString("location_id").equals(friend_id.getText().toString())) {

                                                                           double latitude = documentChange.getDocument().getDouble("latitude");
                                                                           double longitude = documentChange.getDocument().getDouble("longitude");
                                                                           String name = documentChange.getDocument().getString("name");

                                                                           try {
                                                                               mDialog.dismiss();
                                                                           }catch (Exception exxx){
                                                                               exxx.printStackTrace();
                                                                           }
                                                                           drawMarker(latitude, longitude, name + "'s last known location");

                                                                           Toast.makeText(view.getContext(), "Showing "+name + "'s last known location", Toast.LENGTH_SHORT).show();

                                                                       } else {

                                                                           try {
                                                                               mDialog.dismiss();
                                                                           }catch (Exception exxxx){
                                                                               exxxx.printStackTrace();
                                                                           }
                                                                       }

                                                                   }

                                                               }

                                                           }
                                                       });


                                           }
                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   mDialog.dismiss();
                                   e.printStackTrace();
                               }
                           });


                       }catch (Exception e){
                           e.printStackTrace();
                       }
                   }

               }
           });
       }

    }

    private void initDialog() {
        mDialog=new ProgressDialog(view.getContext());
        mDialog.setMessage("Locating");
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
    }


}
