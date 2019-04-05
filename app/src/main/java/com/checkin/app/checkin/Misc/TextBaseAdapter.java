package com.checkin.app.checkin.Misc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

public class TextBaseAdapter extends RecyclerView.Adapter<TextBaseAdapter.TextViewHolder> {
    private String[] data;
    private DiscreteScrollView recyclerView;
    private int textColor, selectedColor;

    public TextBaseAdapter(int[] data, int textColor, int selectedColor) {
        this.data = Arrays.toString(data).split("[\\[\\]]")[1].split(", ");
        this.textColor = textColor;
        this.selectedColor = selectedColor;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = (DiscreteScrollView) recyclerView;

        this.recyclerView.setItemTransitionTimeMillis(150);
        this.recyclerView.setItemTransformer(new ScaleTransformer() {
            @Override
            public void transformItem(View item, float position) {
                float closenessToCenter = 1f - Math.abs(position);
                super.transformItem(item, position);
                ((TextView) item.findViewById(R.id.tv_value)).setTextColor(ColorUtils.blendARGB(textColor, selectedColor, closenessToCenter));
            }
        });
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_text;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        holder.bindData(data[position], recyclerView.getCurrentItem() == position);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        TextView vText;

        TextViewHolder(View view) {
            super(view);
            vText = view.findViewById(R.id.tv_value);
        }

        void bindData(String value, boolean selected) {
            vText.setText(value);
            vText.setTextColor(selected ? selectedColor : textColor);
        }
    }
}
