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
import com.checkin.app.checkin.session.models.TrendingDishModel

class MenuTreatYourselfAdapter(private val mListener: TreatYourselfInteraction) : RecyclerView.Adapter<MenuTreatYourselfAdapter.ViewHolder>() {
    private var mDishes: List<TrendingDishModel>? = null

    fun setData(data: List<TrendingDishModel>) {
        this.mDishes = data.filter { !it.isComplexItem }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(mDishes!![position])
    }

    override fun getItemCount(): Int = mDishes?.size ?: 0

    override fun getItemViewType(position: Int): Int = R.layout.item_menu_treat_yourself

    interface TreatYourselfInteraction {
        fun onTreatYourselfItemClick(itemModel: TrendingDishModel?)
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.im_menu_cart_treat_item_image)
        internal lateinit var imDish: ImageView
        @BindView(R.id.im_menu_cart_treat_item_type)
        internal lateinit var imDishType: ImageView
        @BindView(R.id.im_menu_cart_treat_item_name)
        internal lateinit var tvName: TextView

        private var mItemModel: TrendingDishModel? = null

        init {
            ButterKnife.bind(this, itemView)

            itemView.setOnClickListener { mListener.onTreatYourselfItemClick(mItemModel) }
        }

        internal fun bindData(itemModel: TrendingDishModel) {
            this.mItemModel = itemModel
            Utils.loadImageOrDefault(imDish, itemModel.image, 0)
            tvName.text = itemModel.name
            if (itemModel.isVegetarian)
                imDishType.setImageDrawable(imDishType.context.resources.getDrawable(R.drawable.ic_veg))
            else
                imDishType.setImageDrawable(imDishType.context.resources.getDrawable(R.drawable.ic_non_veg))
        }
    }
}
