package com.checkin.app.checkin.menu.holders

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.menu.models.MenuItemModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.misc.views.ExpandableTextView
import com.checkin.app.checkin.utility.Utils

@EpoxyModelClass(layout = R.layout.item_as_menu_group_item)
abstract class UserMenuItemModelHolder : EpoxyModelWithHolder<UserMenuItemModelHolder.Holder>() {
    @EpoxyAttribute
    lateinit var menuItem: MenuItemModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: OnItemInteractionListener

    @EpoxyAttribute
    var itemOrderedCount: Int = 0

    override fun bind(holder: Holder) {
        holder.mCount = itemOrderedCount
        holder.bindData(menuItem)
    }

    inner class Holder : BaseEpoxyHolder<MenuItemModel>() {
        @BindView(R.id.tv_as_menu_item_name)
        internal lateinit var tvItemName: TextView
        @BindView(R.id.tv_as_menu_item_price)
        internal lateinit var tvItemPrices: TextView
        @BindView(R.id.container_menu_item_add_quantity)
        internal lateinit var containerAddQuantity: ViewGroup
        @BindView(R.id.tv_menu_item_add)
        internal lateinit var tvMenuItemAdd: TextView
        @BindView(R.id.tv_menu_item_add_quantity)
        internal lateinit var tvItemQuantity: TextView
        @BindView(R.id.im_menu_item_quantity_increment)
        internal lateinit var imQuantityIncrement: ImageView
        @BindView(R.id.im_menu_item_quantity_decrement)
        internal lateinit var imQuantityDecrement: ImageView
        @BindView(R.id.tv_as_menu_item_desc)
        internal lateinit var tvDesc: ExpandableTextView
        @BindView(R.id.im_as_menu_item)
        internal lateinit var imItem: ImageView

        private var menuItem: MenuItemModel? = null

        private var mImageExpanded = false
        private lateinit var defaultCs: ConstraintSet
        private lateinit var imageCs: ConstraintSet
        private lateinit var descriptionCs: ConstraintSet
        private var mState = DEFAULT_STATE
        internal var mCount = 0

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            itemView.setOnLongClickListener {
                menuItem?.let { listener.onItemLongPress(it) } ?: false
            }

            tvMenuItemAdd.setOnClickListener {
                if (!listener.onItemAdded(menuItem)) {
                    Utils.toast(itemView.context, "Not allowed!")
                    return@setOnClickListener
                }

            }

            imItem.setOnClickListener {
                if (mState != DESCRIPTION_STATE) {
                    mImageExpanded = !mImageExpanded
                    applyState(if (mImageExpanded) IMAGE_STATE else DEFAULT_STATE, animate = true)
                    tvDesc.toggle()
                }
            }

            imQuantityDecrement.setOnClickListener { decreaseQuantity() }
            imQuantityIncrement.setOnClickListener { increaseQuantity() }

            defaultCs = ConstraintSet().apply { clone(itemView as ConstraintLayout) }
            imageCs = ConstraintSet().apply { clone(itemView.context, R.layout.item_as_menu_group_item_image_expanded) }
            descriptionCs = ConstraintSet().apply { clone(itemView.context, R.layout.item_as_menu_group_item_description_expanded) }

            tvDesc.setCondition { mState != IMAGE_STATE }
            tvDesc.setListener(object : ExpandableTextView.ExpandableTextListener {
                override fun onToggle(trimmed: Boolean) {
                    if (mState == IMAGE_STATE) return
                    applyState(if (trimmed) DEFAULT_STATE else DESCRIPTION_STATE, animate = true)
                }
            })
        }

        internal fun applyState(state: Int, animate: Boolean = false) {
            val cs = when (state) {
                IMAGE_STATE -> imageCs
                DESCRIPTION_STATE -> descriptionCs
                else -> {
                    if (menuItem?.image == null) descriptionCs else defaultCs
                }
            }
            mState = state
            cs.applyTo(itemView as ConstraintLayout)
            if (animate) TransitionManager.beginDelayedTransition(itemView as ViewGroup)
        }

        override fun bindData(data: MenuItemModel) {
            menuItem = data

            tvItemName.text = data.name
            tvDesc.text = data.description ?: ""
            if (data.image != null) {
                applyState(DEFAULT_STATE)
                Utils.loadImageOrDefault(imItem, data.image, null)
            } else {
                applyState(DESCRIPTION_STATE)
            }

            tvItemPrices.text = Utils.formatIntegralCurrencyAmount(tvItemPrices.context, data.typeCosts[0])

            tvItemQuantity.text = mCount.toString()
            if (mCount > 0) showAddQuantity()
            else hideAddQuantity()

            if (data.isComplexItem) {
                tvMenuItemAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_setting, 0)
            } else {
                tvMenuItemAdd.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }

        fun showAddQuantity() {
            containerAddQuantity.visibility = View.VISIBLE
            tvMenuItemAdd.visibility = View.GONE
        }

        fun hideAddQuantity() {
            containerAddQuantity.visibility = View.GONE
            tvMenuItemAdd.visibility = View.VISIBLE
        }

        private fun increaseQuantity() {
            listener.onItemChanged(menuItem, mCount + 1)
        }

        private fun decreaseQuantity() {
            if (menuItem!!.isComplexItem) {
                disallowDecreaseCount()
                return
            }
            if (mCount <= 0)
                return
            listener.onItemChanged(menuItem, mCount - 1)
        }

        private fun disallowDecreaseCount() {
            Utils.toast(itemView.context, "Not allowed to change item count from here - use cart.")
        }
    }

    companion object {
        private val DEFAULT_STATE = 0
        private val DESCRIPTION_STATE = 1
        private val IMAGE_STATE = 2
    }
}

interface OnItemInteractionListener {
    fun onItemAdded(item: MenuItemModel?): Boolean

    fun onItemLongPress(item: MenuItemModel): Boolean

    fun onItemChanged(item: MenuItemModel?, count: Int): Boolean
}
