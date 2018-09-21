package com.checkin.app.checkin.User;

/**
 * Created by TAIYAB on 10-04-2018.
 */


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.SelectCropImageActivity;
import com.transitionseverywhere.AutoTransition;
import com.transitionseverywhere.TransitionManager;

import java.io.File;

public class UserProfileActivity extends AppCompatActivity {
    private float weightTop=70;
    private float minweightTop=40;
    private float weightBottom=35;
    private  float minChangePercent=2f;
    String state="down";
    String mode="auto";
    private static final String TAG = "UserProfileActivity";


    private UserViewModel mViewModel;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int screenHeight=getWindow().getAttributes().height;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        //final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
//        Drawable imagetoshow = ContextCompat.getDrawable(getApplicationContext(),R.drawable.flier);
//        final Drawable mod= new BitmapDrawable(getResources(),getRoundedCornerBitmap(((BitmapDrawable)imagetoshow).getBitmap(),getApplicationContext()));
//        ImageView imageView=findViewById(R.id.imgg);
        /*RelativeLayout v=findViewById(R.id.botomm);
        LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)v.getLayoutParams();
        p.weight+=(dpToPx(20)/screenHeight);
        weightBottom=p.weight;*/
        Log.e(TAG, "onCreate: "+weightBottom );
        Drawable imagetoshow = ContextCompat.getDrawable(getApplicationContext(),R.drawable.flier);
        final Drawable mod= new BitmapDrawable(getResources(),getRoundedCornerBitmap(((BitmapDrawable)imagetoshow).getBitmap(),getApplicationContext()));
        ImageView imageView=findViewById(R.id.imgg);
        Button editImage=findViewById(R.id.editImage);
        editImage.setOnClickListener(v -> {
            Intent intent;
                intent = new Intent(this , SelectCropImageActivity.class);
            startActivityForResult(intent,SelectCropImageActivity.KEY_CROP_IMAGE_REQUEST_CODE);
        });
        /*final Matrix matrix = imageView.getImageMatrix();
        final float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final float scaleRatio = screenWidth / imageWidth;
        matrix.postScale(scaleRatio, scaleRatio);
        imageView.setImageMatrix(matrix);*/
        //  v.setBackground(wallpaperDrawable);
        //imageView.setImageResource(R.drawable.flier);
        // v1.setBackground(wallpaperDrawable);

        mViewModel= ViewModelProviders.of(this).get(UserViewModel.class);

