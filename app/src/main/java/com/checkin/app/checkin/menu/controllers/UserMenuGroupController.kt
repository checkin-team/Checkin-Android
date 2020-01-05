package com.checkin.app.checkin.menu.controllers

import android.animation.ValueAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyModel
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuGroupModel
import com.checkin.app.checkin.Menu.UserMenu.Adapter.MenuGroupAdapter
import com.checkin.app.checkin.menu.holders.BestSellerModelHolder_
import com.checkin.app.checkin.menu.holders.SessionTrendingDishInteraction
import com.checkin.app.checkin.menu.holders.ViewGroupInteraction
import com.checkin.app.checkin.menu.holders.userMenuGroupModelHolder
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyController
import com.checkin.app.checkin.misc.holders.ShimmerModelHolder_
import com.checkin.app.checkin.misc.holders.ShimmerVerticalModelGroup
import com.checkin.app.checkin.misc.holders.verticalGridCarousel
import com.checkin.app.checkin.session.models.TrendingDishModel

class UserMenuGroupController(
        val bestSellerItemSpacing: Int = 0,
        val menuItemListener: MenuItemInteraction?,
        val groupInteractionListener: MenuGroupAdapter.OnGroupInteractionInterface?,
        val bestSellerListener: SessionTrendingDishInteraction?
) : BaseEpoxyController(), ViewGroupInteraction {
    private var mRecyclerView: RecyclerView? = null

    var menuGroups: List<MenuGroupModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }
    var trendingDishes: List<TrendingDishModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    var orderedCounts: Map<Long, Int>? = null
        set(value) {
            field = value
            requestModelBuild()
        }
    var doShowBestseller: Boolean = false
        set(value) {
            field = value
            requestModelBuild()
        }

    val hasData = menuGroups?.isNotEmpty() ?: false

    private var expandedGroupId: Long? = null

    override fun buildModels() {
        if (doShowBestseller) {
            val bestsellerModels: List<EpoxyModel<*>> = trendingDishes.let {
                it?.map { dish ->
                    BestSellerModelHolder_().apply {
                        id(dish.pk)
                        trendingItem(dish)
                        bestSellerListener?.let { listener(it) }
                        orderedCounts?.also { itemOrderedCount(it[dish.pk] ?: 0) }
                    }
                } ?: listOf(
                        ShimmerModelHolder_().apply { withAsMenuBestsellerItemLayout().id("sb1") },
                        ShimmerModelHolder_().apply { withAsMenuBestsellerItemLayout().id("sb2") }
                )
            }
            verticalGridCarousel {
                id("bestseller")
                models(bestsellerModels)
                itemSpacing(bestSellerItemSpacing)
            }
        }

        menuGroups.let {
            if (it != null) {
                it.forEach { group ->
                    userMenuGroupModelHolder {
                        id(group.pk)
                        groupData(group)
                        isExpanded(group.pk == expandedGroupId)
                        menuItemListener?.let { itemListener(it) }
                        groupViewListener(this@UserMenuGroupController)
                        groupItemsOrderedCounts(orderedCounts?.filterKeys { pk ->
                            group.items.find { it.pk == pk } != null
                        } ?: emptyMap())
                    }
                }
            } else {
                ShimmerVerticalModelGroup(4) {
                    (this as ShimmerModelHolder_).withAsMenuGroupLayout().id("s$it")
                }.addTo(this)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
    }

    override fun contractView() {
        if (expandedGroupId != null) {
            expandedGroupId = null
            requestModelBuild()
        }
    }

    override fun expandView(group: MenuGroupModel) {
        expandedGroupId = group.pk
        requestModelBuild()

        val scrollAnim = ValueAnimator.ofInt(1, 2, 3, 4)
        scrollAnim.duration = GROUP_ANIMATION_DURATION
        scrollAnim.addUpdateListener {
            (mRecyclerView?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(adapter.run {
                getModelById(group.pk)?.let { getModelPosition(it) } ?: 0
            }, 0)
        }
        scrollAnim.start()
    }

    companion object {
        private const val GROUP_ANIMATION_DURATION = 300L
    }
}
