package com.checkin.app.checkin.misc.views

import android.app.Activity
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.checkin.app.checkin.R

/**
 * Created by TAIYAB on 05-02-2018.
 */
open class EndDrawerToggle(activity: Activity, private val drawerLayout: DrawerLayout, toolbar: Toolbar,
                           openDrawerContentDescRes: Int, closeDrawerContentDescRes: Int, @DrawableRes toggleButtonDrawable: Int) : DrawerListener {

    private val arrowDrawable: DrawerArrowDrawable = DrawerArrowDrawable(toolbar.context)
    private val toggleButton: AppCompatImageButton = AppCompatImageButton(toolbar.context, null, R.attr.toolbarNavigationButtonStyle)
    private val openDrawerContentDesc: String = activity.getString(openDrawerContentDescRes)
    private val closeDrawerContentDesc: String = activity.getString(closeDrawerContentDescRes)

    init {
        arrowDrawable.direction = DrawerArrowDrawable.ARROW_DIRECTION_END
        toolbar.addView(toggleButton, Toolbar.LayoutParams(GravityCompat.END))
        toggleButton.setImageDrawable(activity.resources.getDrawable(toggleButtonDrawable))
        toggleButton.setOnClickListener { toggle() }
    }

    fun syncState() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) setPosition(1f)
        else setPosition(0f)
    }

    fun toggle() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun setPosition(position: Float) {
        if (position == 1f) {
            arrowDrawable.setVerticalMirror(true)
            toggleButton.contentDescription = closeDrawerContentDesc
        } else if (position == 0f) {
            arrowDrawable.setVerticalMirror(false)
            toggleButton.contentDescription = openDrawerContentDesc
        }
        arrowDrawable.progress = position
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        setPosition(Math.min(1f, Math.max(0f, slideOffset)))
    }

    override fun onDrawerOpened(drawerView: View) {
        setPosition(1f)
    }

    override fun onDrawerClosed(drawerView: View) {
        setPosition(0f)
    }

    override fun onDrawerStateChanged(newState: Int) {}
}