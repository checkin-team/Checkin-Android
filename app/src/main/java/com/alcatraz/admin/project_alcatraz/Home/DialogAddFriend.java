package com.alcatraz.admin.project_alcatraz.Home;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class DialogAddFriend extends DialogFragment {
    String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
    ArrayList<Chip> chips=new ArrayList<>();

    static DialogAddFriend newInstance() {
        DialogAddFriend f = new DialogAddFriend();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);

        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_addfriend, container, false);
        for(int i=0;i<fruits.length;i++)
            chips.add(new Chip(fruits[i],null));
        ChipsInput mInputFriends = v.findViewById(R.id.chipinputforfriend);
        mInputFriends.setChipDeletable(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (inflater.getContext(), android.R.layout.simple_list_item_1, fruits);
//        //Getting the instance of AutoCompleteTextView
//        MultiAutoCompleteTextView actv =  v.findViewById(R.id.auto);
//        actv.setThreshold(1);//will start working from first character
//        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
//        actv.setTokenizer(new SpaceTokenizer());
//        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int po, long l) {
//                String f=actv.getAdapter().getItem(po).toString();
//                int i=0;
//                for(i=0;i<fruits.length;i++)
//                    if(fruits[i].equals(f))
//                        break;
//                mInputFriends.addChip(chips.get(i) );
//                Log.e(TAG, "onItemClick: "+i+" "+l );
//                actv.setText("        ");
//            }
//        });
        mInputFriends.setFilterableList(chips);
        v.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                //add in adapter
            }
        });
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return v;
    }
//    }
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // Get the layout inflater
//
//        ArrayList<Chip> friendsList = new ArrayList<>();
//        friendsList.add(new Chip("Ram", null));
//        friendsList.add(new Chip("Sam", null));
//        friendsList.add(new Chip("Gyan", "wise guy."));
//        friendsList.add(new Chip("Man", null));
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        View v=inflater.inflate(R.layout.dialog_addfriend, null);
//        ChipsInput mInputFriends=v.findViewById(R.id.chipinputforfriend);
//        mInputFriends.setFilterableList(friendsList);
//        v.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismiss();
//                //add in adapter
//            }
//        });
//        builder.setView(v);
//        Dialog dialog= builder.create();
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        return dialog;
//
//    }
}