package com.checkin.app.checkin.Menu.UserMenu.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.ExpandableTextView
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.DebouncedOnClickListener

class MenuItemAdapter(private var mItemsList: List<MenuItemModel>?, private val mListener: OnItemInteractionListener, private val mIsSessionActive: Boolean) : RecyclerView.Adapter<MenuItemAdapter.ItemViewHolder>() {

    fun setMenuItems(menuItems: List<MenuItemModel>?) {
        this.mItemsList = menuItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = R.layout.item_as_menu_group_item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder = LayoutInflater.from(parent.context).inflate(viewType, parent, false).run { ItemViewHolder(this) }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bindData(mItemsList!![position])

    override fun getItemCount() = mItemsList?.size ?: 0

    interface OnItemInteractionListener {
        fun onItemAdded(item: MenuItemModel?): Boolean

        fun onItemLongPress(item: MenuItemModel): Boolean

        fun onItemChanged(item: MenuItemModel?, count: Int): Boolean

        fun orderedItemCount(item: MenuItemModel): Int
    }

    inner class ItemViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tv_as_menu_item_name)
        internal lateinit var tvItemName: TextView
        @BindView(R.id.container_as_menu_quantity_picker)
        internal lateinit var containerQp: ViewGroup
        @BindView(R.id.container_menu_item_quantity)
        internal lateinit var containerItemQuantity: ViewGroup
        @BindView(R.id.tv_as_menu_item_price)
        internal lateinit var tvItemPrices: TextView
        @BindView(R.id.tv_menu_item_quantity_decrement)
        internal lateinit var tvQuantityDecrement: TextView
        @BindView(R.id.tv_menu_item_quantity_number)
        internal lateinit var tvQuantityNumber: TextView
        @BindView(R.id.tv_menu_item_quantity_increment)
        internal lateinit var tvQuantityIncrement: TextView
        @BindView(R.id.btn_as_menu_item_add)
        internal lateinit var btnItemAdd: TextView
        @BindView(R.id.tv_as_menu_item_desc)
        internal lateinit var tvDesc: ExpandableTextView
        @BindView(R.id.im_as_menu_item)
        internal lateinit var imItem: ImageView

        lateinit var menuItem: MenuItemModel

        private var mCount = 1
        private var mImageExpanded = false
        private var defaultCs: ConstraintSet
        private var imageCs: ConstraintSet
        private var descriptionCs: ConstraintSet
        private var mState = DEFAULT_STATE

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnLongClickListener { mListener.onItemLongPress(menuItem) }

            if (!mIsSessionActive)
                containerQp.visibility = View.GONE

            tvQuantityNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    val count = s.toString().toInt()
                    if (count < mCount && menuItem.isComplexItem) {
                        disallowDecreaseCount()
                        mCount = Math.min(mCount, count + 1)
                        displayQuantity(mCount)
                        return
                    }
                    if (!mListener.onItemChanged(menuItem, count)) {
                        displayQuantity(0)
                        hideQuantitySelection()
                        return
                    }
                    if (count == 0) hideQuantitySelection()
                    mCount = count
                }
            })
            btnItemAdd.setOnClickListener(DebouncedOnClickListener {
                if (!mListener.onItemAdded(menuItem)) {
                    Utils.toast(itemView.context, "Not allowed!")
                    return@DebouncedOnClickListener
                }
                showQuantitySelection(1)
            })

            imItem.setOnClickListener {
                if (mState != DESCRIPTION_STATE) {
                    mImageExpanded = !mImageExpanded
                    applyState(if (mImageExpanded) IMAGE_STATE else DEFAULT_STATE, animate = true)
                    tvDesc.toggle()
                }
            }

            tvQuantityDecrement.setOnClickListener { decreaseQuantity() }
            tvQuantityIncrement.setOnClickListener(DebouncedOnClickListener { increaseQuantity() })

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
                    if (menuItem.image == null) descriptionCs else defaultCs
                }
            }
            mState = state
            cs.applyTo(itemView as ConstraintLayout)
            if (animate) TransitionManager.beginDelayedTransition(itemView as ViewGroup)
        }

        internal fun bindData(menuItem: MenuItemModel) {
            this.menuItem = menuItem
            this.menuItem.setActiveSessionItemHolder(this)

            tvItemName.text = menuItem.name
            tvDesc.text = menuItem.description
            if (menuItem.image != null) {
                applyState(DEFAULT_STATE)
                Utils.loadImageOrDefault(imItem, menuItem.image, null)
            } else {
                applyState(DESCRIPTION_STATE)
            }

            tvItemPrices.text = Utils.formatIntegralCurrencyAmount(tvItemPrices.context, menuItem.typeCosts[0])

            val count = mListener.orderedItemCount(menuItem)
            if (count > 0)
                showQuantitySelection(count)
            else
                hideQuantitySelection()

            if (menuItem.isComplexItem) {
                btnItemAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_setting, 0)
            } else {
                btnItemAdd.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }

        internal fun hideQuantitySelection() {
            containerItemQuantity.visibility = View.GONE
            btnItemAdd.visibility = View.VISIBLE
        }

        private fun showQuantitySelection(count: Int) {
            btnItemAdd.visibility = View.GONE
            containerItemQuantity.visibility = View.VISIBLE
            displayQuantity(count)
        }

        fun changeQuantity(count: Int) {
            mCount = count
            if (count <= 0) hideQuantitySelection()
        }

        private fun increaseQuantity() {
            mCount++
            displayQuantity(mCount)
        }

        private fun decreaseQuantity() {
            if (menuItem.isComplexItem) {
                disallowDecreaseCount()
                return
            }
            if (mCount <= 0)
                return
            mCount--
            displayQuantity(mCount)
        }

        private fun displayQuantity(number: Int) {
            tvQuantityNumber.text = number.toString()
        }

        private fun disallowDecreaseCount() {
            Utils.toast(itemView.context, "Not allowed to change item count from here - use cart.")
        }
    }

    companion object {
        private val TAG = MenuItemAdapter::class.java.simpleName

        private val DEFAULT_STATE = 0
        private val DESCRIPTION_STATE = 1
        private val IMAGE_STATE = 2
    }
}
