package com.checkin.app.checkin.Menu.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterCategoryAdapter extends RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder>{
    private List<String> mCategories;
    private CategoryInteraction mListener;

    public FilterCategoryAdapter(List<String> categories, CategoryInteraction listener) {
        this.mCategories = categories;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FilterCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterCategoryAdapter.ViewHolder holder, int position) {
        holder.bind(mCategories.get(position));
    }

    @Override
    public int getItemCount() {
        return mCategories == null ? 0 : mCategories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_menu_filter_category;
    }

    public void setCategories(List<String> categories){
        this.mCategories = categories;
        notifyDataSetChanged();
    }

    public String getCategory(int position) {
        return mCategories.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.btn_filter_category) Button btnFilterCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final String title) {
            btnFilterCategory.setText(title);
            btnFilterCategory.setOnClickListener(view -> mListener.onClick(title));
        }
    }

    public interface CategoryInteraction {
        void onClick(String category);
    }
}
