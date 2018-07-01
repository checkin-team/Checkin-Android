package com.alcatraz.admin.project_alcatraz.Utility;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.alcatraz.admin.project_alcatraz.Session.MenuChip;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.views.DetailedChipView;

public class MaterialChip implements ChipInterface {
    protected String label;
    protected String info;
    protected Drawable avatarDrawable;
    protected Object id;
    protected Uri avatarUri;

    protected ColorStateList detailedBackgroundColor;
    protected ColorStateList detailedTextColor;
    protected ColorStateList detailedDeleteIconColor;

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Uri getAvatarUri() {
        return avatarUri;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return avatarDrawable;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getInfo() {
        return info;
    }

    public static DetailedChipView getDetailedChipView(Context context, MaterialChip chip) {
        return (new DetailedChipView.Builder(context))
                .chip(chip)
                .backgroundColor(chip.detailedBackgroundColor)
                .textColor(chip.detailedTextColor)
                .deleteIconColor(chip.detailedDeleteIconColor)
                .build();
    }
}
