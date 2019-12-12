package com.checkin.app.checkin.Home

import android.content.Context
import android.text.Html
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
import com.checkin.app.checkin.Utility.pass
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

    private val adPosition: Int
        get() = data.count().coerceAtMost(1)

    override fun getItemCount(): Int = data.count() + 1

    override fun getItemViewType(position: Int): Int = if (position == adPosition) R.layout.item_advertisement_banner else R.layout.item_home_restaurant_banner

    override fun onBindViewHolder(holder: BaseViewHolder<Any>, position: Int) {
        when {
            position < adPosition -> holder.bindData(data[position])
            position > adPosition -> holder.bindData(data[position - 1])
            else -> holder.bindData(pass)
        }
    }

    inner class NearbyRestaurantViewHolder(itemView: View) : BaseViewHolder<NearbyRestaurantModel>(itemView) {
        @BindView(R.id.im_restaurant_banner_cover)
        internal lateinit var imRestaurantBanner: ImageView
        @BindView(R.id.tv_restaurant_banner_cuisines)
        internal lateinit var tvCuisines: TextView
        @BindView(R.id.tv_restaurant_banner_rating)
        internal lateinit var tvRating: TextView
        @BindView(R.id.tv_restaurant_banner_name_locality)
        internal lateinit var tvRestaurantName: TextView
        @BindView(R.id.tv_restaurant_banner_distance)
        internal lateinit var tvDistance: TextView
        @BindView(R.id.tv_restaurant_banner_offer_code)
        internal lateinit var tvCouponCode: TextView
        @BindView(R.id.tv_restaurant_banner_count_checkins)
        internal lateinit var tvCountCheckins: TextView
        @BindView(R.id.tv_restaurant_banner_offer_special)
        internal lateinit var tvExclusiveOffer: TextView
        @BindView(R.id.tv_restaurant_banner_offer_summary)
        internal lateinit var tvOfferSummary: TextView
        @BindView(R.id.im_restaurant_banner_distance)
        internal lateinit var imDistance: ImageView
        @BindView(R.id.container_restaurant_banner_offer)
        internal lateinit var containerOffer: ViewGroup

        init {
            ButterKnife.bind(this, itemView)
            imRestaurantBanner.scaleType = ImageView.ScaleType.FIT_XY
        }

        override fun bindData(data: NearbyRestaurantModel) {
            tvCuisines.text = data.cuisines.let {
                if (it.isNotEmpty()) {
                    val maxLen = it.size.coerceAtMost(3)
                    it.slice(0 until maxLen).joinToString(" ")
                } else "-"
            }

            tvRating.text = data.ratings.toString()
            tvDistance.text = data.formatDistance

            if (data.distance > 10) {
                imDistance.setImageResource(R.drawable.ic_road)
            }

            tvRestaurantName.text = data.locality.let {
                if (it != null) data.name + "-" + data.locality
                else data.name
            }

            tvCountCheckins.text = data.formatCheckins

            data.offers.let {
                if (it == null) containerOffer.visibility = View.GONE
                else {
                    containerOffer.visibility = View.VISIBLE
                    tvCouponCode.text = Html.fromHtml(context.getString(R.string.discount_code, it.code))
                    tvExclusiveOffer.visibility = if (it.isGlobal) View.INVISIBLE else View.VISIBLE
                    tvOfferSummary.text = it.name
                }
            }

            data.covers.let {
                if (it.getOrNull(0) != null) Utils.loadImageOrDefault(imRestaurantBanner, it[0], R.drawable.cover_restaurant_unknown)
                else imRestaurantBanner.setImageResource(R.drawable.cover_restaurant_unknown)
            }
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
