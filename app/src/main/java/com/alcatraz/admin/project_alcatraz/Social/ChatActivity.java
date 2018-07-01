package com.alcatraz.admin.project_alcatraz.Social;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.alcatraz.admin.project_alcatraz.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by TAIYAB on 09-06-2018.
 */
public class ChatActivity extends AppCompatActivity{
    private static final String TAG = "ChatActivity";
    //ArrayList<String> message,time;
    //ArrayList<Integer> type;
    ChatAdapter chatAdapter;
    ArrayList<MessageUnit> messageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        randomChat();
    }
    public void randomChat(){
        messageList=new ArrayList<>();
        String s="Hey!`Hi...`How are you???`Grrrreat....`Don't you think it's strange that our whole existence is based on the assumption that there is a higher purpose???\nWhat if none of it means anything?`Yeah Ok.\nBYE.";
        Scanner sc=new Scanner(s);
        sc.useDelimiter("`");
        int i=1;
        while(sc.hasNext()) {
            messageList.add(new MessageUnit(sc.next(),"11:38 pm",i%2+1));
            i++;
        }
        //Log.e(TAG, message.toString() );
        RecyclerView recyclerView=findViewById(R.id.recycler_chat);
         chatAdapter = new ChatAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);



    }
    public void sendmessage(View v)
    {
        EditText text=findViewById(R.id.edittext_chat);
        //message.add(text.getText().toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String formattedDate = dateFormat.format(new Date()).toString();

        messageList.add(new MessageUnit(text.getText().toString(),formattedDate,1));
        chatAdapter.notifyItemInserted(messageList.size());
        ((RecyclerView)findViewById(R.id.recycler_chat)).scrollToPosition(messageList.size()-1);
        text.setText("");
        //InputMethodManager inputManager = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        //inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }
}
