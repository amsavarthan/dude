package com.amsavarthan.dude.OCR;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.dude.activities.ResultActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class FirebaseOCR {

    Context context;
    Activity activity;
    ProgressDialog mDialog;

    public FirebaseOCR(Activity activity, Context context, ProgressDialog mDialog) {
        this.activity=activity;
        this.context=context;
        this.mDialog=mDialog;
    }

    public void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.Block> blocks = texts.getBlocks();
        if (blocks.size() == 0) {
            activity.finish();
            ResultActivity.startActivity(context,"","image");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    sb.append(elements.get(k).getText()).append(" ");
                    Log.i(String.valueOf(k),elements.get(k).getText());
                }
            }
        }
        activity.finish();
        ResultActivity.startActivity(context,sb.toString(),"image");

    }

    public void runTextRecognition(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                .getVisionTextDetector();
        detector.detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                mDialog.dismiss();
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDialog.dismiss();
                                Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        });
    }

}
