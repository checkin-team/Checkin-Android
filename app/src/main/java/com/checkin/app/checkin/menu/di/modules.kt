package com.checkin.app.checkin.menu.di

import com.checkin.app.checkin.menu.activities.ActiveSessionMenuActivity
import com.checkin.app.checkin.menu.activities.ShopMenuActivity
import com.checkin.app.checkin.menu.fragments.UserMenuGroupsFragment
import com.checkin.app.checkin.menu.viewmodels.ActiveSessionCartViewModel
import com.checkin.app.checkin.menu.viewmodels.ScheduledCartViewModel
import com.checkin.app.checkin.restaurant.activities.PublicRestaurantProfileActivity
import org.koin.dsl.module

val menuCartModule = module {
    scope<ShopMenuActivity> {
        scoped { UserMenuGroupsFragment(ActiveSessionCartViewModel::class) }
    }
    scope<PublicRestaurantProfileActivity> {
        scoped { UserMenuGroupsFragment(ScheduledCartViewModel::class) }
    }
    scope<ActiveSessionMenuActivity> {
        scoped { UserMenuGroupsFragment(ActiveSessionCartViewModel::class) }
    }
}
