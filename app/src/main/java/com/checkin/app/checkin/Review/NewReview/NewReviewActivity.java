package com.checkin.app.checkin.Review.NewReview;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Misc.SelectCropImageActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.ActiveSessionChatAdapter;
import com.checkin.app.checkin.Utility.Utils;

import java.io.File;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewReviewActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, ReviewImageAdapter.ImageInteraction/*, DialogInterface.OnClickListener*/ {
    private static final String TAG = NewReviewActivity.class.getSimpleName();

    public static final String KEY_SESSION_PK = "review.session_pk";

    @BindView(R.id.et_experience)
    EditText etExperience;
    @BindView(R.id.seekbar_food_quality)
    SeekBar seekbar_food_quality;
    @BindView(R.id.seekbar_ambience)
    SeekBar seekbar_ambience;
    @BindView(R.id.seekbar_service)
    SeekBar seekbar_service;
    @BindView(R.id.rv_add_images)
    RecyclerView rvAddImages;
    private NewReviewViewModel mViewModel;
    int mFoodQualityRating, mAmbienceRating, mServiceRating;
    final CharSequence[] imageUseCase = {"Ambiance", "Food"};

    private ReviewImageAdapter mImageAdapter;
    ReviewImageShowModel imageShowModel;


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
        seekbar_food_quality.incrementProgressBy(0);
        seekbar_food_quality.setMax(5);
        seekbar_ambience.setProgress(0);
        seekbar_ambience.incrementProgressBy(0);
        seekbar_ambience.setMax(5);
        seekbar_service.setProgress(0);
        seekbar_service.incrementProgressBy(0);
        seekbar_service.setMax(5);

        mViewModel = ViewModelProviders.of(this).get(NewReviewViewModel.class);
        seekbar_food_quality.setOnSeekBarChangeListener(this);
        seekbar_ambience.setOnSeekBarChangeListener(this);
        seekbar_service.setOnSeekBarChangeListener(this);

        rvAddImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mImageAdapter = new ReviewImageAdapter(null, this);
        rvAddImages.setAdapter(mImageAdapter);

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

        mViewModel.getImageData().observe(this, genericDetailModelResource -> {
            if (genericDetailModelResource == null)
                return;
            switch (genericDetailModelResource.status) {
                case SUCCESS: {
                    Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG,String.valueOf(genericDetailModelResource.data.getIdentifier()));
                    genericDetailModelResource.data.setPk(String.valueOf(genericDetailModelResource.data.getIdentifier()));
                    mImageAdapter.setData(Collections.singletonList(genericDetailModelResource.data));
//                    addImages(genericDetailModelResource.data.getImage(),llAddImages);
                    break;
                }
                case LOADING:
//                    mImageAdapter.setData(Collections.singletonList(genericDetailModelResource.data));
                    break;
                default: {
                    Utils.toast(this, genericDetailModelResource.message);
                }
            }
        });
    }

    @OnClick(R.id.btn_done)
    public void onDone() {
        mViewModel.updateBody(etExperience.getText().toString());
        mViewModel.updateRating(mFoodQualityRating, mAmbienceRating, mServiceRating);
        mViewModel.submitReview();
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
                openDialog(image);
            }
        }
    }

    private void openDialog(File image){
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this)
                .setTitle("Choose a Image Use Case")
                .setSingleChoiceItems(imageUseCase, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (imageUseCase[which] == "Ambience") mViewModel.uploadReviewImage(image,ReviewImageModel.REVIEW_IMAGE_USE_CASE.AMBIENCE,0);
                        else  mViewModel.uploadReviewImage(image,ReviewImageModel.REVIEW_IMAGE_USE_CASE.FOOD,0);


                        dialog.dismiss();
                    }
                });
        AlertDialog alertdialog2 = builder2.create();
        alertdialog2.show();

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
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

    }

    @Override
    public void onDeleteImage(GenericDetailModel orderedItem) {
        mViewModel.deleteReviewImage(orderedItem);

    }

    /*@Override
    public void onClick(DialogInterface dialog, int which) {

    }*/
}
