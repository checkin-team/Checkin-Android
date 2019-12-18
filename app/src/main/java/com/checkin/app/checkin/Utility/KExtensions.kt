package com.checkin.app.checkin.Utility

import androidx.annotation.ColorInt
import com.google.android.material.tabs.TabLayout

fun TabLayout.setTabBackground(@ColorInt color: Int) = (0 until tabCount).forEach {
    getTabAt(it)?.view?.setBackgroundColor(color)
}
