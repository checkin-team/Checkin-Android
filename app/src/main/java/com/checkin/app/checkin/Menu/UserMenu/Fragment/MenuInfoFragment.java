package com.checkin.app.checkin.Menu.UserMenu.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MenuInfoFragment extends Fragment {
    @BindView(R.id.dark_back_menu_info)
    ViewGroup vDarkBack;
    @BindView(R.id.container_menu_info)
    ViewGroup containerMenuInfo;
    @BindView(R.id.tv_menu_info_description)
    TextView tvMenuItemInfo;
    @BindView(R.id.im_menu_info_image)
    ImageView imMenuItemInfo;
    private Unbinder unbinder;

    private MenuItemModel item;

    public MenuInfoFragment() {
    }

    public static MenuInfoFragment newInstance(MenuItemModel item) {
        MenuInfoFragment fragment = new MenuInfoFragment();
        fragment.item = item;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvMenuItemInfo.setText(item.getDescription());
        Glide.with(this).load(item.getImage()).into(imMenuItemInfo);
        view.setOnTouchListener((v, event) -> {
            onBackPressed();
            return true;
        });

        animateCard();
    }

    private void animateCard() {
        containerMenuInfo.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.expand_in));
    }

    public void onBackPressed() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .remove(this)
                    .commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
