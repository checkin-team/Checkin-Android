package com.checkin.app.checkin.Review.NewReview;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.checkin.app.checkin.Misc.SelectCropImageActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Review.ShopReview.ShopReviewViewModel;
import com.checkin.app.checkin.Utility.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewReviewActivity extends AppCompatActivity {
    private static final String TAG = NewReviewActivity.class.getSimpleName();

    public static final String KEY_SESSION_PK = "review.session_pk";

    @BindView(R.id.et_experience)
    EditText et_experience;
    @BindView(R.id.seekbar_food_quality)
    SeekBar seekbar_food_quality;
    @BindView(R.id.seekbar_ambience)
    SeekBar seekbar_ambience;
    @BindView(R.id.seekbar_service)
    SeekBar seekbar_service;
    @BindView(R.id.ll_add_images)
    LinearLayout ll_add_images;
    private ShopReviewViewModel mViewModel;
    int mFoodQualityRating, mAmbienceRating, mServiceRating;
    Dialog imageCaseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_new);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        seekbar_food_quality.setProgress(0);
        seekbar_food_quality.incrementProgressBy(1);
        seekbar_food_quality.setMax(5);
        seekbar_ambience.setProgress(0);
        seekbar_ambience.incrementProgressBy(1);
        seekbar_ambience.setMax(5);
        seekbar_service.setProgress(0);
        seekbar_service.incrementProgressBy(1);
        seekbar_service.setMax(5);

        mViewModel = ViewModelProviders.of(this).get(ShopReviewViewModel.class);
        seekbar_food_quality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFoodQualityRating = progress;
                Log.e("mFoodQualityRating", String.valueOf(mFoodQualityRating));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbar_ambience.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAmbienceRating = progress;
                Log.e("mFoodQualityRating", String.valueOf(mFoodQualityRating));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbar_service.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mServiceRating = progress;
                Log.e("mFoodQualityRating", String.valueOf(mFoodQualityRating));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(this, resource.message);
                }
            }
        });

    }

    @OnClick(R.id.btn_done)
    public void onDone() {
        Log.e("ratingfff", et_experience.getText().toString());
//        mViewModel.register(et_experience.getText().toString(), mFoodQualityRating, mAmbienceRating, mServiceRating);
    }

    @OnClick(R.id.btn_add_image)
    public void onAddImage() {
        Intent intent = new Intent(this, SelectCropImageActivity.class);
        startActivityForResult(intent, SelectCropImageActivity.RC_CROP_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectCropImageActivity.RC_CROP_IMAGE && resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
                File image = (File) data.getExtras().get(SelectCropImageActivity.KEY_IMAGE);
//                mViewModel.uploadReviewImage(image);
                fleetDialog();
//                ll_add_images.addView();
            }
        }
    }

    private String fleetDialog() {
        imageCaseDialog = new Dialog(this, R.style.AppDialog);
        imageCaseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageCaseDialog.setContentView(R.layout.dialog_review_image_case);
        imageCaseDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        imageCaseDialog.setCancelable(false);
        imageCaseDialog.show();
        ButterKnife.bind(this,imageCaseDialog);
        return null;
    }



   /* @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.seekbar_food_quality:
                mFoodQualityRating = progress;
                Log.e("mFoodQualityRating", String.valueOf(mFoodQualityRating));
                break;
            case R.id.seekbar_ambience:
                mAmbienceRating = progress;
                Log.e("mFoodQualityRating1", String.valueOf(mAmbienceRating));
                break;
            case R.id.seekbar_service:
                mServiceRating = progress;
                Log.e("mFoodQualityRating12", String.valueOf(mServiceRating));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }*/
}
