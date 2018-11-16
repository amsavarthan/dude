package com.amsavarthan.dude.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.adapter.ContactsAdapter;
import com.amsavarthan.dude.models.Contacts;
import com.amsavarthan.dude.utils.ContactsDatabase;
import com.amsavarthan.dude.utils.GoogleService;
import com.amsavarthan.dude.utils.ShakeService;
import com.amsavarthan.dude.utils.UserDatabase;
import com.amsavarthan.dude.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.amsavarthan.dude.utils.Utils.isMyServiceRunning;
import static com.amsavarthan.dude.utils.Utils.isOnline;

public class Settings extends AppCompatActivity {

    private static final int PICK_CONTACT = 1002;
    EditText name_field,phone_field,radius_field,allergy_field,bgroup_field,donor_field;
    RecyclerView recyclerView;
    ContactsAdapter adapter;
    List<Contacts> contactsList=new ArrayList<>();
    FloatingActionButton add;
    private UserDatabase userdata;
    private ContactsDatabase contactsDatabase;
    private SQLiteDatabase sqLiteDatabase;
    private ProgressDialog mDialog;
    Switch accidetect;

    public static void startActivity(Context context){
        context.startActivity(new Intent(context,Settings.class));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(base));
    }

    private void initializeActivity() {

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
        setContentView(R.layout.activity_settings);

        name_field=findViewById(R.id.name);
        phone_field=findViewById(R.id.phone);
        radius_field=findViewById(R.id.radius);
        recyclerView=findViewById(R.id.contact_list);
        adapter=new ContactsAdapter(contactsList);
        add=findViewById(R.id.add);
        allergy_field=findViewById(R.id.allergies);
        bgroup_field=findViewById(R.id.bgroup);
        donor_field=findViewById(R.id.organ);
        accidetect=findViewById(R.id.acciswitch);

        if(!isMyServiceRunning(GoogleService.class,Settings.this)) {
            accidetect.setChecked(false);
        }else{
            accidetect.setChecked(true);
        }

        accidetect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!isMyServiceRunning(GoogleService.class,Settings.this)) {
                        startService(new Intent(Settings.this, ShakeService.class));
                    }
                }else{
                    if(isMyServiceRunning(GoogleService.class,Settings.this)) {
                        stopService(new Intent(Settings.this, ShakeService.class));
                    }
                }
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        contactsDatabase=new ContactsDatabase(this);
        userdata=new UserDatabase(this);

        try {

            Cursor rs = userdata.getData(1);
            rs.moveToFirst();

            String name = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_NAME));
            String phone = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_PHONE));
            String allergy = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_ALLERGY));
            String bgroup = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_BGROUP));
            String organ = rs.getString(rs.getColumnIndex(UserDatabase.CONTACTS_COLUMN_DONOR));

            if (!rs.isClosed()) {
                rs.close();
            }

            name_field.setText(name);
            phone_field.setText(phone);
            allergy_field.setText(allergy);
            bgroup_field.setText(bgroup);
            donor_field.setText(organ);

        }catch (CursorIndexOutOfBoundsException e){

            FirebaseFirestore.getInstance().collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(!documentSnapshot.exists()){
                                Toast.makeText(Settings.this, "It seems you have not setup your profile, taking you back...", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Settings.this,ProfileSetup.class));
                                finish();
                            }else{
                                String name = documentSnapshot.getString("name");
                                String phone = documentSnapshot.getString("phone");

                                name_field.setText(name);
                                phone_field.setText(phone);
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

        SharedPreferences sharedpreferences = getSharedPreferences(Utils.RADIUS, Context.MODE_PRIVATE);
        radius_field.setText(sharedpreferences.getString("radius","5000"));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });

        try {

            sqLiteDatabase = contactsDatabase.getReadableDatabase();
            Cursor cursor = contactsDatabase.getinformation(sqLiteDatabase);
            if (cursor.moveToFirst()) {
                do {
                    String contact_name, contact_phone;
                    contact_name = cursor.getString(0);
                    contact_phone = cursor.getString(1);
                    Contacts contacts = new Contacts(contact_name, contact_phone);
                    contactsList.add(contacts);
                    adapter.notifyDataSetChanged();

                } while (cursor.moveToNext());
            }
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


    public void onChangePhoneClicked(View view) {
        if(isOnline(this)) {
            startActivity(new Intent(Settings.this, Login.class).putExtra("phone", phone_field.getText().toString()));
        }else{
            Snackbar.make(findViewById(R.id.layout), "You are offline.",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        String name = name_field.getText().toString();
        String phoneNumber = phone_field.getText().toString();
        String radius=radius_field.getText().toString();
        String organ=donor_field.getText().toString();
        String allergy=allergy_field.getText().toString();
        String bgroup=bgroup_field.getText().toString();

        if (TextUtils.isEmpty(organ) || TextUtils.isEmpty(allergy) || TextUtils.isEmpty(bgroup) ||contactsList.isEmpty()||TextUtils.isEmpty(radius) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber)) {
            Snackbar.make(findViewById(R.id.layout), "Fill all details, Including emergency contacts.",
                    Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;


    }

    public void onSaveClicked(View view) {

        if(validate()){

            if(isOnline(this)){

                mDialog=new ProgressDialog(this);
                mDialog.setMessage("Saving settings...");
                mDialog.setIndeterminate(true);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setCancelable(false);

                mDialog.show();
                Map<String,Object> userMap=new HashMap<>();
                userMap.put("name",name_field.getText().toString());
                userMap.put("phone",phone_field.getText().toString());
                userMap.put("allergy", allergy_field.getText().toString());
                userMap.put("blood_group", bgroup_field.getText().toString());
                userMap.put("organ_donor", donor_field.getText().toString());

                FirebaseFirestore.getInstance().collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .update(userMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDialog.dismiss();

                                ContactsDatabase contactsDatabase=new ContactsDatabase(Settings.this);
                                contactsDatabase.deleteAll();
                                for(int i=0;i<contactsList.size();i++){
                                    contactsDatabase.insertContact(contactsList.get(i).getName(),contactsList.get(i).getPhone());
                                }

                                UserDatabase userDatabase=new UserDatabase(Settings.this);
                                userDatabase.updateContactName(1,name_field.getText().toString());
                                userDatabase.updateContactPhone(1,phone_field.getText().toString());
                                userDatabase.updateContactAllergy(1,allergy_field.getText().toString());
                                userDatabase.updateContactBGroup(1,bgroup_field.getText().toString());
                                userDatabase.updateContactDonor(1,donor_field.getText().toString());

                                SharedPreferences sharedpreferences = getSharedPreferences(Utils.RADIUS, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedpreferences.edit();
                                editor.putString("radius",radius_field.getText().toString()).apply();

                                mDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDialog.dismiss();
                                Log.e("Settings", e.getLocalizedMessage());
                            }
                        });


            }else {

                UserDatabase userDatabase=new UserDatabase(Settings.this);
                userDatabase.updateContactName(1,name_field.getText().toString());
                userDatabase.updateContactPhone(1,phone_field.getText().toString());
                userDatabase.updateContactAllergy(1,allergy_field.getText().toString());
                userDatabase.updateContactBGroup(1,bgroup_field.getText().toString());
                userDatabase.updateContactDonor(1,donor_field.getText().toString());

                ContactsDatabase contactsDatabase=new ContactsDatabase(Settings.this);
                contactsDatabase.deleteAll();
                for(int i=0;i<contactsList.size();i++){
                    contactsDatabase.insertContact(contactsList.get(i).getName(),contactsList.get(i).getPhone());
                }

                SharedPreferences sharedpreferences = getSharedPreferences(Utils.RADIUS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedpreferences.edit();
                editor.putString("radius",radius_field.getText().toString()).apply();

                Snackbar.make(findViewById(R.id.layout), "Details saved and will be uploaded when online.",
                        Snackbar.LENGTH_SHORT).show();

            }
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
