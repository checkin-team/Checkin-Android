package com.checkin.app.checkin.User.Friendship;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

public class FriendsListActivity extends BaseActivity implements FriendshipAdapter.FriendshipInteraction {
    private static final String TAG = FriendsListActivity.class.getSimpleName();
    public final static String KEY_USER_PK = "friends_user_pk";

    private FriendshipViewModel mViewModel;
    private MaterialStyledDialog mMessageDialog;
    private EditText mEdMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        init(R.id.root_view, true);

        final FriendshipAdapter adapter = new FriendshipAdapter(null, FriendsListActivity.this );
        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(adapter);



        setupMessageDialog();

        mViewModel = ViewModelProviders.of(this, new FriendshipViewModel.Factory(getApplication())).get(FriendshipViewModel.class);
        mViewModel.setUserPk(getIntent().getLongExtra(KEY_USER_PK,0));
        mViewModel.getUserFriends().observe(this, listResource -> {
           if (listResource.status == Resource.Status.SUCCESS) {
               adapter.setData(listResource.data);
               doneLoad();
           } else if (listResource.status == Resource.Status.LOADING) {
               initLoad();
           }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS) {
                mViewModel.updateResults();
            }
        });

    }

    private void setupMessageDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.view_input_text, null);
        mEdMessage = view.findViewById(R.id.ed_input);
        mMessageDialog = new MaterialStyledDialog.Builder(this)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("Enter message")
                .setPositiveText("Request")
                .setNegativeText("Cancel")
                .setCustomView(view)
                .build();
    }

    @Override
    public void onAddFriend(final long userPk) {
        String message = mEdMessage.getText().toString();
        mMessageDialog.getBuilder()
                .onPositive((dialog, which) -> mViewModel.addFriend(userPk, message)).show();
    }

    @Override
    public void onRemoveFriend(long userPk) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("")
                .setMessage("Do you want to remove connection?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    dialog.dismiss();
                    mViewModel.removeFriend(userPk);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}