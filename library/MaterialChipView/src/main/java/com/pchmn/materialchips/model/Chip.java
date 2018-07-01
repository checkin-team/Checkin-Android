package com.pchmn.materialchips.model;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Chip implements ChipInterface, Comparable<Chip>
{

    private Object id;
    private Uri avatarUri;
    private Drawable avatarDrawable;
    private String label;
    private String info;

    public Chip(@NonNull Object id, @Nullable Uri avatarUri, @NonNull String label, @Nullable String info) {
        this.id = id;
        this.avatarUri = avatarUri;
        this.label = label;
        this.info = info;
    }

    public Chip(@NonNull Object id, @Nullable Drawable avatarDrawable, @NonNull String label, @Nullable String info) {
        this.id = id;
        this.avatarDrawable = avatarDrawable;
        this.label = label;
        this.info = info;
    }

    public Chip(@Nullable Uri avatarUri, @NonNull String label, @Nullable String info) {
        this.avatarUri = avatarUri;
        this.label = label;
        this.info = info;
    }

    public Chip(@Nullable Drawable avatarDrawable, @NonNull String label, @Nullable String info) {
        this.avatarDrawable = avatarDrawable;
        this.label = label;
        this.info = info;
    }

    public Chip(@NonNull Object id, @NonNull String label, @Nullable String info) {
        this.id = id;
        this.label = label;
        this.info = info;
    }

    public Chip(@NonNull String label, @Nullable String info) {
        this.label = label;
        this.info = info;
    }

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

    @Override
    public int compareTo(@NonNull Chip o)
    {
        if(this.getId().equals(o.getId()))
            return 0;

        return this.getLabel().compareTo(o.getLabel());
    }

    @Override
    public int hashCode()
    {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || !(obj instanceof Chip))
            return false;

        if(this.getId() == null || ((Chip) obj).getId() == null)
        	return super.equals(obj);

        return this.getId().equals(((Chip)obj).getId());
    }
}
