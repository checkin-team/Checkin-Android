package com.alcatraz.admin.project_alcatraz.Session;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alcatraz.admin.project_alcatraz.Utility.MaterialChip;
import com.pchmn.materialchips.views.DetailedChipView;

import java.util.Locale;

public class MenuChip extends MaterialChip {
    private OrderedItem mOrderedItem;

    MenuChip(@NonNull OrderedItem item) {
        mOrderedItem = item;
        avatarUri = null;
        avatarDrawable = null;
        label = item.name + " (" + item.getType() + ")";
        info = String.format(Locale.ENGLISH, "Count: %d | Price: %.2f", item.getCount(), item.getPrice());
    }

    MenuChip(@NonNull String title, int count, float price, @Nullable String type) {
        avatarDrawable = null;
        avatarUri = null;
        label = title + " (" + type + ")";
        info = String.format(Locale.ENGLISH, "Count: %d | Price: %.2f", count, price);
    }

    public OrderedItem getOrderedItem() {
        return mOrderedItem;
    }

    public DetailedChipView getDetailedChip(Context mContext) {
        return MaterialChip.getDetailedChipView(mContext, this);
    }
}
