package com.checkin.app.checkin.Utility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.checkin.app.checkin.R;
import com.lyft.android.scissors2.CropView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SelectCropImageActivity extends AppCompatActivity {
    public static final int KEY_CROP_IMAGE_REQUEST_CODE = 1000;
    private static final int PICK_IMAGE_REQUEST = 100;
    public static final String RECTANGLE_CROP_ASPECT_RATIO = "aspect_ratio";
    private File mRectangleFile;

    public static final String KEY_RECTANGLE_IMAGE="rectangle";

    CropView cropView;
    boolean flag=false;


    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_crop_image);

        requestImage();

        float viewportRatio = getIntent().getFloatExtra(RECTANGLE_CROP_ASPECT_RATIO, 0.81f);

        mRectangleFile = new File(getCacheDir(), "rectangle.png");

        cropView = findViewById(R.id.crop_view);
        cropView.setViewportRatio(viewportRatio);
        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cropView.extensions()
                        .crop()
                        .into(mRectangleFile);
                setResult(Activity.RESULT_OK, getImageIntent());
                finish();
            }
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
    private byte[] getByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream stream =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        return stream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST) {
            Uri uri=data.getData();
            try
            {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                cropView.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
