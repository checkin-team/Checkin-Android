package com.alcatraz.admin.project_alcatraz.Session;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alcatraz.admin.project_alcatraz.R;
import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnCloseClickListener;

import java.util.ArrayList;

public class MenuChipAdapter extends RecyclerView.Adapter<MenuChipAdapter.ChipViewHolder> {
    private ArrayList<MenuChip> chipsList;

    MenuChipAdapter() {
        chipsList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return chipsList.size();
    }

    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ChipViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipViewHolder holder, int position) {
        holder.bindData(chipsList.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.menu_chip_layout;
    }

    public void addChip(MenuChip menuChip) {
        chipsList.add(menuChip);
        notifyItemInserted(chipsList.size() - 1);
    }

    public void removeAt(int position) {
        chipsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, chipsList.size());
    }

    class ChipViewHolder extends RecyclerView.ViewHolder {
        Chip vChip;
        MenuChipAdapter mChipAdapter;

        ChipViewHolder(View v, MenuChipAdapter chipAdapter) {
            super(v);
            vChip = v.findViewById(R.id.chip_view);
            mChipAdapter = chipAdapter;
            vChip.setOnCloseClickListener(new OnCloseClickListener() {
                @Override
                public void onCloseClick(View v) {
                    removeAt(getAdapterPosition());
                }
            });
        }

        void bindData(MenuChip menuChip) {
            vChip.setChipText(menuChip.title + " x " + menuChip.count);
        }
    }
}
