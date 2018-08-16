package com.alcatraz.admin.project_alcatraz.Session;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
//import com.alcatraz.admin.project_alcatraz.Utility.GlideApp;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MenuInfoFragment extends Fragment {
    @BindView(R.id.tv_menu_item_info)
    TextView tvMenuItemInfo;
    @BindView(R.id.im_menu_item_info)
    ImageView imMenuItemInfo;
    private String desc, imUrl;
    private Unbinder unbinder;
    private MenuItemModel item;


    public MenuInfoFragment() {
        // Required empty public_selected constructor
    }

    public static MenuInfoFragment newInstance(MenuItemModel item) {
        MenuInfoFragment fragment = new MenuInfoFragment();
        fragment.item = item;
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_info, container, false);
        unbinder = ButterKnife.bind(this,view);
        tvMenuItemInfo.setText(item.getDescription());
        String url = "https://www.ebag.bg/en/product/3651/images/0/800";
        Glide.with(this).load(url).into(imMenuItemInfo);
        view.setOnTouchListener((v, event) -> true);
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
