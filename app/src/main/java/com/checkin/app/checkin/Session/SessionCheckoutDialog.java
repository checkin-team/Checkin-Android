package com.checkin.app.checkin.Session;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.checkin.app.checkin.R;

public class SessionCheckoutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v=inflater.inflate(R.layout.dialog_session_checkout, null);
        v.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImage(view);
            }
        });
        v.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImage(view);
            }
        });
        builder.setView(v);
        Dialog dialog= builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
    public void onClickImage(View v)
    {
        switch (v.getId()){
            case R.id.confirm:
                dismiss();
                getActivity().finish();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}