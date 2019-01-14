package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionViewModel;
import com.checkin.app.checkin.Utility.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionChat extends AppCompatActivity {
    @BindView(R.id.bottom_expand_menu) RelativeLayout bottom_expand_menu;
    @BindView(R.id.rv_active_session_chat) RecyclerView rv_active_session_chat;
    @BindView(R.id.et_msg) EditText et_msg;

    private ActiveSessionChatAdapter mChatAdapter;
    private ActiveSessionViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session_chat);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        rv_active_session_chat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mChatAdapter = new ActiveSessionChatAdapter(null, ActiveSessionChat.this);
        rv_active_session_chat.setAdapter(mChatAdapter);

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);
        mViewModel.getSessionChat().observe(this, new Observer<Resource<List<ActiveSessionChatModel>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ActiveSessionChatModel>> activeSessionChatModelResource) {
                if (activeSessionChatModelResource != null && activeSessionChatModelResource.status == Resource.Status.SUCCESS)
                    mChatAdapter.setData(activeSessionChatModelResource.data);
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                    et_msg.setHint(getResources().getString(R.string.title_how_can_we_assist_you));
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Util.toast(this, resource.message);
                }
            }
        });
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

    @OnClick(R.id.btn_send_msg)
    public void sendMsg() {
        if (!TextUtils.isEmpty(et_msg.getText().toString().trim())) {
            mViewModel.sendMessage(et_msg.getText().toString().trim());
        }else {
            Toast.makeText(this, "Please enter the message.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.im_chat_side_menu)
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_delay:
                    return (true);
                case R.id.menu_quality:
                    return (true);
                default:
                    return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_active_session_chat, popup.getMenu());
        popup.show();
    }
}
