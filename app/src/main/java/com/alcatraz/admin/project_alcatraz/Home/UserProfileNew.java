package com.alcatraz.admin.project_alcatraz.Home;

/**
 * Created by TAIYAB on 10-04-2018.
 */


    import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
    import android.app.WallpaperManager;
    import android.content.Context;
    import android.content.Intent;
import android.content.res.Resources;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.Canvas;
    import android.graphics.Matrix;
    import android.graphics.Paint;
    import android.graphics.Point;
    import android.graphics.PorterDuff;
    import android.graphics.PorterDuffXfermode;
    import android.graphics.Rect;
    import android.graphics.RectF;
    import android.graphics.drawable.BitmapDrawable;
    import android.graphics.drawable.Drawable;
    import android.os.Build;
    import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
    import android.support.v4.content.ContextCompat;
    import android.support.v4.view.GestureDetectorCompat;
    import android.support.v4.view.MotionEventCompat;
    import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.OverScroller;
    import android.widget.RelativeLayout;
    import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.Profile.UserProfileActivity;
import com.alcatraz.admin.project_alcatraz.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.Scanner;

    import javax.security.auth.login.LoginException;

public class UserProfileNew  extends AppCompatActivity {
    private static final String TAG = "UserProfileNew";
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile1);
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        //final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        Drawable imagetoshow = ContextCompat.getDrawable(getApplicationContext(),R.drawable.flier);
        final Drawable mod= new BitmapDrawable(getResources(),getRoundedCornerBitmap(((BitmapDrawable)imagetoshow).getBitmap(),getApplicationContext()));
        ImageView imageView=findViewById(R.id.imgg);
        /*final Matrix matrix = imageView.getImageMatrix();
        final float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final float scaleRatio = screenWidth / imageWidth;
        matrix.postScale(scaleRatio, scaleRatio);
        imageView.setImageMatrix(matrix);*/
        View v1=findViewById(R.id.th);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          //  v.setBackground(wallpaperDrawable);
            imageView.setImageResource(R.drawable.flier);
           // v1.setBackground(wallpaperDrawable);
        }


        listen(getApplicationContext());


    }
    protected void listen(Context c)
    {
        View v=findViewById(R.id.upper);

        final GestureDetector gestureDetector=new GestureDetector(c, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {

                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float x, float y) {
                Log.e("COOL",x+","+y);

                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });
        //Touch Listener
        /*View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // pass the events to the gesture detector
                // a return value of true means the detector is handling it
                // a return value of false means the detector didn't
                // recognize the event
               // return gestureDetector.onTouchEvent(event);
                return false;

            }
        };*/
        //Touch off
        //v.setOnTouchListener(touchListener);
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
                Log.d(TAG,"Action was MOVE");

                    RelativeLayout v=(RelativeLayout)findViewById(R.id.top);
                RelativeLayout v1=(RelativeLayout)findViewById(R.id.botomm);

                LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)v.getLayoutParams();
                LinearLayout.LayoutParams p1=(LinearLayout.LayoutParams)v1.getLayoutParams();
                Log.e(TAG, "onTouchEvent: "+p.weight+" ~~ "+p1.weight );


                if(p.weight+(event.getY() - y)*2*100.0/screen>70) {
                    p.weight = (int) 70;
                    p1.weight = (int) 35;
                }
                else
            {
                p.weight += (event.getY() - y)*2*100.0/screen;
                p1.weight -= (event.getY() - y)*2*100.0/screen;
            }


                if(p.weight<dpToPx(20)*100.0/screen)p.weight=dpToPx(20)*100f/screen;
                findViewById(R.id.lin).setAlpha(p.weight-35<=0?0:(p.weight-35)/35);

                v.setLayoutParams(p);
                    v1.setLayoutParams(p1);
                    x=event.getX();
                    y=event.getY();

                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(TAG,"Action was UP");
                x=0;y=0;
                down=false;
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
        maxy=screen*.7f;
        miny=screen*.35f;
    }
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    int screen;
    boolean state_up=true;
    boolean down=false;
    float x=0,y=0,maxy=0,miny=0;
}
