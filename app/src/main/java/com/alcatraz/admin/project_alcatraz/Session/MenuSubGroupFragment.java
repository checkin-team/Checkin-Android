package com.alcatraz.admin.project_alcatraz.Session;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alcatraz.admin.project_alcatraz.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuSubGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuSubGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuSubGroupFragment extends Fragment {
    private static final String SUB_GROUP_ID = "subGroupId";

    @BindView(R.id.sub_group_items) RecyclerView rvItemsList;

    private Unbinder unbinder;
    private OnFragmentInteractionListener mListener;
    private MenuItemAdapter mItemAdapter;

    private int mSubGroupId;

    public MenuSubGroupFragment() {
        // Required empty public_selected constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param subGroupId ID (Position) of Sub-Group.
     * @return A new instance of fragment MenuSubGroupFragment.
     */
    public static MenuSubGroupFragment newInstance(int subGroupId) {
        MenuSubGroupFragment fragment = new MenuSubGroupFragment();
        Bundle args = new Bundle();
        args.putInt(SUB_GROUP_ID, subGroupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSubGroupId = getArguments().getInt(SUB_GROUP_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.menu_sub_group_layout, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        mItemAdapter = new MenuItemAdapter(null);
        rvItemsList.setAdapter(mItemAdapter);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * */
    public interface OnFragmentInteractionListener {
    }
}
