package com.amsavarthan.dude.OCR;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.amsavarthan.dude.activities.ResultActivity;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.amsavarthan.dude.utils.Utils.DATA_PATH;
import static com.amsavarthan.dude.utils.Utils.tess_lang;

public class TessOCR {

    private static final String TAG = "TESSERACT";
    private AssetManager assetManager;
    private TessBaseAPI mTess;

    public TessOCR(AssetManager assetManager) {

        Log.i(TAG, DATA_PATH);

        this.assetManager = assetManager;

        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }
        }

        if (!(new File(DATA_PATH + "tessdata/" + tess_lang + ".traineddata")).exists()) {
            try {
                InputStream in = assetManager.open("tessdata/" + tess_lang + ".traineddata");
                OutputStream out = new FileOutputStream(new File(DATA_PATH + "tessdata/", tess_lang + ".traineddata"));

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                Log.v(TAG, "Copied " + tess_lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + tess_lang + " traineddata " + e.toString());
            }
        }

        mTess = new TessBaseAPI();
        mTess.setDebug(true);
        mTess.init(DATA_PATH, tess_lang);

    }


    public void runOcr(Bitmap bitmap, Activity activity, Context context, ProgressDialog mDialog)
    {
        mDialog.dismiss();
        mTess.setImage(bitmap);
        if(StringUtils.isEmpty(mTess.getUTF8Text())) {
            activity.finish();
            ResultActivity.startActivity(context, "", "image");
            if (mTess != null)
                mTess.end();
        }else{
            activity.finish();
            ResultActivity.startActivity(context,mTess.getUTF8Text(),"image");
            if (mTess != null)
                mTess.end();
        }

    }

}