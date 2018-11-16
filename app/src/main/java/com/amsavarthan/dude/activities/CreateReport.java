package com.amsavarthan.dude.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.adapter.UploadListAdapter;
import com.amsavarthan.dude.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.grpc.Compressor;
import rx.schedulers.Schedulers;


import static android.media.MediaRecorder.OutputFormat.MPEG_4;
import static com.amsavarthan.dude.adapter.UploadListAdapter.uploadedImagesUrl;
import static com.amsavarthan.dude.utils.Utils.DATA_PATH;

public class CreateReport extends AppCompatActivity implements LocationListener,DatePickerDialog.OnDateSetListener {


    FirebaseFirestore mFirestore;
    FirebaseUser mCurrentUser;
    FloatingActionButton capture,pick,video;
    private LocationManager locationManager;
    EditText date,location,info;
    private double latitude,longitude;

    private Map<String, Object> postMap;
    private ProgressDialog mDialog;
    private ArrayList<Image> imagesList;
    private List<String> fileNameList;
    private List<String> fileUriList;
    private List<String> fileDoneList;
    public static boolean canUpload=false;

    private UploadListAdapter uploadListAdapter;
    private StorageReference mStorage;
    private RecyclerView mUploadList;

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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initializeActivity();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        mFirestore=FirebaseFirestore.getInstance();
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();

        capture=findViewById(R.id.capture_img);
        pick =findViewById(R.id.pick_img);
        date=findViewById(R.id.date);
        location=findViewById(R.id.location);
        info=findViewById(R.id.info);

