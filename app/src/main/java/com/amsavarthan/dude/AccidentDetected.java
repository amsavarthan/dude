package com.amsavarthan.dude;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsavarthan.dude.adapter.AccidentContactsAdapter;
import com.amsavarthan.dude.adapter.ContactsAdapter;
import com.amsavarthan.dude.models.Contacts;
import com.amsavarthan.dude.utils.ContactsDatabase;
import com.amsavarthan.dude.utils.UserDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.erehmi.countdown.CountDownTask;
import io.github.erehmi.countdown.CountDownTimers;

public class AccidentDetected extends AppCompatActivity implements LocationListener {

    private LocationManager lm;
    public double latitude, longitude;
    private ContactsDatabase contactsDatabase;
    private SQLiteDatabase sqLiteDatabase;
    private UserDatabase userDatabase;
    RecyclerView contactsList;
    ImageView close;
    AccidentContactsAdapter adapter;
    List<Contacts> contacts=new ArrayList<>();
    TextView countdowntext;
    LinearLayout accident_layout,countdown_layout;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_detected);

        final SmsManager sms = SmsManager.getDefault();
        activity=this;
        lm=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 5, this);
        contactsDatabase=new ContactsDatabase(this);
        userDatabase=new UserDatabase(this);
        adapter=new AccidentContactsAdapter(contacts);
        close=findViewById(R.id.close);
        contactsList=findViewById(R.id.contacts_list);
        countdowntext=findViewById(R.id.countdowntimer);
        countdown_layout=findViewById(R.id.timer_layout);
        accident_layout=findViewById(R.id.message_layout);
        countdown_layout.setVisibility(View.VISIBLE);
        accident_layout.setVisibility(View.GONE);
        final CountDownTask countDownTask = CountDownTask.create();
        long targetMillis = CountDownTask.elapsedRealtime() + 1000 * 6;//6sec

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTask.cancel(countdowntext);
                finish();
            }
        });


        final int CD_INTERVAL = 1000;

        contactsList.setHasFixedSize(true);
        contactsList.setItemAnimator(new DefaultItemAnimator());
        contactsList.setLayoutManager(new LinearLayoutManager(this));
        contactsList.setAdapter(adapter);

        try {

            sqLiteDatabase = contactsDatabase.getReadableDatabase();
            Cursor cursor = contactsDatabase.getinformation(sqLiteDatabase);
            if (cursor.moveToFirst()) {
                do {
                    String contact_name, contact_phone;
                    contact_name = cursor.getString(0);
                    contact_phone = cursor.getString(1);
                    Contacts contact = new Contacts(contact_name, contact_phone);
                    contacts.add(contact);
                    adapter.notifyDataSetChanged();

                } while (cursor.moveToNext());
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        countDownTask.until(countdowntext, targetMillis, CD_INTERVAL, new CountDownTimers.OnCountDownListener() {
            @Override
            public void onTick(View view, long millisUntilFinished) {
                countdown_layout.setVisibility(View.VISIBLE);
                accident_layout.setVisibility(View.GONE);
                ((TextView)view).setText(String.valueOf(millisUntilFinished / CD_INTERVAL));
            }
            @Override
            public void onFinish(View view) {

                countdown_layout.setVisibility(View.GONE);
                accident_layout.setVisibility(View.VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            Cursor rs = userDatabase.getData(1);
                            rs.moveToFirst();

                            String name = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_NAME));

                            if (!rs.isClosed()) {
                                rs.close();
                            }

                            sqLiteDatabase = contactsDatabase.getReadableDatabase();
                            Cursor cursor = contactsDatabase.getinformation(sqLiteDatabase);
                            if (cursor.moveToFirst()) {
                                do {
                                    String contact_name, contact_phone;
                                    contact_name = cursor.getString(0);
                                    contact_phone = cursor.getString(1);

                                    sms.sendTextMessage(contact_phone, null, "Help! "+name+" met with an accident at http://maps.google.com/?q="+String.valueOf(latitude)+","+String.valueOf(longitude), null, null);
                                    sms.sendTextMessage(contact_phone, null, "Nearby Hospitals http://maps.google.com/maps?q=hospital&mrt=yp&sll="+String.valueOf(latitude)+","+String.valueOf(longitude)+"&output=kml", null, null);


                                } while (cursor.moveToNext());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        System.exit(1);
                    }
                }, 10000);

            }
        });





    }

    @Override
    public void onLocationChanged(Location location){
        latitude=location.getLatitude();
        longitude=location.getLongitude();
       // Toast.makeText(getApplicationContext(),"Getting current location",Toast.LENGTH_LONG).show();
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
}
