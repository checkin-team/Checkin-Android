package com.checkin.app.checkin.session.activesession.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.session.activesession.chat.SessionChatDataModel.EVENT_CONCERN_TYPE;
import com.checkin.app.checkin.session.activesession.chat.SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class SessionChatFragment extends BaseFragment implements ActiveSessionChatAdapter.SessionChatInteraction {
    public static final String KEY_SERVICE_TYPE = "session_chat.service";
    private static final String TAG = SessionChatFragment.class.getSimpleName();
    @BindView(R.id.bottom_expand_menu)
    ViewGroup bottomExpandedMenu;
    @BindView(R.id.container_session_trending_items_actions)
    ViewGroup containerSessionActions;
    @BindView(R.id.rv_active_session_chat)
    RecyclerView rvSessionChat;
    @BindView(R.id.et_chat_msg)
    EditText etMessage;
    @BindView(R.id.btn_chat_send_msg)
    ImageView btnSendMsg;
    @BindView(R.id.ll_call_waiter_button)
    View vCallWaiter;
    @BindView(R.id.ll_table_cleaning_button)
    View vCleanTable;
    @BindView(R.id.ll_refill_glass_button)
    View vRefillGlass;

    private ActiveSessionChatAdapter mChatAdapter;
    private ActiveSessionChatViewModel mViewModel;

    public SessionChatFragment() {
    }

    public static SessionChatFragment newInstance(int tag) {
        SessionChatFragment fragment = new SessionChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SessionChatFragment.KEY_SERVICE_TYPE, tag);
        fragment.setArguments(bundle);
        return fragment;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message = MessageUtils.parseMessage(intent);
            if (message == null) return;

            switch (message.getType()) {
                case USER_SESSION_EVENT_NEW:
                    SessionChatFragment.this.addEvent(message.getRawData().getSessionEventDetail());
                    break;
                case USER_SESSION_EVENT_UPDATE:
                    SessionChatFragment.this.updateEvent(message.getObject(), message.getRawData().getSessionEventStatus());
                    break;
                case WAITER_SESSION_UPDATE_ORDER:
                    SessionChatFragment.this.updateEvent(message.getObject(), message.getRawData().getSessionEventStatus());
                    break;
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up));
        mViewModel = ViewModelProviders.of(this).get(ActiveSessionChatViewModel.class);

        setupUi();

        mViewModel.getSessionChat().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                mChatAdapter.setData(resource.data);
                stopRefreshing();
            } else if (resource.status == Resource.Status.LOADING) {
                startRefreshing();
            } else {
                stopRefreshing();
                Utils.toast(requireContext(), resource.message);
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    resetMessageState();
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(requireContext(), resource.message);
                }
            }
        });

        mViewModel.fetchSessionChat();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.activity_active_session_chat;
    }

    private void setupUi() {
        /*if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }*/

        initRefreshScreen(R.id.sr_session_chat);

        rvSessionChat.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true));
        mChatAdapter = new ActiveSessionChatAdapter(null, this);
        rvSessionChat.setAdapter(mChatAdapter);

        int serviceTag =getArguments().getInt(KEY_SERVICE_TYPE, EVENT_REQUEST_SERVICE_TYPE.SERVICE_NONE.tag);
        if (serviceTag == EVENT_REQUEST_SERVICE_TYPE.SERVICE_CALL_WAITER.tag)
            vCallWaiter.performClick();
        else if (serviceTag == EVENT_REQUEST_SERVICE_TYPE.SERVICE_CLEAN_TABLE.tag)
            vCleanTable.performClick();
        else if (serviceTag == EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY.tag)
            vRefillGlass.performClick();

        getActivity().invalidateOptionsMenu();
    }

    private void resetMessageState() {
        etMessage.setText("");
        etMessage.setHint(getResources().getString(R.string.title_how_can_we_assist_you));
        btnSendMsg.setTag(null);
        btnSendMsg.setEnabled(true);
    }

    public void setMessage(String msg) {
        etMessage.setText(msg);
        etMessage.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @OnClick(R.id.ll_call_waiter_button)
    public void clickCallWaiter() {
        setMessage(getResources().getString(R.string.msg_call_waiter));
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_CALL_WAITER);
    }

    @OnClick(R.id.ll_table_cleaning_button)
    public void clickTableClean() {
        setMessage(getResources().getString(R.string.msg_clean_table));
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_CLEAN_TABLE);
    }

    @OnClick(R.id.ll_refill_glass_button)
    public void clickRefillGlass() {
        setMessage(getResources().getString(R.string.msg_refill_glass));
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
    }

    @OnClick(R.id.im_expand_bottom_menu)
    public void onExpandMenu() {
        Animation bottomUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
        bottomExpandedMenu.startAnimation(bottomUp);
        bottomExpandedMenu.clearAnimation();
        bottomExpandedMenu.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.im_collapse_bottom_menu)
    public void onCollapseMenu() {
        Animation upDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down);
        bottomExpandedMenu.startAnimation(upDown);
        bottomExpandedMenu.clearAnimation();
        bottomExpandedMenu.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_chat_send_msg)
    public void sendMsg() {
        String msg = etMessage.getText().toString().trim();
        btnSendMsg.setEnabled(false);
        if (!TextUtils.isEmpty(msg)) {
            if (btnSendMsg.getTag() == null)
                btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_NONE);
            mViewModel.sendMessage(msg, btnSendMsg.getTag());
        } else {
            Utils.toast(requireContext(), "Please enter the message.");
            btnSendMsg.setEnabled(true);
        }
    }

    @OnClick(R.id.ll_napkin_container_button)
    public void clickNapkin() {
        setMessage("Napkins required");
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
        onCollapseMenu();
    }

    @OnClick(R.id.ll_extra_plates_container_button)
    public void clickExtraPlates() {
        setMessage("Bring extra plates");
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
        onCollapseMenu();
    }

    @OnClick(R.id.ll_salt_container_button)
    public void clickSalt() {
        setMessage("Bring salt");
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
        onCollapseMenu();
    }

    @OnClick(R.id.ll_sauce_container_button)
    public void clickSauce() {
        setMessage("Bring sauce");
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
        onCollapseMenu();
    }

    private void updateEvent(MessageObjectModel object, SessionChatModel.CHAT_STATUS_TYPE sessionEventStatus) {
        mViewModel.updateEventStatus(object.getPk(), sessionEventStatus);
    }


    private void addEvent(SessionChatModel sessionEventDetail) {
        mViewModel.addNewEvent(sessionEventDetail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SessionChatModel event = mChatAdapter.getSelectedEvent();
        if (event == null)
            return false;
        switch (item.getItemId()) {
            case R.id.menu_delay:
                mViewModel.raiseConcern(EVENT_CONCERN_TYPE.CONCERN_DELAY, event.getPk());
                return true;
            case R.id.menu_quality:
                mViewModel.raiseConcern(EVENT_CONCERN_TYPE.CONCERN_QUALITY, event.getPk());
                return true;
//            case R.id.menu_remark:
//                mViewModel.raiseConcern(EVENT_CONCERN_TYPE.CONCERN_REMARK, event.getPk());
//                return true;
            default:
                return false;
        }
    }


    @Override
    protected void updateScreen() {
        mViewModel.updateResults();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mChatAdapter.getSelectedEvent();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_active_session_chat, menu);
        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public void onSelectionChange(@Nullable SessionChatModel chatModel) {
        getActivity().invalidateOptionsMenu();
    }

    /*@OnClick(R.id.im_session_chat_back)
    public void onBack() {
        onBackPressed();
    }*/

    /*@Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mChatAdapter.getSelectedEvent() != null) {
            mChatAdapter.resetSelectedEvent();
        } else {
            Utils.setKeyboardVisibility(etMessage, false);
            super.onBackPressed();
        }
    }*/

    public boolean onBackPressed() {
        if (getFragmentManager() != null) {
            if (getView() != null)
                getView().startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down));
            getFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        MessageUtils.registerLocalReceiver(requireContext(), mReceiver, MESSAGE_TYPE.USER_SESSION_EVENT_NEW, MESSAGE_TYPE.USER_SESSION_EVENT_UPDATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(requireContext(), mReceiver);
    }
}
