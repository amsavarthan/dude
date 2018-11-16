package com.amsavarthan.dude.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.adapter.ContactsAdapter;
import com.amsavarthan.dude.models.Contacts;
import com.amsavarthan.dude.utils.ContactsDatabase;
import com.amsavarthan.dude.utils.UserDatabase;
import com.amsavarthan.dude.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.amsavarthan.dude.utils.Utils.isOnline;
import static com.amsavarthan.dude.utils.Utils.locationUserId;

public class ProfileSetup extends AppCompatActivity implements LocationListener {

    private static final String TAG = ProfileSetup.class.getSimpleName();
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String phone;
    private EditText name_field, age_field, phone_field, city_field, state_field, radius_field,donor_field,allergy_field,bgroup_field;
    private ProgressDialog mDialog;
    public static Activity profile_activity;
    private FloatingActionButton fab;
    private List<Contacts> contactsList = new ArrayList<>();
    private RecyclerView contactsRecyclerview;
    private ContactsAdapter adapter;
    public final int PICK_CONTACT = 1001;
    private LocationManager locationManager;
    private double latitude,longitude;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private int clocationPermissionCheck = PackageManager.PERMISSION_DENIED;
    private int flocationPermissionCheck = PackageManager.PERMISSION_DENIED;
    private SharedPreferences sharedpreferences;
    private String location_id;

