package com.checkin.app.checkin.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.checkin.app.checkin.Home.model.SuggestedDishModel
import com.checkin.app.checkin.R

class PopularDishesAdapter : RecyclerView.Adapter<PopularDishesAdapter.ViewHolder>() {
    private var mDishesList: List<SuggestedDishModel>? = null

    fun setData(data: List<SuggestedDishModel>) {
        this.mDishesList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LayoutInflater.from(parent.context).inflate(viewType, parent, false).run { ViewHolder(this) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(mDishesList!![position])
    }

    override fun getItemCount() = mDishesList?.size ?: 0

    override fun getItemViewType(position: Int) = R.layout.item_home_suggested_dish


    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mItemModel: SuggestedDishModel? = null

        init {
            ButterKnife.bind(this, itemView)
        }

        internal fun bindData(itemModel: SuggestedDishModel) {
            this.mItemModel = itemModel
        }
    }
}
