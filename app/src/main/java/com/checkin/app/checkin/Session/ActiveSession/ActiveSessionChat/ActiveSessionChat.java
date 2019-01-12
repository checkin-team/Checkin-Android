package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionChat extends AppCompatActivity {
    @BindView(R.id.bottom_expand_menu)
    RelativeLayout bottom_expand_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session_chat);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.im_expand_bottom_menu)
    public void onExpandMenu() {
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        bottom_expand_menu.startAnimation(bottomUp);
        bottom_expand_menu.setVisibility(View.VISIBLE);

    }
    @OnClick(R.id.im_collapse_bottom_menu)
    public void onCollapseMenu() {
        bottom_expand_menu.setVisibility(View.GONE);
        Animation upDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        bottom_expand_menu.startAnimation(upDown);
    }
}
