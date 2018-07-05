package com.alcatraz.admin.project_alcatraz.Social;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.alcatraz.admin.project_alcatraz.Utility.NamedFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by TAIYAB on 09-06-2018.
 */
public class ChatActivity extends AppCompatActivity{
    private static final String TAG = ChatActivity.class.getSimpleName();
    private MessageViewModel mMessageViewModel;
    private int mUserId;
    MessageAdapter messageAdapter;
    @BindView(R.id.edittext_chat) EditText edChatText;
    @BindView(R.id.recycler_chat) RecyclerView rvChatList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mUserId = getIntent().getIntExtra(Constants.EXTRA_SELECTED_USER_ID, 0);
        if (mUserId == 0) {
            Log.e(TAG, "Invalid User ID!");
            finish();
        }
        messageAdapter = new MessageAdapter(null);
        rvChatList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvChatList.setAdapter(messageAdapter);

        mMessageViewModel = ViewModelProviders.of(this, new MessageViewModel.Factory(getApplication())).get(MessageViewModel.class);
        mMessageViewModel.getMessagesOfChat(mUserId).observe(this, new Observer<Resource<List<Message>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Message>> messagesResource) {
                if (messagesResource == null)
                    return;
                if (messagesResource.status == Resource.Status.SUCCESS) {
                    messageAdapter.setMessages(messagesResource.data);
                } else if (messagesResource.status == Resource.Status.LOADING) {
                    // LOADING
                } else{
                        Toast.makeText(ChatActivity.this, "Error fetching messages! Status: " +
                                messagesResource.status.toString() + "\nDetails: " + messagesResource.message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @OnClick(R.id.button_chat_send)
    public void send_message(View v)
    {
        Message msg = new Message(edChatText.getText().toString(), new Date(), 0, mUserId);
        Map<String, Integer> values = new HashMap<>();
        values.put("sender", msg.getSenderId());
        values.put("recipient", msg.getRecipientId());
        values.put("chat", msg.getChatId());
        Log.e(TAG, NamedFormatter.format("senderID: %(sender), recipientID: %(recipient) & chatID: %(chat)", values));
        mMessageViewModel.sendMessages(msg);
        edChatText.setText("");
        rvChatList.smoothScrollToPosition(rvChatList.getAdapter().getItemCount());
    }
}
