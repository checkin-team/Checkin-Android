package com.checkin.app.checkin.User;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by TAIYAB on 22-02-2018.
 */

public class EditProfile extends AppCompatActivity{

 @BindViews({R.id.phoneNumber,R.id.email,R.id.bio,R.id.website,R.id.user_name})
 List<EditText> bind;
    public static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_layout);
        ButterKnife.bind(this);
    }

    public static boolean isEmailValid(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public void goback(View v){
        /*if(v.getId()==R.id.check)
        {
            if(!isEmailValid(bind.get(1).getText().toString()))
            {
                Toast.makeText(this, "The Email Entered is INVALID.", Toast.LENGTH_SHORT).show();
                return;
            }
            Box<UserProfileEntity> users=AppDatabase.getUserProfileModel(this);
            UserProfileEntity user=users.getAll().get(0);
            users.removeAll();
            user.setBio(bind.get(2).getText().toString());
            user.setName(bind.get(4).getText().toString());
            user.setProfession(bind.get(3).getText().toString());
            users.put(user);

        }*/
        finish();
    }
    public void changeImage(View v)
    {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            ((ImageView)findViewById(R.id.profile_photo))
                    .setImageURI(imageUri);
        }
    }
}
