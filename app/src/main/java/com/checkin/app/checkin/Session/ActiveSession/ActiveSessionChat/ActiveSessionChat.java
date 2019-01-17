package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat.ActiveSessionChat.CHATCONCERNYPES.CONCERN_DELAY;
import static com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat.ActiveSessionChat.CHATCONCERNYPES.CONCERN_QUALITY;

public class ActiveSessionChat extends AppCompatActivity implements ActiveSessionChatAdapter.ChatInteraction {
    @BindView(R.id.bottom_expand_menu)
    RelativeLayout bottom_expand_menu;
    @BindView(R.id.rv_active_session_chat)
    RecyclerView rv_active_session_chat;
    @BindView(R.id.et_msg)
    EditText et_msg;
    @BindView(R.id.btn_send_msg)
    ImageView btn_send_msg;
    @BindView(R.id.im_chat_side_menu)
    ImageView im_chat_side_menu;
    @BindView(R.id.ll_call_waiter_container)
    LinearLayout ll_call_waiter_container;
    @BindView(R.id.ll_table_cleaning_container)
    LinearLayout ll_table_cleaning_container;
    @BindView(R.id.ll_refill_glass)
    LinearLayout ll_refill_glass;
    @BindView(R.id.swipe_down)
    SwipeRefreshLayout swipe_down;
    int concerns = 0, event_id = 0;

    public enum CHATCONCERNYPES {
        CONCERN_NONE(0), CONCERN_DELAY(1), CONCERN_QUALITY(2);

        final int tag;

        CHATCONCERNYPES(int tag) {
            this.tag = tag;
        }

        public static CHATCONCERNYPES getByTag(int tag) {
            switch (tag) {
                case 0:
                    return CONCERN_NONE;
                case 1:
                    return CONCERN_DELAY;
                case 2:
                    return CONCERN_QUALITY;
            }
            return CONCERN_NONE;
        }
    }

    private ActiveSessionChatAdapter mChatAdapter;
    private ActiveSessionChatViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session_chat);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        rv_active_session_chat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        mChatAdapter = new ActiveSessionChatAdapter(null, ActiveSessionChat.this, this);
        rv_active_session_chat.setAdapter(mChatAdapter);

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionChatViewModel.class);
        mViewModel.getSessionChat().observe(this, new Observer<Resource<List<ActiveSessionChatModel>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ActiveSessionChatModel>> activeSessionChatModelResource) {
                if (activeSessionChatModelResource != null && activeSessionChatModelResource.status == Resource.Status.SUCCESS){
                    mChatAdapter.setData(activeSessionChatModelResource.data);
                }
            }
        });

        swipe_down.setOnRefreshListener(() -> {
            mViewModel.updateResults();
            swipe_down.setRefreshing(false);
        });

        if (getIntent().getSerializableExtra("service").equals(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_CALL_WAITER))
            ll_call_waiter_container.performClick();
        else if (getIntent().getSerializableExtra("service").equals(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_CLEAN_TABLE))
            ll_table_cleaning_container.performClick();
        else if (getIntent().getSerializableExtra("service").equals(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_BRING_COMMODITY))
            ll_refill_glass.performClick();

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    Toast.makeText(this, "Sent!", Toast.LENGTH_SHORT).show();
                    et_msg.setText("");
                    et_msg.setHint(getResources().getString(R.string.title_how_can_we_assist_you));
                    btn_send_msg.setTag(null);
                    mViewModel.updateResults();
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

    public void setMessage(String msg) {
        et_msg.setText(msg);
    }

    @OnClick(R.id.ll_call_waiter_container)
    public void clickCallWaiter() {
        et_msg.setText(getResources().getString(R.string.msg_call_waiter));
        btn_send_msg.setTag(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_CALL_WAITER);
    }

    @OnClick(R.id.ll_table_cleaning_container)
    public void clickTableClean() {
        et_msg.setText(getResources().getString(R.string.msg_clean_table));
        btn_send_msg.setTag(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_CLEAN_TABLE);
    }

    @OnClick(R.id.ll_refill_glass)
    public void clickRefillGlass() {
        et_msg.setText(getResources().getString(R.string.msg_refill_glass));
        btn_send_msg.setTag(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_BRING_COMMODITY);
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
            if (concerns != 0) {
                mViewModel.raiseConcern(concerns, et_msg.getText().toString().trim(), event_id);
            } else {
                if (btn_send_msg.getTag() == null) {
                    btn_send_msg.setTag(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_NONE);
                    Log.e("tag====1==", String.valueOf(btn_send_msg.getTag()));
                }
                Log.e("tag====12==", String.valueOf(btn_send_msg.getTag()));
                mViewModel.sendMessage(et_msg.getText().toString().trim(), btn_send_msg.getTag());

            }

        } else {
            Toast.makeText(this, "Please enter the message.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.ll_napkin_container)
    public void clickNapkin() {
        et_msg.setText("Napkins required");
        btn_send_msg.setTag(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_BRING_COMMODITY);
    }

    @OnClick(R.id.ll_extra_plates_container)
    public void clickExtraPlates() {
        et_msg.setText("Bring extra plates");
        btn_send_msg.setTag(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_BRING_COMMODITY);
    }

    @OnClick(R.id.ll_salt_container)
    public void clickSalt() {
        et_msg.setText("Bring salt");
        btn_send_msg.setTag(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_BRING_COMMODITY);
    }

    @OnClick(R.id.ll_sauce_container)
    public void clickSauce() {
        et_msg.setText("Bring chili sauce");
        btn_send_msg.setTag(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_BRING_COMMODITY);
    }

    @OnClick(R.id.parent_layout)
    public void onParent(){
        im_chat_side_menu.setVisibility(View.GONE);
    }

    @OnClick(R.id.im_chat_side_menu)
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_delay:
                    concerns = CONCERN_DELAY.tag;
                    return (true);
                case R.id.menu_quality:
                    concerns = CONCERN_QUALITY.tag;
                    return (true);
                default:
                    return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_active_session_chat, popup.getMenu());
        popup.show();
    }


    @Override
    public void onLongPress(ActiveSessionChatModel.CHATEVENTTYPE event) {
        im_chat_side_menu.setVisibility(View.VISIBLE);
        im_chat_side_menu.performClick();
        event_id = event.tag;
    }

    @OnClick(R.id.im_back)
    public void goBack(View v){
        onBackPressed();
    }
}
