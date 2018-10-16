package com.checkin.app.checkin.Utility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.checkin.app.checkin.R;
import com.lyft.android.scissors2.CropView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SelectCropImageActivity extends AppCompatActivity {
    private static final String TAG = SelectCropImageActivity.class.getSimpleName();
    public static final int KEY_CROP_IMAGE_REQUEST_CODE = 1000;
    private static final int PICK_IMAGE_REQUEST = 100;
    public static final String RECTANGLE_CROP_ASPECT_RATIO = "aspect_ratio";
    private File mRectangleFile;

    public static final String KEY_RECTANGLE_IMAGE="rectangle";
    CropView cropView;
    private int maxHeight=600, maxWidth=600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_crop_image);

        requestImage();

        float viewportRatio = getIntent().getFloatExtra(RECTANGLE_CROP_ASPECT_RATIO, 0.81f);

        mRectangleFile = new File(getCacheDir(), "rectangle.png");

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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private Intent getImageIntent() {
        Intent intent =new Intent();
        intent.putExtra(KEY_RECTANGLE_IMAGE, mRectangleFile);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            Uri uri=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                cropView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
