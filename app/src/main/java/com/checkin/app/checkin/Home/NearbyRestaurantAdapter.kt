package com.checkin.app.checkin.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.checkin.app.checkin.Home.model.NearbyRestaurantModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.BaseViewHolder

class NearbyRestaurantAdapter : RecyclerView.Adapter<BaseViewHolder<Any>>() {
    var data: List<NearbyRestaurantModel> = emptyList()

    fun updateData(newData: List<NearbyRestaurantModel>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Any> {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return if (viewType == R.layout.item_home_restaurant_banner) NearbyRestaurantViewHolder(view) as BaseViewHolder<Any> else AdvertisementViewHolder(view)
    }

    override fun getItemCount(): Int = data.count() + 1

    override fun getItemViewType(position: Int): Int = if (position == 2) R.layout.item_advertisement_banner else R.layout.item_home_restaurant_banner

    override fun onBindViewHolder(holder: BaseViewHolder<Any>, position: Int) {
        holder.bindData(data[position])
    }

    inner class NearbyRestaurantViewHolder(itemView: View) : BaseViewHolder<NearbyRestaurantModel>(itemView) {
        override fun bindData(data: NearbyRestaurantModel) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    inner class AdvertisementViewHolder(itemView: View) : BaseViewHolder<Any>(itemView) {
        override fun bindData(data: Any) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
