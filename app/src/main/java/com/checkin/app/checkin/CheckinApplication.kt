package com.checkin.app.checkin

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.multidex.MultiDex
import com.bumptech.glide.Glide
import com.checkin.app.checkin.Utility.GlideApp
import com.checkin.app.checkin.menu.di.menuCartModule
import com.mikepenz.iconics.IconicsColor.Companion.colorRes
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize.Companion.dp
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerUIUtils
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CheckinApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        startKoin {
            androidContext(this@CheckinApplication)
            androidLogger()
            modules(menuCartModule)
        }
    }

    override fun onCreate() {
        super.onCreate()

        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable, tag: String?) {
                GlideApp.with(imageView.context).load(uri).placeholder(placeholder).into(imageView)
            }

            override fun cancel(imageView: ImageView) {
                Glide.with(imageView.context).clear(imageView)
            }

            override fun placeholder(ctx: Context, tag: String?): Drawable {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                return when (tag) {
                    DrawerImageLoader.Tags.PROFILE.name -> DrawerUIUtils.getPlaceHolder(ctx)
                    DrawerImageLoader.Tags.ACCOUNT_HEADER.name -> IconicsDrawable(ctx).iconText(" ").backgroundColor(colorRes(com.mikepenz.materialdrawer.R.color.primary)).size(dp(56))
                    "customUrlItem" -> IconicsDrawable(ctx).iconText(" ").backgroundColor(colorRes(R.color.md_red_500)).size(dp(56))
                    //we use the default one for
                    //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()
                    else -> super.placeholder(ctx, tag)
                }
            }
        })
    }
}