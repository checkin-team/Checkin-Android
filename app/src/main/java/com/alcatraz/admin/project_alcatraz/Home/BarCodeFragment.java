package com.alcatraz.admin.project_alcatraz.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alcatraz.admin.project_alcatraz.R;

/**
 * Created by admin on 1/15/2018.
 */

public class BarCodeFragment extends Fragment {
    private final String TAG="barcode fragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_barcode,container,false);
        return view;
    }
}
