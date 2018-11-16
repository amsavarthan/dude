package com.amsavarthan.dude.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.adapter.CategoriesAdapter;
import com.amsavarthan.dude.models.CategoryItems;
import com.amsavarthan.dude.utils.ContactsDatabase;
import com.amsavarthan.dude.utils.GoogleService;
import com.amsavarthan.dude.utils.NetworkUtil;
import com.amsavarthan.dude.utils.ShakeService;
import com.amsavarthan.dude.utils.UserDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stone.vega.library.VegaLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import shortbread.Shortbread;
import shortbread.Shortcut;

import static com.amsavarthan.dude.utils.Utils.isMyServiceRunning;
import static com.amsavarthan.dude.utils.Utils.isOnline;

public class MainActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int REQUEST_PERMISSIONS = 200;
    private int cameraPermissionCheck = PackageManager.PERMISSION_DENIED;
    private int externalStoragePermissionCheck = PackageManager.PERMISSION_DENIED;
    private RecyclerView mRecyclerview;
    private CategoriesAdapter adapter1;
    private List<CategoryItems> list1;
    private TextView name,greeting;
    private ImageView time_image;
    private LinearLayout top;
    Double latitude,longitude;
    Geocoder geocoder;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;
    public BroadcastReceiver NetworkChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = NetworkUtil.getConnectivityStatusString(context);
            Log.i("Network reciever", "OnReceive: "+status);
            if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if (status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {

                    performUploadTask();
                    Snackbar.make(findViewById(R.id.main), "Syncing...", Snackbar.LENGTH_LONG).show();

                }
            }
        }

    };
    private boolean boolean_permission;

    private void performUploadTask() {

        try {

            mFirestore.collection("Users")
                    .document(mCurrentUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            UserDatabase userDatabase = new UserDatabase(MainActivity.this);

                            Cursor rs = userDatabase.getData(1);
                            rs.moveToFirst();

                            final String nam = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_NAME));
                            String phon = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_PHONE));
                            String age = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_AGE));
                            String city = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_CITY));
                            String state = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_STATE));
                            String location_id = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_LID));
                            String allergy = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_ALLERGY));
                            String bgroup = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_BGROUP));
                            String organ = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_DONOR));

                            if (!rs.isClosed()) {
                                rs.close();
                            }

                            if (TextUtils.isEmpty(nam) && !documentSnapshot.exists()) {

                                Toast.makeText(MainActivity.this, "It seems you have not setup your profile, taking you back...", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this, ProfileSetup.class));
                                finish();

                            } else if (!documentSnapshot.exists()) {

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("id", mCurrentUser.getUid());
                                userMap.put("phone", phon);
                                userMap.put("city", city);
                                userMap.put("state", state);
                                userMap.put("name", nam);
                                userMap.put("age", age);
                                userMap.put("latitude",0.0);
                                userMap.put("longitude",0.0);
                                userMap.put("location_id",location_id);
                                userMap.put("allergy", allergy);
                                userMap.put("blood_group", bgroup);
                                userMap.put("organ_donor", organ);

                                mFirestore.collection("Users")
                                        .document(mCurrentUser.getUid())
                                        .set(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("Update", "success");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Update", "error : " + e.getLocalizedMessage());
                                                e.printStackTrace();
                                            }
                                        });


                            } else if (!documentSnapshot.getString("name").equals(nam)) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("id", mCurrentUser.getUid());
                                userMap.put("phone", phon);
                                userMap.put("city", city);
                                userMap.put("state", state);
                                userMap.put("name", nam);
                                userMap.put("age", age);
                                userMap.put("location_id",location_id);
                                userMap.put("allergy", allergy);
                                userMap.put("blood_group", bgroup);
                                userMap.put("organ_donor", organ);


                                mFirestore.collection("Users")
                                        .document(mCurrentUser.getUid())
                                        .update(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("Update", "success");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Update", "error : " + e.getLocalizedMessage());
                                                e.printStackTrace();
                                            }
                                        });
                            } else if (!documentSnapshot.getString("phone").equals(phon)) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("id", mCurrentUser.getUid());
                                userMap.put("phone", phon);
                                userMap.put("city", city);
                                userMap.put("state", state);
                                userMap.put("name", nam);
                                userMap.put("age", age);
                                userMap.put("location_id",location_id);
                                userMap.put("allergy", allergy);
                                userMap.put("blood_group", bgroup);
                                userMap.put("organ_donor", organ);


                                mFirestore.collection("Users")
                                        .document(mCurrentUser.getUid())
                                        .update(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                name.setText(nam);
                                                Log.i("Update", "success");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Update", "error : " + e.getLocalizedMessage());
                                                e.printStackTrace();
                                            }
                                        });
                            }else if (!documentSnapshot.getString("allergy").equals(allergy)) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("id", mCurrentUser.getUid());
                                userMap.put("phone", phon);
                                userMap.put("city", city);
                                userMap.put("state", state);
                                userMap.put("name", nam);
                                userMap.put("age", age);
                                userMap.put("location_id",location_id);
                                userMap.put("allergy", allergy);
                                userMap.put("blood_group", bgroup);
                                userMap.put("organ_donor", organ);


                                mFirestore.collection("Users")
                                        .document(mCurrentUser.getUid())
                                        .update(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                name.setText(nam);
                                                Log.i("Update", "success");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Update", "error : " + e.getLocalizedMessage());
                                                e.printStackTrace();
                                            }
                                        });
                            }else if (!documentSnapshot.getString("blood_group").equals(bgroup)) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("id", mCurrentUser.getUid());
                                userMap.put("phone", phon);
                                userMap.put("city", city);
                                userMap.put("state", state);
                                userMap.put("name", nam);
                                userMap.put("age", age);
                                userMap.put("location_id",location_id);
                                userMap.put("allergy", allergy);
                                userMap.put("blood_group", bgroup);
                                userMap.put("organ_donor", organ);


                                mFirestore.collection("Users")
                                        .document(mCurrentUser.getUid())
                                        .update(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                name.setText(nam);
                                                Log.i("Update", "success");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Update", "error : " + e.getLocalizedMessage());
                                                e.printStackTrace();
                                            }
                                        });
                            }else if (!documentSnapshot.getString("organ_donor").equals(organ)) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("id", mCurrentUser.getUid());
                                userMap.put("phone", phon);
                                userMap.put("city", city);
                                userMap.put("state", state);
                                userMap.put("name", nam);
                                userMap.put("age", age);
                                userMap.put("location_id",location_id);
                                userMap.put("allergy", allergy);
                                userMap.put("blood_group", bgroup);
                                userMap.put("organ_donor", organ);


                                mFirestore.collection("Users")
                                        .document(mCurrentUser.getUid())
                                        .update(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                name.setText(nam);
                                                Log.i("Update", "success");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Update", "error : " + e.getLocalizedMessage());
                                                e.printStackTrace();
                                            }
                                        });
                            }else if (!documentSnapshot.getString("location_id").equals(location_id)) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("id", mCurrentUser.getUid());
                                userMap.put("phone", phon);
                                userMap.put("city", city);
                                userMap.put("state", state);
                                userMap.put("name", nam);
                                userMap.put("age", age);
                                userMap.put("latitude",latitude);
                                userMap.put("longitude",longitude);
                                userMap.put("location_id",location_id);
                                userMap.put("allergy", allergy);
                                userMap.put("blood_group", bgroup);
                                userMap.put("organ_donor", organ);


                                mFirestore.collection("Users")
                                        .document(mCurrentUser.getUid())
                                        .update(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                name.setText(nam);
                                                Log.i("Update", "success");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Update", "error : " + e.getLocalizedMessage());
                                                e.printStackTrace();
                                            }
                                        });
                            } else {
                                Log.i("Update", "...");
                            }

                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(NetworkChangeReceiver);
        stopService(new Intent(this,GoogleService.class));
    }

    @Shortcut(id = "sos", icon = R.drawable.ic_sos, shortLabel = "Trigger SOS")
    public void triggerSOS() {
        startActivity(new Intent(MainActivity.this, FragmentContainer.class).putExtra("name","sos_"));
    }

    @Shortcut(id = "report", icon = R.drawable.ic_report, shortLabel = "Report an incident")
    public void sendReport() {
        startActivity(new Intent(MainActivity.this, FragmentContainer.class).putExtra("name","reports"));
    }

    @Shortcut(id = "scan_question", icon = R.drawable.ic_shortcut_camera, shortLabel = "Scan a question")
    public void scanQuestion() {
        if (cameraPermissionCheck == PackageManager.PERMISSION_GRANTED && externalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(MainActivity.this, FragmentContainer.class).putExtra("name","camera"));
        } else {
            verifyAndRequestPermission();
        }
    }

    public static void startActivity(Context context,String fragment){
        context.startActivity(new Intent(context,MainActivity.class).putExtra("frag",fragment));
    }

    private void verifyAndRequestPermission() {

        cameraPermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);

        externalStoragePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED || externalStoragePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
        }

    }

    private boolean isDeviceSupportCamera() {
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

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

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
                break;
            }
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        cameraPermissionCheck = PackageManager.PERMISSION_GRANTED;
                        setItems();
                    }
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        externalStoragePermissionCheck = PackageManager.PERMISSION_GRANTED;
                        setItems();
                    }

                }
            }
        }

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));

            Map<String,Object> userMap=new HashMap<>();
            userMap.put("latitude",latitude);
            userMap.put("longitude",longitude);

            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .update(userMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("location","updated..");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String cityName = addresses.get(0).getLocality();
                String stateName = addresses.get(0).getAdminArea();
                String countryName = addresses.get(0).getCountryName();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initializeActivity();
        super.onCreate(savedInstanceState);
        Shortbread.create(this);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            verifyAndRequestPermission();

        mFirestore=FirebaseFirestore.getInstance();
        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();

        if(mCurrentUser!=null) {

            fn_permission();

            registerReceiver(NetworkChangeReceiver
                    , new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);

            /*
            PowerGestureReceiver mReceiver = new PowerGestureReceiver ();
            registerReceiver(mReceiver, filter);
             if(!isMyServiceRunning(GestureService.class,this)) {
                startService(new Intent(this, GestureService.class));
            }
            */

            if(!isMyServiceRunning(GoogleService.class,this)) {
                startService(new Intent(this,GoogleService.class));
            }

            if(!isMyServiceRunning(GoogleService.class,this)) {
                startService(new Intent(this,ShakeService.class));
            }

            if (isOnline(this)) {
                performUploadTask();
            }

            geocoder = new Geocoder(this, Locale.getDefault());
            mRecyclerview = findViewById(R.id.recyclerview);
            name = findViewById(R.id.name);
            greeting = findViewById(R.id.greeting);
            time_image = findViewById(R.id.image_time);
            top = findViewById(R.id.top);

            UserDatabase userdata = new UserDatabase(this);

            try {

                Cursor rs = userdata.getData(1);
                rs.moveToFirst();

                String nam = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_NAME));

                if (!rs.isClosed()) {
                    rs.close();
                }

                name.setText(nam);

                mFirestore.collection("Users")
                        .document(mCurrentUser.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                UserDatabase userDatabase = new UserDatabase(MainActivity.this);

                                Cursor rs = userDatabase.getData(1);
                                rs.moveToFirst();

                                String username = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_NAME));

                                if (!rs.isClosed()) {
                                    rs.close();
                                }

                                if (TextUtils.isEmpty(username) && !documentSnapshot.exists()) {
                                    Toast.makeText(MainActivity.this, "It seems you have not setup your profile, taking you back...", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(MainActivity.this, ProfileSetup.class));
                                    finish();
                                } else if (!documentSnapshot.exists()) {
                                    performUploadTask();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });

            } catch (CursorIndexOutOfBoundsException e) {

                mFirestore.collection("Users")
                        .document(mCurrentUser.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                UserDatabase userDatabase = new UserDatabase(MainActivity.this);

                                Cursor rs = userDatabase.getData(1);
                                rs.moveToFirst();

                                String username = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_NAME));

                                if (!rs.isClosed()) {
                                    rs.close();
                                }

                                if (TextUtils.isEmpty(username) && !documentSnapshot.exists()) {
                                    Toast.makeText(MainActivity.this, "It seems you have not setup your profile, taking you back...", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(MainActivity.this, ProfileSetup.class));
                                    finish();
                                } else if (!documentSnapshot.exists()) {
                                    performUploadTask();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }

            changeBg();
            setupRecyclerviews();
            setItems();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCurrentUser!=null) {
            changeBg();
            registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));

            if(isOnline(this)){
                performUploadTask();
            }

            if(!isMyServiceRunning(GoogleService.class,this)) {
                startService(new Intent(this, ShakeService.class));
            }else{
                stopService(new Intent(this, ShakeService.class));
                startService(new Intent(this, ShakeService.class));
            }


            try {
                UserDatabase userDatabase = new UserDatabase(MainActivity.this);

                Cursor rs = userDatabase.getData(1);
                rs.moveToFirst();

                String username = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_NAME));

                if (!rs.isClosed()) {
                    rs.close();
                }

                name.setText(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void changeBg(){

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            greeting.setText("Good Morning");
            time_image.setImageResource(R.mipmap.morning);

            top.setBackgroundResource(R.drawable.gradient_morning);
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greeting.setText("Good Afternoon");
            time_image.setImageResource(R.mipmap.morning);

            top.setBackgroundResource(R.drawable.gradient_afternoon);
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            greeting.setText("Good Evening");
            time_image.setImageResource(R.mipmap.eve);

            top.setBackgroundResource(R.drawable.gradient_eve);
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            greeting.setText("Good Night");
            time_image.setImageResource(R.mipmap.night);

            top.setBackgroundResource(R.drawable.gradient_night);
        }


    }

    private void setupRecyclerviews() {

        list1=new ArrayList<>();
        adapter1=new CategoriesAdapter(list1);

        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new VegaLayoutManager());
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mRecyclerview.setAdapter(adapter1);

    }

    private void setItems() {

        list1.clear();

        CategoryItems item=new CategoryItems("SOS",String.valueOf(getResources().getColor(R.color.red)),"sos","Emergency");
        list1.add(item);

        item=new CategoryItems("Locate Friends",String.valueOf(getResources().getColor(R.color.amber)),"locate","Emergency");
        list1.add(item);

        item=new CategoryItems("Driving Mode",String.valueOf(getResources().getColor(R.color.pink)),"drive","Emergency");
        list1.add(item);

        item=new CategoryItems("Report an incident",String.valueOf(getResources().getColor(R.color.green)),"reports","Citizen Cop");
        list1.add(item);

        item=new CategoryItems("My Reports",String.valueOf(getResources().getColor(R.color.deep_orange)),"my_reports","Citizen Cop");
        list1.add(item);

        if(isDeviceSupportCamera()){
            if (cameraPermissionCheck == PackageManager.PERMISSION_GRANTED && externalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED) {
                item = new CategoryItems("Scan a question", String.valueOf(getResources().getColor(R.color.blue)), "camera","Homework Help");
                list1.add(item);
            }
        }

        item=new CategoryItems("Enter a question",String.valueOf(getResources().getColor(R.color.orange)),"input","Homework Help");
        list1.add(item);

        item=new CategoryItems("Tell a question",String.valueOf(getResources().getColor(R.color.pink)),"speech","Homework Help");
        list1.add(item);

        item=new CategoryItems("Math functions",String.valueOf(getResources().getColor(R.color.green)),"math","Homework Help");
        list1.add(item);

        item=new CategoryItems("Global Questions",String.valueOf(getResources().getColor(R.color.deep_purple)),"forum","Forum");
        list1.add(item);

        item=new CategoryItems("My Questions",String.valueOf(getResources().getColor(R.color.amber)),"my_forum","Forum");
        list1.add(item);

        item=new CategoryItems("Nearby places",String.valueOf(getResources().getColor(R.color.pink)),"nearby","Miscellaneous");
        list1.add(item);

        item=new CategoryItems("Reminder",String.valueOf(getResources().getColor(R.color.green)),"reminder","Miscellaneous");
        list1.add(item);

        adapter1.notifyDataSetChanged();

    }



    public void openSettings(View view) {
        Settings.startActivity(this);
    }

    public void onLogoutClicked(View view) {

        new MaterialDialog.Builder(this)
                .title("Logout")
                .content("Are you sure do you want to logout?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        ProgressDialog mDialog=new ProgressDialog(MainActivity.this);
                        mDialog.setMessage("Logging out...");
                        mDialog.setIndeterminate(true);
                        mDialog.setCancelable(false);
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();
                        try {

                            mCurrentUser=null;
                            UserDatabase userDatabase=new UserDatabase(MainActivity.this);
                            userDatabase.deleteContact(1);

                            ContactsDatabase contactsDatabase=new ContactsDatabase(MainActivity.this);
                            contactsDatabase.deleteAll();

                            mDialog.dismiss();

                            FirebaseAuth.getInstance().signOut();
                            finish();

                        }catch (Exception e){
                            mDialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
}
