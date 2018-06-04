package com.alcatraz.admin.project_alcatraz.Session;

import android.support.annotation.NonNull;

public class MenuChip {
    protected String title;
    protected int count;

    public MenuChip(@NonNull String title, @NonNull int count) {
        this.title = title;
        this.count = count;
    }
}
