package com.checkin.app.checkin.home.holders

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
import com.checkin.app.checkin.R
import com.checkin.app.checkin.home.model.NearbyRestaurantModel
import com.checkin.app.checkin.misc.holders.BaseViewHolder
import com.checkin.app.checkin.restaurant.activities.openPublicRestaurantProfile
import com.checkin.app.checkin.utility.Utils
import com.rd.PageIndicatorView
import com.rd.animation.type.AnimationType


class NearbyRestaurantAdapter : RecyclerView.Adapter<BaseViewHolder<Any>>() {
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

    override fun getItemCount(): Int = data.count() //+1

    override fun getItemViewType(position: Int): Int = R.layout.item_home_restaurant_banner

    override fun onBindViewHolder(holder: BaseViewHolder<Any>, position: Int) {
        /*when {
            position < adPosition -> holder.bindData(data[position])
            position > adPosition -> holder.bindData(data[position - 1])
            else -> holder.bindData(pass)
        }*/
        holder.bindData(data[position])
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

        private var mRestaurantData: NearbyRestaurantModel? = null

        init {
            ButterKnife.bind(this, itemView)

            itemView.setOnClickListener {
                mRestaurantData?.let { itemView.context.openPublicRestaurantProfile(it.pk) }
            }
        }

        override fun bindData(data: NearbyRestaurantModel) {
            mRestaurantData = data

            tvCuisines.text = data.cuisines.let {
                if (it.isNotEmpty()) {
                    val maxLen = it.size.coerceAtMost(3)
                    it.slice(0 until maxLen).joinToString(" • ")
                } else "-"
            }

            tvRating.text = data.formatRating
            tvDistance.text = data.formatDistance

            imDistance.setImageResource(if (data.distance > 1.5) R.drawable.ic_distance_vehicle else R.drawable.ic_distance_walking)

            tvRestaurantName.text = data.locality.let {
                if (it != null) data.name + " - " + data.locality
                else data.name
            }

            tvCountCheckins.text = data.formatCheckins

            data.offer.let {
                if (it == null) containerOffer.visibility = View.GONE
                else {
                    containerOffer.visibility = View.VISIBLE
                    tvCouponCode.text = Utils.fromHtml(itemView.context.getString(R.string.discount_code, it.code))
                    tvExclusiveOffer.visibility = if (it.isGlobal) View.GONE else View.VISIBLE
                    tvOfferSummary.text = Utils.fromHtml(it.name)
                }
            }

            data.covers.let {
                if (it.getOrNull(0) != null) Utils.loadImageOrDefault(imRestaurantBanner, it[0], R.drawable.cover_restaurant_unknown)
                else imRestaurantBanner.setImageResource(R.drawable.cover_restaurant_unknown)
            }
        }
    }

    class AdvertisementViewHolder(itemView: View) : BaseViewHolder<Any>(itemView) {
        @BindView(R.id.vp_home_banner)
        internal lateinit var vpBanner: ViewPager
        @BindView(R.id.indicator_home_banner)
        internal lateinit var indicatorView: PageIndicatorView

        private val mPagerAdapter: BannerPagerAdapter = BannerPagerAdapter(itemView.context)

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

    class BannerPagerAdapter(context: Context) : PagerAdapter() {
        private val mResources = intArrayOf()//R.drawable.first_banner, R.drawable.second_banner, R.drawable.third_banner)
        private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

        override fun getCount(): Int {
            return mResources.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return `object` === view
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = mLayoutInflater.inflate(R.layout.item_ad_banner, container, false)
            val imageView = itemView.findViewById<View>(R.id.im_item_banner) as ImageView
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.setImageResource(mResources[position])
            container.addView(itemView)

            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
