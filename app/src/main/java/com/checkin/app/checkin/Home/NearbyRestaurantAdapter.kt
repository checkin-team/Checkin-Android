package com.checkin.app.checkin.Home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Home.model.NearbyRestaurantModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.BaseViewHolder
import com.checkin.app.checkin.Utility.Utils
import com.rd.PageIndicatorView
import com.rd.animation.type.AnimationType

class NearbyRestaurantAdapter(var context: Context) : RecyclerView.Adapter<BaseViewHolder<Any>>() {
    var data: List<NearbyRestaurantModel> = emptyList()

    fun updateData(newData: List<NearbyRestaurantModel>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Any> = LayoutInflater.from(parent.context).inflate(viewType, parent, false).run {
        if (viewType == R.layout.item_home_restaurant_banner) NearbyRestaurantViewHolder(this) as BaseViewHolder<Any> else AdvertisementViewHolder(this)
    }

    override fun getItemCount(): Int = data.count() + 1

    override fun getItemViewType(position: Int): Int = if (position == (itemCount % 2)) R.layout.item_advertisement_banner else R.layout.item_home_restaurant_banner

    override fun onBindViewHolder(holder: BaseViewHolder<Any>, position: Int) = holder.bindData(data[position])

    inner class NearbyRestaurantViewHolder(itemView: View) : BaseViewHolder<NearbyRestaurantModel>(itemView) {
        @BindView(R.id.im_restaurant_banner_cover)
        internal lateinit var imRestaurantBanner: ImageView
        @BindView(R.id.tv_restaurant_banner_cuisines)
        internal lateinit var tvCuisines: TextView
        @BindView(R.id.tv_restaurant_banner_rating)
        internal lateinit var tvRating: TextView
        @BindView(R.id.tv_restaurant_banner_name_locality)
        internal lateinit var tvRestaurantName: TextView

        init {
            ButterKnife.bind(this, itemView)
            imRestaurantBanner.scaleType = ImageView.ScaleType.FIT_XY
        }

        override fun bindData(data: NearbyRestaurantModel) {
            tvCuisines.text = data.imageTitle
            tvRating.text = data.ratings
            tvRestaurantName.text = data.restaurantName
            Utils.loadImageOrDefault(imRestaurantBanner, data.url, R.drawable.cover_restaurant_unknown)
        }
    }

    inner class AdvertisementViewHolder(itemView: View) : BaseViewHolder<Any>(itemView) {
        @BindView(R.id.vp_home_banner)
        internal lateinit var vpBanner: ViewPager
        @BindView(R.id.indicator_home_banner)
        internal lateinit var indicatorView: PageIndicatorView

        private val mPagerAdapter: BannerPagerAdapter = BannerPagerAdapter(context)

        init {
            ButterKnife.bind(this, itemView)
            vpBanner.adapter = mPagerAdapter
            indicatorView.setViewPager(vpBanner)
            indicatorView.setAnimationType(AnimationType.FILL)
            indicatorView.setClickListener { position -> vpBanner.currentItem = position }
        }

        override fun bindData(data: Any) {
        }

    }

    internal inner class BannerPagerAdapter(context: Context) : PagerAdapter() {
        private val mResources = intArrayOf(R.drawable.first_banner, R.drawable.second_banner, R.drawable.third_banner)
        private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

        override fun getCount(): Int {
            return mResources.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return `object` === view
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = mLayoutInflater.inflate(R.layout.item_home_banner, container, false)
            val imageView = itemView.findViewById<View>(R.id.im_home_banner) as ImageView
            imageView.setImageResource(mResources[position])
            container.addView(itemView)

            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