    private void verifyAndRequestPermission() {

        clocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        flocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (clocationPermissionCheck != PackageManager.PERMISSION_GRANTED || flocationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileSetup.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        clocationPermissionCheck = PackageManager.PERMISSION_GRANTED;
                    }
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        flocationPermissionCheck = PackageManager.PERMISSION_GRANTED;
                    }

                }
            }
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/bold.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        profile_activity = this;

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            verifyAndRequestPermission();

        UserDatabase userDatabase=new UserDatabase(this);
        try {
            if(mCurrentUser!=null) {
                Cursor rs = userDatabase.getData(1);
                rs.moveToFirst();
                String nam = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_NAME));
                if (!rs.isClosed()) {
                    rs.close();
                }
                if (!TextUtils.isEmpty(nam)) {
                    startActivity(new Intent(ProfileSetup.this, MainActivity.class));
                    finish();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        adapter=new ContactsAdapter(contactsList);
        sharedpreferences = getSharedPreferences(Utils.RADIUS, Context.MODE_PRIVATE);

        phone=getIntent().getStringExtra("phone");
        name_field=findViewById(R.id.name);
        fab=findViewById(R.id.add);
        age_field=findViewById(R.id.age);
        phone_field=findViewById(R.id.phone);
        city_field=findViewById(R.id.city);
        radius_field=findViewById(R.id.radius);
        allergy_field=findViewById(R.id.allergies);
        bgroup_field=findViewById(R.id.bgroup);
        donor_field=findViewById(R.id.organ);
        state_field=findViewById(R.id.state);


        String radius=sharedpreferences.getString("radius","5000");
        radius_field.setText(radius);

        contactsRecyclerview=findViewById(R.id.contact_list);
        contactsRecyclerview.setHasFixedSize(true);
        contactsRecyclerview.setItemAnimator(new DefaultItemAnimator());
        contactsRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        contactsRecyclerview.setAdapter(adapter);

        mDialog=new ProgressDialog(this);
        mDialog.setMessage("Just a sec...");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });

        ContactsDatabase contactsDatabase=new ContactsDatabase(this);
        SQLiteDatabase sqLiteDatabase=contactsDatabase.getReadableDatabase();
        Cursor cursor=contactsDatabase.getinformation(sqLiteDatabase);
        if(cursor.moveToFirst())
        {
            do {
                String contact_name,contact_phone;
                contact_name= cursor.getString(0);
                contact_phone= cursor.getString(1);
                Contacts contacts=new Contacts(contact_name,contact_phone);
                contactsList.add(contacts);
                adapter.notifyDataSetChanged();

            }while (cursor.moveToNext());
        }

        try{
            mFirestore.collection("Users")
                    .document(mCurrentUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()) {
                                name_field.setText(documentSnapshot.getString("name"));
                                age_field.setText(documentSnapshot.getString("age"));
                                phone_field.setText(documentSnapshot.getString("phone"));
                                city_field.setText(documentSnapshot.getString("city"));
                                state_field.setText(documentSnapshot.getString("state"));
                                allergy_field.setText(documentSnapshot.getString("allergy"));
                                donor_field.setText(documentSnapshot.getString("organ_donor"));
                                bgroup_field.setText(documentSnapshot.getString("blood_group"));
                                if(TextUtils.isEmpty(documentSnapshot.getString("location_id"))){
                                    location_id=locationUserId(7);
                                }else{
                                    location_id=documentSnapshot.getString("location_id");
                                }
                            }else{

                                if(!TextUtils.isEmpty(phone)) {
                                    phone_field.setText(phone);
                                }else{
                                    phone_field.setText(mCurrentUser.getPhoneNumber());
                                }

                                location_id=locationUserId(7);
                                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                if (ActivityCompat.checkSelfPermission(ProfileSetup.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProfileSetup.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                onLocationChanged(location);
                                getLocationDetails(location);

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    private void contactPicked(android.content.Intent data) {
        Cursor cursor = null;
        try {
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String phoneNo = cursor.getString(phoneIndex);
            String name = cursor.getString(nameIndex);

            Contacts contact=new Contacts(name,phoneNo);
            contactsList.add(contact);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            cursor.close();
        }
    }


    public void onFabClicked(View view) {

        if(validate()){

            mDialog.show();
            if(!isOnline(this)){

                ContactsDatabase contactsDatabase = new ContactsDatabase(ProfileSetup.this);
                try {
                    for (int i = 0; i < contactsList.size(); i++)
                        contactsDatabase.deleteContact(i);

                }catch (Exception e){
                    e.printStackTrace();
                }

                for (int i = 0; i < contactsList.size(); i++)
                    contactsDatabase.insertContact(contactsList.get(i).getName(), contactsList.get(i).getPhone());

                UserDatabase userDatabase = new UserDatabase(ProfileSetup.this);
                try {
                    userDatabase.deleteContact(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userDatabase.insertContact(name_field.getText().toString(), phone_field.getText().toString(), city_field.getText().toString(),state_field.getText().toString(),age_field.getText().toString(),location_id,bgroup_field.getText().toString(),allergy_field.getText().toString(),donor_field.getText().toString());

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("radius", radius_field.getText().toString()).apply();

                mDialog.dismiss();
                Toast.makeText(ProfileSetup.this, "Welcome " + name_field.getText().toString(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileSetup.this, MainActivity.class));
                finish();

            }else {

                mFirestore.collection("Users")
                        .document(mCurrentUser.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (!documentSnapshot.exists()) {

                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("id", mCurrentUser.getUid());
                                    userMap.put("name", name_field.getText().toString());
                                    userMap.put("age", age_field.getText().toString());
                                    userMap.put("latitude",latitude);
                                    userMap.put("longitude",longitude);
                                    userMap.put("location_id",location_id);
                                    userMap.put("phone", phone_field.getText().toString());
                                    userMap.put("city", city_field.getText().toString());
                                    userMap.put("state", state_field.getText().toString());
                                    userMap.put("allergy", allergy_field.getText().toString());
                                    userMap.put("blood_group", bgroup_field.getText().toString());
                                    userMap.put("organ_donor", donor_field.getText().toString());

                                    mFirestore.collection("Users")
                                            .document(mCurrentUser.getUid())
                                            .set(userMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    for (int i = 0; i < contactsList.size(); i++) {
                                                        ContactsDatabase contactsDatabase = new ContactsDatabase(ProfileSetup.this);
                                                        contactsDatabase.insertContact(contactsList.get(i).getName(), contactsList.get(i).getPhone());
                                                    }

                                                    UserDatabase userDatabase = new UserDatabase(ProfileSetup.this);
                                                    try {
                                                        userDatabase.deleteContact(1);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    userDatabase.insertContact(name_field.getText().toString(), phone_field.getText().toString(), city_field.getText().toString(),state_field.getText().toString(),age_field.getText().toString(),location_id,bgroup_field.getText().toString(),allergy_field.getText().toString(),donor_field.getText().toString());

                                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                                    editor.putString("radius", radius_field.getText().toString()).apply();

                                                    mDialog.dismiss();
                                                    Toast.makeText(ProfileSetup.this, "Welcome " + name_field.getText().toString(), Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(ProfileSetup.this, MainActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mDialog.dismiss();
                                                    Log.e(TAG, e.getLocalizedMessage());
                                                }
                                            });

                                } else {

                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("id", mCurrentUser.getUid());
                                    userMap.put("name", name_field.getText().toString());
                                    userMap.put("age", age_field.getText().toString());
                                    userMap.put("phone", phone_field.getText().toString());
                                    userMap.put("latitude",latitude);
                                    userMap.put("longitude",longitude);
                                    userMap.put("location_id",location_id);
                                    userMap.put("city", city_field.getText().toString());
                                    userMap.put("state", state_field.getText().toString());
                                    userMap.put("allergy", allergy_field.getText().toString());
                                    userMap.put("blood_group", bgroup_field.getText().toString());
                                    userMap.put("organ_donor", donor_field.getText().toString());


                                    mFirestore.collection("Users")
                                            .document(mCurrentUser.getUid())
                                            .update(userMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    for (int i = 0; i < contactsList.size(); i++) {
                                                        ContactsDatabase contactsDatabase = new ContactsDatabase(ProfileSetup.this);
                                                        contactsDatabase.insertContact(contactsList.get(i).getName(), contactsList.get(i).getPhone());
                                                    }

                                                    UserDatabase userDatabase = new UserDatabase(ProfileSetup.this);
                                                    try {
                                                        userDatabase.deleteContact(1);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    userDatabase.insertContact(name_field.getText().toString(), phone_field.getText().toString(), city_field.getText().toString(),state_field.getText().toString(),age_field.getText().toString(),location_id,bgroup_field.getText().toString(),allergy_field.getText().toString(),donor_field.getText().toString());

                                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                                    editor.putString("radius", radius_field.getText().toString()).apply();

                                                    mDialog.dismiss();
                                                    Toast.makeText(ProfileSetup.this, "Welcome " + name_field.getText().toString(), Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(ProfileSetup.this, MainActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mDialog.dismiss();
                                                    Log.e(TAG, e.getLocalizedMessage());
                                                }
                                            });

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                });
            }


        }

    }

    public void onChangePhoneClicked(View view) {

        startActivity(new Intent(ProfileSetup.this, Login.class).putExtra("phone",phone_field.getText().toString()));

    }

    private boolean validate() {
        String name = name_field.getText().toString();
        String age = age_field.getText().toString();
        String phoneNumber = phone_field.getText().toString();
        String state = state_field.getText().toString();
        String city = city_field.getText().toString();
        String radius=radius_field.getText().toString();
        String organ=donor_field.getText().toString();
        String allergy=allergy_field.getText().toString();
        String bgroup=bgroup_field.getText().toString();

        if (TextUtils.isEmpty(organ) || TextUtils.isEmpty(allergy) || TextUtils.isEmpty(bgroup) || contactsList.isEmpty() || TextUtils.isEmpty(radius) || TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(state) || TextUtils.isEmpty(city)) {

            Snackbar.make(findViewById(R.id.layout), "Fill all details, Including emergency contacts.",
                    Snackbar.LENGTH_SHORT).show();

            return false;
        }


        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.i("Location", "latitude: " + latitude + "; longitude: " + longitude);


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getLocationDetails(Location location){

        try {
            Geocoder geocoder=new Geocoder(this);
            List<Address> addresses=null;
            addresses=geocoder.getFromLocation(latitude,longitude,1);

            String city=addresses.get(0).getLocality();
            String state=addresses.get(0).getAdminArea();
            city_field.setText(city);
            state_field.setText(state);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showBloodGroupDialogbox(View view) {

        new MaterialDialog.Builder(this)
                .title("Select your Blood group")
                .items(R.array.blood_groups)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                        String selected=text.toString();
                        bgroup_field.setText(selected);

                        return true;
                    }
                })
                .show();

    }

    public void showOrganDialogbox(View view) {

        new MaterialDialog.Builder(this)
                .title("Are you a organ donor?")
                .items(R.array.orgon_donor)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                        String selected=text.toString();
                        donor_field.setText(selected);

                        return true;
                    }
                })
                .show();

    }
}
