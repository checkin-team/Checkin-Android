package com.alcatraz.admin.project_alcatraz.Session;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alcatraz.admin.project_alcatraz.R;
import com.pchmn.materialchips.ChipView;
import com.pchmn.materialchips.util.ViewUtil;
import com.pchmn.materialchips.views.DetailedChipView;

import java.util.ArrayList;

public class MenuChipAdapter extends RecyclerView.Adapter<MenuChipAdapter.ChipViewHolder> {
    private ArrayList<MenuChip> chipsList;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private boolean mShowDetailedChip;
    private MenuUserFragment.OnMenuFragmentInteractionListener mMenuInteractionListener;

    MenuChipAdapter(Context context, boolean showDetailedChip, MenuUserFragment.OnMenuFragmentInteractionListener menuInteractionListener) {
        mShowDetailedChip = showDetailedChip;
        mContext = context;
        chipsList = new ArrayList<>();
        mMenuInteractionListener = menuInteractionListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return chipsList.size();
    }

    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ChipViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipViewHolder holder, int position) {
        holder.bindData(chipsList.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.menu_chip_layout;
    }

    public OrderedItem[] getItems() {
        int count = getItemCount();
        OrderedItem[] orderedItems = new OrderedItem[count];
        for (int i = 0; i < count; i++) {
            orderedItems[i] = chipsList.get(i).getOrderedItem();
        }
        return orderedItems;
    }

    public void addChip(MenuChip menuChip) {
        chipsList.add(menuChip);
        notifyItemInserted(chipsList.size() - 1);
        mMenuInteractionListener.onItemOrderInteraction(getItemCount());
    }

    public void removeAt(int position) {
        chipsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, chipsList.size());
        mMenuInteractionListener.onItemOrderInteraction(getItemCount());
    }

    public void clearItems() {
        chipsList.clear();
        notifyDataSetChanged();
        mMenuInteractionListener.onItemOrderInteraction(getItemCount());
    }

    class ChipViewHolder extends RecyclerView.ViewHolder {
        ChipView vChip;

        ChipViewHolder(View v) {
            super(v);
            vChip = v.findViewById(R.id.chip_view);
        }

        void bindData(final MenuChip menuChip) {
            vChip.setLabel(menuChip.getLabel() + " x " + menuChip.getOrderedItem().getCount());
            final int position = getAdapterPosition();

            vChip.setOnDeleteClicked(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAt(position);
                }
            });
            if (mShowDetailedChip) {
                vChip.setOnChipClicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final DetailedChipView detailedChipView = menuChip.getDetailedChip(mContext);
                        int[] coord = new int[2];
                        view.getLocationOnScreen(coord);
                        setDetailedChipViewPosition(detailedChipView, coord);
                        detailedChipView.setOnDeleteClicked(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeAt(position);
                                detailedChipView.fadeOut();
                            }
                        });
                    }
                });
            }
        }

        private void setDetailedChipViewPosition(DetailedChipView detailedChipView, int[] coord) {
            ViewGroup rootView = (ViewGroup) vChip.getRootView();
            View containerView = detailedChipView.findViewById(com.pchmn.materialchips.R.id.container);
            int windowWidth = ViewUtil.getWindowWidth(mContext);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewUtil.dpToPx(300), ViewUtil.dpToPx(100));
            Log.e("ChipAdapter", "Coord[0]: " + coord[0] + ", Coord[1]: " + coord[1]);
            Log.e("ViewUtil", "dpToPx(13): " + ViewUtil.dpToPx(13));
            if (coord[0] <= 0) {
                layoutParams.leftMargin = 0;
                detailedChipView.alignLeft();
            } else if (coord[0] + ViewUtil.dpToPx(300) > windowWidth + ViewUtil.dpToPx(13)) {
                layoutParams.leftMargin = windowWidth - ViewUtil.dpToPx(300);
                detailedChipView.alignRight();
            } else {
                layoutParams.leftMargin = coord[0] + ViewUtil.dpToPx(13);
            }
            layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(45);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            containerView.setLayoutParams(lp);
            rootView.addView(detailedChipView, layoutParams);
            detailedChipView.fadeIn();
        }
    }
}
