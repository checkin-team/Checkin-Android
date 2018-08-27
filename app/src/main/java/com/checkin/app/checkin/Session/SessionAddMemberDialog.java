package com.checkin.app.checkin.Session;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SessionAddMemberDialog extends DialogFragment {

    String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
//    ArrayList<Chip> chips=new ArrayList<>();

    static SessionAddMemberDialog newInstance() {
        SessionAddMemberDialog f = new SessionAddMemberDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);

        return f;
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_session_add_member, container, false);
        for(int i=0;i<fruits.length;i++)
            chips.add(new Chip(fruits[i],null));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (inflater.getContext(), R.layout.item_select_layout,R.id.tv_text, fruits);
        NachoTextView actv =  v.findViewById(R.id.auto);
        actv.setThreshold(1);
        ArrayList<SelectListItem> rowItems=new ArrayList<>();
        for(int i=0;i<fruits.length;i++)
        {
         rowItems.add(new SelectListItem(R.drawable.water,fruits[i],""));
        }
        actv.setOnChipClickListener((chip, event) -> actv.getChipTokenizer().deleteChipAndPadding(chip, actv.getEditableText()));
        actv.setChipTokenizer(new SpanChipTokenizer<>(inflater.getContext(), new ChipSpanChipCreator() {
            @Override
            public ChipSpan createChip(@NonNull Context context, @NonNull CharSequence text, Object data) {
                return new ChipSpan(context, text, ContextCompat.getDrawable(inflater.getContext(), R.drawable.ic_close), data);
            }

            @Override
            public void configureChip(@NonNull ChipSpan chip, @NonNull ChipConfiguration chipConfiguration) {
                super.configureChip(chip, chipConfiguration);
                chip.setShowIconOnLeft(false);
                chip.setIconBackgroundColor(getResources().getColor(R.color.colorPrimaryRed));
            }
        }, ChipSpan.class));

        SelectListViewAdapter selectListViewAdapter = new SelectListViewAdapter(inflater.getContext(),
                R.layout.item_select_layout, rowItems);
        actv.setAdapter(selectListViewAdapter);


        v.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                //add in adapter
            }
        });
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return v;
    }*/
}