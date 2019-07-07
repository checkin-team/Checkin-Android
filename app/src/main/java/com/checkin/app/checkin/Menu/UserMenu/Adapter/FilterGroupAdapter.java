package com.checkin.app.checkin.Menu.UserMenu.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterGroupAdapter extends RecyclerView.Adapter<FilterGroupAdapter.ViewHolder> {
    private List<String> mCategories;
    private CategoryInteraction mListener;

    public FilterGroupAdapter(List<String> categories, CategoryInteraction listener) {
        this.mCategories = categories;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FilterGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterGroupAdapter.ViewHolder holder, int position) {
        holder.bind(mCategories.get(position));
    }

    @Override
    public int getItemCount() {
        return mCategories == null ? 0 : mCategories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_as_menu_filter_group_names;
    }

    public void setCategories(List<String> categories) {
        this.mCategories = categories;
        notifyDataSetChanged();
    }

    public String getCategory(int position) {
        return mCategories.get(position);
    }

    public interface CategoryInteraction {
        void onClick(String category);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_filter_category)
        TextView btnFilterCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final String title) {
            btnFilterCategory.setText(title);
            btnFilterCategory.setOnClickListener(view -> mListener.onClick(title));
        }
    }
}
