package com.checkin.app.checkin.Misc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.checkin.app.checkin.R;
import com.lyft.android.scissors.CropView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SelectCropImageActivity extends AppCompatActivity {
    private static final String TAG = SelectCropImageActivity.class.getSimpleName();
    public static final int RC_CROP_IMAGE = 1000;
    private static final int RC_PICK_IMAGE = 100;
    public static final String RECTANGLE_CROP_ASPECT_RATIO = "aspect_ratio";
    private File mRectangleFile;

    public static final String KEY_IMAGE = "select_crop_image";
    CropView cropView;
    private int maxHeight=600, maxWidth=600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_crop_image);

        requestImage();

        float viewportRatio = getIntent().getFloatExtra(RECTANGLE_CROP_ASPECT_RATIO, 0.81f);

        mRectangleFile = new File(getCacheDir(), "profile.png");

        cropView = findViewById(R.id.crop_view);
        cropView.setViewportRatio(viewportRatio);

        findViewById(R.id.next_button).setOnClickListener(v -> {
            Bitmap bitmap = cropView.crop();
            if (bitmap == null) {
                Log.e(TAG, "Cropped bitmap is null!");
                return;
            }
//            if (bitmap.getHeight() > maxHeight || bitmap.getWidth() > maxWidth) {
//                Log.e(TAG,+bitmap.getHeight()+" "+bitmap.getWidth()+ " ");
//                Toast.makeText(getApplicationContext(),"Image should be less than "+maxWidth+"*"+maxHeight,Toast.LENGTH_LONG).show();
//                requestImage();
//            }
            try (FileOutputStream out = new FileOutputStream(mRectangleFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setResult(Activity.RESULT_OK, getImageIntent());
            finish();
        });
    }

    private void requestImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PICK_IMAGE);
    }

    private Intent getImageIntent() {
        Intent intent =new Intent();
        intent.putExtra(KEY_IMAGE, mRectangleFile);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            finish();
            return;
        }
        if (requestCode == RC_PICK_IMAGE) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                cropView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
