package com.checkin.app.checkin.Menu.UserMenu.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.session.model.TrendingDishModel
import com.checkin.app.checkin.Utility.DebouncedOnClickListener

class MenuBestSellerAdapter(private val mListener: SessionTrendingDishInteraction?) : RecyclerView.Adapter<MenuBestSellerAdapter.ViewHolder>() {
    private var mItems: List<TrendingDishModel>? = null

    fun setData(data: List<TrendingDishModel>) {
        this.mItems = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = LayoutInflater.from(parent.context).inflate(viewType, parent, false).run { ViewHolder(this) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindData(mItems!![position])

    override fun getItemCount(): Int = mItems?.size ?: 0

    override fun getItemViewType(position: Int): Int = R.layout.item_menu_bestseller_dish

    interface SessionTrendingDishInteraction {
        fun onDishClick(itemModel: TrendingDishModel)
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.im_menu_dish)
        internal lateinit var imDish: ImageView
        @BindView(R.id.im_menu_dish_name)
        internal lateinit var tvName: TextView
        @BindView(R.id.tv_menu_dish_price)
        internal lateinit var tvPrice: TextView

        private lateinit var mItemModel: TrendingDishModel

        init {
            ButterKnife.bind(this, itemView)

            itemView.setOnClickListener(DebouncedOnClickListener {
                mListener?.onDishClick(mItemModel)
            })
        }

        internal fun bindData(itemModel: TrendingDishModel) {
            this.mItemModel = itemModel
            Utils.loadImageOrDefault(imDish, itemModel.image, 0)
            tvName.text = itemModel.name
            tvPrice.text = Utils.formatCurrencyAmount(itemView.context, itemModel.typeCosts[0])
        }
    }
}
