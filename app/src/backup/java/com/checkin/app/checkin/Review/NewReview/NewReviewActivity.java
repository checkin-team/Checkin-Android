package com.checkin.app.checkin.Review.NewReview;

import android.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.materialrangebar.RangeBar;
import com.checkin.app.checkin.Misc.SelectCropImageActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewReviewActivity extends AppCompatActivity implements RangeBar.OnRangeBarChangeListener, ReviewImageAdapter.ImageInteraction {
    private static final String TAG = NewReviewActivity.class.getSimpleName();

    public static final String KEY_SESSION_PK = "review.session_pk";
    public static final String KEY_RESTAURANT_PK = "review.restaurant_pk";

    @BindView(R.id.im_ar_restaurant_logo)
    ImageView imRestaurantLogo;
    @BindView(R.id.tv_ar_restaurant_name)
    TextView tvRestaurantName;
    @BindView(R.id.btn_ar_review_ratings)
    Button btnReviewRatings;
    @BindView(R.id.btn_ar_user_follow)
    Button btnUserFollow;
    @BindView(R.id.tv_ar_restaurant_locality)
    TextView tvRestaurantReviewsFollowers;
    @BindView(R.id.et_ar_user_experience)
    EditText etExperience;
    @BindView(R.id.seekbar_review_food)
    RangeBar seekbarFoodQuality;
    @BindView(R.id.seekbar_review_ambiance)
    RangeBar seekbarAmbience;
    @BindView(R.id.seekbar_review_hospitality)
    RangeBar seekbarService;
    @BindView(R.id.rv_add_images)
    RecyclerView rvAddImages;

    private NewReviewViewModel mViewModel;

    private int mFoodQualityRating, mAmbianceRating, mServiceRating;
    private final CharSequence[] imageUseCase = {"Ambiance", "Food"};

    private ReviewImageAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_new);
        ButterKnife.bind(this);

        setupUi();
        getData();
    }

    private void setupUi() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }
        seekbarFoodQuality.setSeekPinByIndex(0);
        seekbarAmbience.setSeekPinByIndex(0);
        seekbarService.setSeekPinByIndex(0);

        seekbarFoodQuality.setOnRangeBarChangeListener(this);
        seekbarAmbience.setOnRangeBarChangeListener(this);
        seekbarService.setOnRangeBarChangeListener(this);

        rvAddImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mImageAdapter = new ReviewImageAdapter(this);
        rvAddImages.setAdapter(mImageAdapter);
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(this).get(NewReviewViewModel.class);

        mViewModel.getRestaurantBriefData(getIntent().getStringExtra(KEY_RESTAURANT_PK)).observe(this, restaurantBriefModelResource -> {
            if (restaurantBriefModelResource == null)
                return;
            switch (restaurantBriefModelResource.status) {
                case SUCCESS: {
                    if (restaurantBriefModelResource.data != null) {
                        setData(restaurantBriefModelResource.data);
                    }
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(this, restaurantBriefModelResource.message);
                }
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

        mViewModel.getImageData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    if (resource.data != null) {
                        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                        mImageAdapter.updateData(resource.data.getIdentifier(), Long.valueOf(resource.data.getPk()));
                    }
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

    private void setData(RestaurantBriefModel data){
        Utils.loadImageOrDefault(imRestaurantLogo, data.getLogo(), R.drawable.ic_waiter);
        tvRestaurantName.setText(data.getName());
        tvRestaurantReviewsFollowers.setText(data.getLocality());
        btnReviewRatings.setText(data.getRating());
    }

    @OnClick(R.id.btn_ar_done)
    public void onDone() {
        mViewModel.updateBody(etExperience.getText().toString());
        mViewModel.updateRating(mFoodQualityRating, mAmbianceRating, mServiceRating);
        if (!mViewModel.submitReview(getIntent().getStringExtra(KEY_SESSION_PK)))
            Utils.toast(this, "Please rate");
    }

    @OnClick(R.id.btn_ar_add_image)
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

    private void openDialog(final File image) {
        new AlertDialog.Builder(this)
            .setTitle("Choose a type of image")
            .setSingleChoiceItems(imageUseCase, -1, (dialog, which) -> {
                if (imageUseCase[which] == "Ambiance")
                    mViewModel.uploadReviewImage(image, ReviewImageModel.REVIEW_IMAGE_USE_CASE.AMBIENCE, mImageAdapter.getItemCount());
                else
                    mViewModel.uploadReviewImage(image, ReviewImageModel.REVIEW_IMAGE_USE_CASE.FOOD, mImageAdapter.getItemCount());
                mImageAdapter.addData(new ReviewImageShowModel(0, image));
                dialog.dismiss();
            })
            .show();
    }

    @Override
    public void onDeleteImage(ReviewImageShowModel orderedItem) {
        mViewModel.deleteReviewImage(orderedItem);
    }

    @Override
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
        switch (rangeBar.getId()) {
            case R.id.seekbar_review_food:
                mFoodQualityRating = rightPinIndex;
                break;
            case R.id.seekbar_review_ambiance:
                mAmbianceRating = rightPinIndex;
                break;
            case R.id.seekbar_review_hospitality:
                mServiceRating = rightPinIndex;
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
