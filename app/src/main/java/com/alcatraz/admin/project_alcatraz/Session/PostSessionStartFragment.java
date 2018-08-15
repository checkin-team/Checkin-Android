package com.alcatraz.admin.project_alcatraz.Session;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PostSessionStartFragment extends Fragment {
    private Unbinder unbinder;
    private MODESELECTED mode = null;
    private PostSessionFragmentInteraction fragmentInteraction;
    @BindView(R.id.im_markYourPresence) ImageView imAboutMarkYourPresence;
    @BindView(R.id.im_youAreWith) ImageView imAboutYouAreWith;
    @BindView(R.id.im_privateMode) ImageView imPrivateMode;
    @BindView(R.id.im_publicMode) ImageView imPublicMode;
    @BindView(R.id.ed_youAreWith) EditText edYouAreWith;
    @BindView(R.id.im_proceed) ImageView inProceed;

    public enum MODESELECTED{
        PUBLIC, PRIVATE
    };

    public PostSessionStartFragment() {
        // Required empty public_selected constructor
    }
    public static PostSessionStartFragment newInstance(PostSessionFragmentInteraction fragmentInteraction){
        PostSessionStartFragment fragment = new PostSessionStartFragment();
        fragment.fragmentInteraction = fragmentInteraction;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_scan, null );
        unbinder = ButterKnife.bind(this,view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return view;
    }
    @OnClick(R.id.im_proceed)
    public void OnProceedClicked(){
        String youAreWith = edYouAreWith.getText().toString();
        if(youAreWith.isEmpty()){
            edYouAreWith.setError("This field is mandatory");
        }
        else if(mode == null){
            Toast.makeText(getActivity().getApplicationContext(),"Please select mode",Toast.LENGTH_SHORT).show();
        }
       else
           fragmentInteraction.OnProceedClicked(this, youAreWith, mode);
            //getActivity().getSupportFragmentManager().popBackStack();


    }
    @OnClick(R.id.ed_youAreWith) public void youAreWith(){

    }
   /* @OnClick({R.id.im_publicMode,R.id.im_privateMode})
    public_selected void onModeIconClicked(ImageView icon){
       icon.setSelected(true);
         if(icon.getId() == R.id.im_privateMode ){
    mode = MODESELECTED.PRIVATE;
         }
         else {
             mode = MODESELECTED.PUBLIC;
         }
         if(mode == MODESELECTED.PRIVATE){
             imPublicMode.setSelected(false);
         }
         else {
             imPrivateMode.setSelected(false);
         }

    }*/
    @OnClick({R.id.im_publicMode,R.id.im_privateMode})
           public void  onModeIconClicked(ImageView icon) {
        icon.setSelected(true);
        if(icon.getId() == R.id.im_privateMode){
            mode = MODESELECTED.PRIVATE;
            imPrivateMode.setImageResource(R.drawable.private_selected);
            imPublicMode.setImageResource(R.drawable.public_deselected);
            imPublicMode.setSelected(false);
        }
        else{
            mode = MODESELECTED.PUBLIC;
            imPublicMode.setImageResource(R.drawable.public_selected);
            imPrivateMode.setImageResource(R.drawable.private_deselected);
            imPrivateMode.setSelected(false);
        }

       /* if (mode == MODESELECTED) {
            imPrivateMode.setBackgroundResource(R.drawable.male);
        }
        else {
            imPublicMode.setBackgroundResource(R.drawable.female);
        }*/
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
