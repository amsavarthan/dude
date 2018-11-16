package com.amsavarthan.dude.fragment;

import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.OCR.FirebaseOCR;
import com.amsavarthan.dude.R;
import com.amsavarthan.dude.OCR.TessOCR;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Grid;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CameraScan extends Fragment implements CropImageView.OnGetCroppedImageCompleteListener, CropImageView.OnSetImageUriCompleteListener {

    private static final String TAG = CameraScan.class.getSimpleName();
    private CameraView cameraView;
    private FloatingActionButton capture,grid,cancel,process,rotate;
    private String grid_type="on";
    private CropImageView imageView;
    private ProgressDialog mDialog;
    private String ocr_type;
    private TessOCR tessOCR;
    private FirebaseOCR firebaseOCR;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_camera_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.capture_layout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.crop_view).setVisibility(View.GONE);

        cameraView=view.findViewById(R.id.camera);
        capture=view.findViewById(R.id.capture);
        grid=view.findViewById(R.id.show_grid);
        imageView=view.findViewById(R.id.image);
        cancel=view.findViewById(R.id.cancel);
        rotate=view.findViewById(R.id.rotate);
        process=view.findViewById(R.id.process);

        mDialog=new ProgressDialog(view.getContext());
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("Please wait...");

        ocr_type=getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).getString("ocr_type","firebaseOCR");

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onCameraOpened(CameraOptions options) {
                super.onCameraOpened(options);

                capture.show();
                grid.show();

                view.findViewById(R.id.title).setVisibility(View.VISIBLE);
                view.findViewById(R.id.title).setAlpha(0.0f);
                view.findViewById(R.id.title).animate().alpha(1.0f).setDuration(500).start();

            }

            @Override
            public void onCameraError(@NonNull CameraException exception) {
                super.onCameraError(exception);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Camera Error")
                        .setMessage(exception.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);

                CameraUtils.decodeBitmap(jpeg, new CameraUtils.BitmapCallback() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {

                        mDialog.dismiss();
                        view.findViewById(R.id.capture_layout).setVisibility(View.GONE);
                        view.findViewById(R.id.crop_view).setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(bitmap);
                    }
                });

            }
        });

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.getCroppedImageAsync();
                mDialog.show();
            }
        });


        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.rotateImage(90);
            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               cameraView.capturePicture();
               mDialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.clearImage();
                view.findViewById(R.id.capture_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.crop_view).setVisibility(View.GONE);
            }
        });

        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (grid_type){

                    case "off":
                        cameraView.setGrid(Grid.OFF);
                        grid.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.grid_on));
                        grid_type="on";
                        return;
                    case "on":
                        cameraView.setGrid(Grid.DRAW_3X3);
                        grid.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.grid_off));
                        grid_type="off";


                }
            }
        });

        AssetManager assetManager = getActivity().getAssets();
        tessOCR = new TessOCR(assetManager);
        firebaseOCR=new FirebaseOCR(getActivity(),getContext(),mDialog);

    }


    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        imageView.setOnSetImageUriCompleteListener(this);
        imageView.setOnGetCroppedImageCompleteListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        imageView.setOnSetImageUriCompleteListener(null);
        imageView.setOnGetCroppedImageCompleteListener(null);
    }

    @Override
    public void onSetImageUriComplete(CropImageView cropImageView, Uri uri, Exception error) {
        mDialog.dismiss();
        if (error != null) {
            Log.e("Crop", "Failed to load image for cropping", error);
            Toast.makeText(getContext(), "Something went wrong, try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetCroppedImageComplete(CropImageView view, final Bitmap bitmap, Exception error) {
        if (error == null) {
            if (bitmap != null) {

                new MaterialDialog.Builder(getContext())
                        .title("Scan using my...")
                        .items(R.array.ocr)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                if (text.equals("Right Eye")){
                                    Toast.makeText(getContext(), "Ok will use my right eye.", Toast.LENGTH_SHORT).show();
                                firebaseOCR.runTextRecognition(bitmap);
                                }else{
                                    Toast.makeText(getContext(), "Ok will use my left eye.", Toast.LENGTH_SHORT).show();
                                    tessOCR.runOcr(bitmap,getActivity(),getContext(),mDialog);
                                }

                                return false;
                            }
                        }).positiveText("Process").show();


            }
        } else {
            mDialog.dismiss();
            Log.e("Crop", "Failed to crop image", error);
            Toast.makeText(getContext(), "Something went wrong, try again", Toast.LENGTH_LONG).show();
        }
    }



}