        Utils.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Utils.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        postMap = new HashMap<>();

        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        fileUriList = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileUriList,fileNameList, fileDoneList);

        //RecyclerView
        mUploadList=findViewById(R.id.attachment_recyclerview);
        mUploadList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUploadList.setLayoutManager(layoutManager);
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);

        mDialog = new ProgressDialog(this);
        mStorage= FirebaseStorage.getInstance().getReference();


    }

    @NonNull
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.RC_PICK_IMAGES ) {
            if (resultCode == RESULT_OK && data != null) {
                imagesList = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                if (!imagesList.isEmpty()) {

                    for (int i = 0; i < imagesList.size(); i++) {

                            final int finalI = i;
                            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                            options.quality=60;
                            Tiny.getInstance().source(imagesList.get(i).getPath()).asFile().withOptions(options).compress(new FileCallback() {
                                @Override
                                public void callback(boolean isSuccess, String outfile, Throwable t) {

                                    if(isSuccess) {
                                        File compressedImage = new File(outfile);

                                        Uri fileUri = Uri.fromFile(compressedImage);
                                        String fileName = compressedImage.getName();

                                        fileNameList.add(fileName);
                                        fileUriList.add(fileUri.toString());
                                        fileDoneList.add("uploading");
                                        uploadListAdapter.notifyDataSetChanged();

                                        final StorageReference fileToUpload = mStorage.child("report_attachments").child("Report_" + System.currentTimeMillis() + "_" + random() + ".jpg");

                                        fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        fileDoneList.remove(finalI);
                                                        fileDoneList.add(finalI, "done");
                                                        uploadedImagesUrl.add(uri.toString());
                                                        uploadListAdapter.notifyDataSetChanged();
                                                    }
                                                });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    }else{
                                        t.printStackTrace();
                                        Toast.makeText(CreateReport.this, "Compression failed with error : "+t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                    }


                }
            }
        }

    }

    public void setLocationandDate(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(CreateReport.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateReport.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        getLocationDetails(location);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM, yyyy");
        String formattedDate = df.format(c);
        date.setText("");
        date.setText(formattedDate);
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude=location.getLatitude();
        longitude=location.getLongitude();
        Log.i("Location","latitude: "+latitude+"; longitude: "+longitude);

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

    private void getLocationDetails(Location locationn){

        try {
            Geocoder geocoder=new Geocoder(this);
            List<Address> addresses=null;
            addresses=geocoder.getFromLocation(latitude,longitude,1);

            String city=addresses.get(0).getLocality();
            String state=addresses.get(0).getAdminArea();
            location.setText(String.format("%s, %s", city, state));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startPickImage(boolean gallery) {

        if(gallery) {

            ImagePicker.with(this)
                    .setToolbarColor("#FFFFFF")
                    .setStatusBarColor("#CCCCCC")
                    .setToolbarTextColor("#212121")
                    .setToolbarIconColor("#212121")
                    .setProgressBarColor("#5093FF")
                    .setBackgroundColor("#FFFFFF")
                    .setCameraOnly(false)
                    .setMultipleMode(true)
                    .setFolderMode(true)
                    .setShowCamera(false)
                    .setFolderTitle("Albums")
                    .setImageTitle("Photos")
                    .setDoneTitle("Done")
                    .setLimitMessage("You have reached selection limit")
                    .setMaxSize(7)
                    .setSavePath("DUDE")
                    .setAlwaysShowDoneButton(true)
                    .setKeepScreenOn(true)
                    .start();

        }else{

            ImagePicker.with(this)
                    .setToolbarColor("#FFFFFF")
                    .setStatusBarColor("#CCCCCC")
                    .setToolbarTextColor("#212121")
                    .setToolbarIconColor("#212121")
                    .setProgressBarColor("#5093FF")
                    .setBackgroundColor("#FFFFFF")
                    .setCameraOnly(true)
                    .setMultipleMode(true)
                    .setDoneTitle("Done")
                    .setLimitMessage("You have reached capture limit")
                    .setMaxSize(7)
                    .setSavePath("DUDE")
                    .setKeepScreenOn(true)
                    .start();

        }

    }

    private void uploadPost() {

        mDialog=new ProgressDialog(this);
        mDialog.setMessage("Posting...");
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        if(canUpload) {
            if (!uploadedImagesUrl.isEmpty()) {

                mDialog.show();

                mFirestore.collection("Users").document(mCurrentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        postMap.put("image_count", uploadedImagesUrl.size());
                        try {
                            postMap.put("image_url_0", uploadedImagesUrl.get(0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_1", uploadedImagesUrl.get(1));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_2", uploadedImagesUrl.get(2));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_3", uploadedImagesUrl.get(3));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_4", uploadedImagesUrl.get(4));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_5", uploadedImagesUrl.get(5));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            postMap.put("image_url_6", uploadedImagesUrl.get(6));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        postMap.put("user_id",mCurrentUser.getUid());
                        postMap.put("name",documentSnapshot.getString("name"));
                        postMap.put("by","user");
                        postMap.put("info",info.getText().toString());
                        postMap.put("date",date.getText().toString());
                        postMap.put("status","Received");
                        postMap.put("location",location.getText().toString());
                        postMap.put("timestamp",String.valueOf(System.currentTimeMillis()));

                        mFirestore.collection("Reports")
                                .add(postMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        mDialog.dismiss();
                                        Toast.makeText(CreateReport.this, "Report recorded", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mDialog.dismiss();
                                        Log.e("Error sending post", e.getMessage());
                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Log.e("Error getting user", e.getMessage());
                    }
                });

            } else {
                mDialog.dismiss();
                Toast.makeText(this, "No image has been uploaded, Please wait or try again", Toast.LENGTH_SHORT).show();
            }
        }else{
            if (!uploadedImagesUrl.isEmpty()) {
                Toast.makeText(this, "Please wait, images are uploading...", Toast.LENGTH_SHORT).show();
            }else{
                new MaterialDialog.Builder(this)
                        .title("Send Report")
                        .content("It seems that you haven't added any attachments or maybe it is uploading.., Are you sure do you want to send the report ?")
                        .positiveText("Yes")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                mDialog.show();

                                mFirestore.collection("Users").document(mCurrentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        postMap.put("image_count", 0);
                                        postMap.put("name",documentSnapshot.getString("name"));
                                        postMap.put("user_id",mCurrentUser.getUid());
                                        postMap.put("info",info.getText().toString());
                                        postMap.put("date",date.getText().toString());
                                        postMap.put("by","user");
                                        postMap.put("status","Received");
                                        postMap.put("location",location.getText().toString());
                                        postMap.put("timestamp",String.valueOf(System.currentTimeMillis()));

                                        mFirestore.collection("Reports")
                                                .add(postMap)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        mDialog.dismiss();
                                                        Toast.makeText(CreateReport.this, "Report recorded", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        mDialog.dismiss();
                                                        Log.e("Error sending post", e.getMessage());
                                                    }
                                                });


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mDialog.dismiss();
                                        Log.e("Error getting user", e.getMessage());
                                    }
                                });

                            }
                        })
                        .negativeText("No")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    public void sendReport(View view) {

        if(TextUtils.isEmpty(date.getText().toString())){

            Animation shake= AnimationUtils.loadAnimation(this, R.anim.shake_view);
            date.startAnimation(shake);
            return;

        }

        if(TextUtils.isEmpty(location.getText().toString())){

            Animation shake= AnimationUtils.loadAnimation(this, R.anim.shake_view);
            location.startAnimation(shake);
            return;

        }

        if(TextUtils.isEmpty(info.getText().toString())){

            Animation shake= AnimationUtils.loadAnimation(this, R.anim.shake_view);
            info.startAnimation(shake);
            return;

        }

        uploadPost();

    }

    public void takePic(View view) {
        startPickImage(false);
    }

    public void selectImage(View view) {
        startPickImage(true);
    }

    public void showDatePicker(View view) {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setOkColor("#7C4DFF");
        dpd.setCancelColor("#7C4DFF");
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setThemeDark(false);
        dpd.show(getFragmentManager(), "IncidentDate");

    }

    public String month(int i){
        String month="";
        switch (i){
            case 1:
                month="Jan";
                return month;

            case 2:
                month="Feb";
                return month;

            case 3:
                month="March";
                return month;

            case 4:
                month="April";
                return month;

            case 5:
                month="May";
                return month;

            case 6:
                month="June";
                return month;

            case 7:
                month="July";
                return month;

            case 8:
                month="Aug";
                return month;

            case 9:
                month="Sept";
                return month;

            case 10:
                month="Oct";
                return month;

            case 11:
                month="Nov";
                return month;

            case 12:
                month="Dec";
                return month;
        }
        return month;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String mDate = dayOfMonth + " " + month(++monthOfYear) + ", " + year;
        date.setText(mDate);
    }
}