        mViewModel.getAllUsers().observe(this, userModel -> {
            if (userModel == null) return;
            if (userModel.status == Resource.Status.SUCCESS) {
                if (userModel.data != null) {
                    UserModel person = userModel.data.get(0);
                    UserProfileActivity.this.setUI(person);
                }
            } else if (userModel.status == Resource.Status.LOADING) {
                //rv.setAdapter(new ReviewsAdapter(reviewsModel.data));
                // LOADING
            } else {
                Toast.makeText(UserProfileActivity.this, "Error In getting Reviews", Toast.LENGTH_SHORT).show();
            }

        });

    }
    @SuppressLint("SetTextI18n")
    public  void setUI(UserModel person){
        ImageView imageView=findViewById(R.id.imgg);
        Glide.with(UserProfileActivity.this).load(person.getProfilePic()).into(imageView);
        TextView nfollowers=findViewById(R.id.nfollowers),ncheckins=findViewById(R.id.ncheckins);
        nfollowers.setText(person.formatFollowers());ncheckins.setText(person.formatCheckins());
        TextView bio=findViewById(R.id.bio),username=findViewById(R.id.username),city=findViewById(R.id.city_user),profession=findViewById(R.id.profession);
        bio.setText(person.getBio());
        username.setText(person.getUsername());
        city.setText(person.getLocation());
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, Context context) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        float density = context.getResources().getDisplayMetrics().density;
        float roundPx = 20 * density;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawRect(rectF,paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){


        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(TAG,"Action was DOWN");
                x=event.getX();
                y=event.getY();
                Log.d(TAG, x+","+y);
                down=true;
                return true;
            case (MotionEvent.ACTION_MOVE) :
                mode="auto";
                Log.d(TAG,"Action was MOVE");

                    RelativeLayout v=(RelativeLayout)findViewById(R.id.top);
                RelativeLayout v1=(RelativeLayout)findViewById(R.id.botomm);

                LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)v.getLayoutParams();
                LinearLayout.LayoutParams p1=(LinearLayout.LayoutParams)v1.getLayoutParams();
                Log.e(TAG, "onTouchEvent: "+p.weight+" ~~ "+p1.weight );


                if(p.weight+(event.getY() - y)*100.0/screen>weightTop) {
                    p.weight = weightTop;
                    p1.weight = weightBottom;
                    mode="manual";
                    state="down";
                }
                else
            {
                p.weight += (event.getY() - y)*100.0/screen;
                p1.weight -= (event.getY() - y)*100.0/screen;
            }

                float con=(float)dpToPx(20)*100.0f/screen+minweightTop;
                if(p.weight<dpToPx(20)*100.0/screen+minweightTop){
                    p.weight=dpToPx(20)*100f/screen+minweightTop;
                    state="up";
                    mode="manual";
                }
                findViewById(R.id.third).setAlpha(p.weight-con<=0?0:(p.weight-con)/(weightTop-con));

                v.setLayoutParams(p);
                    v1.setLayoutParams(p1);
                    x=event.getX();
                    y=event.getY();

                return true;
            case (MotionEvent.ACTION_UP) :
                con=(float)dpToPx(20)*100.0f/screen+minweightTop;
                v=(RelativeLayout)findViewById(R.id.top);
                v1=(RelativeLayout)findViewById(R.id.botomm);
                p=(LinearLayout.LayoutParams)v.getLayoutParams();
                p1=(LinearLayout.LayoutParams)v1.getLayoutParams();
                Log.d(TAG,"Action was UP");
                x=0;y=0;
                down=false;
                if(mode.equals("manual"))
                    return true;
                TransitionManager.beginDelayedTransition((ViewGroup)findViewById(R.id.upper),new AutoTransition());

                if(state.equals("up"))
                {
                    float changePercent=100f*(p.weight-minweightTop)/(weightTop-minweightTop);
                    if(changePercent>minChangePercent){
                    state="down";
                    p.weight = weightTop;
                    p1.weight = weightBottom;
                    }
                    else{
                        state="up";
                        p.weight=dpToPx(20)*100f/screen+minweightTop;
                        p1.weight=weightBottom+weightTop-p.weight;
                    }

                }
                else{
                    float changePercent=100f*(weightTop-p.weight)/(weightTop-minweightTop);
                    if(changePercent>minChangePercent) {
                        state = "up";
                        p.weight = dpToPx(20) * 100f / screen + minweightTop;
                        p1.weight = weightBottom + weightTop - p.weight;
                    }
                    else {
                        state="down";
                        p.weight = weightTop;
                        p1.weight = weightBottom;
                    }
                }
                findViewById(R.id.third).setAlpha(p.weight-con<=0?0:(p.weight-con)/(weightTop-con));
                v.setLayoutParams(p);
                v1.setLayoutParams(p1);
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(TAG,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        // the height will be set at this point
        screen = findViewById(R.id.linear).getHeight();
        Log.e(TAG, "onWindowFocusChanged: "+screen );
        maxy=screen*weightTop*.01f;
        miny=screen*weightBottom*.01f;
    }
    public void follow(View v){
        if(following) {
            //start message
            return;
        }
        following=true;
        TransitionManager.beginDelayedTransition((ViewGroup)findViewById(R.id.sec));
        TextView tv=findViewById(R.id.follow_button);
        tv.setText("MESSAGE");
        tv.setTranslationX(-dpToPx(40)*1f);
        findViewById(R.id.following).setVisibility(View.VISIBLE);

    }
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public void goback(View v){
        onBackPressed();
    }
    int screen;
    boolean state_up=true;
    boolean down=false,following=false;
    float x=0,y=0,maxy=0,miny=0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SelectCropImageActivity.KEY_CROP_IMAGE_REQUEST_CODE&&resultCode==RESULT_OK)
        {
            /*byte byteArrayRectangle[]=data.getByteArrayExtra(SelectCropImageActivity.KEY_RECTANGLE_IMAGE);
            byte byteArrayCircle[]=data.getByteArrayExtra(SelectCropImageActivity.KEY_CIRCLE_IMAGE);*/
            File rectangleFile = data.getParcelableExtra(SelectCropImageActivity.KEY_RECTANGLE_IMAGE);
            mViewModel.postImages(rectangleFile);
        }
    }
}
