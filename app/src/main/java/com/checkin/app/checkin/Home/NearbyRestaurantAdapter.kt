package com.checkin.app.checkin.Home

import android.app.PendingIntent.getActivity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.checkin.app.checkin.Home.model.NearbyRestaurantModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.BaseViewHolder
import com.rd.PageIndicatorView
import com.rd.animation.type.AnimationType
import java.net.URL
import java.util.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.nfc.Tag
import android.widget.TextView
import butterknife.BindView
import com.bumptech.glide.Glide


class NearbyRestaurantAdapter(var context: Context) : RecyclerView.Adapter<BaseViewHolder<Any>>() {



    var data: List<NearbyRestaurantModel> = emptyList()

    fun updateData(newData: List<NearbyRestaurantModel>) {

        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Any> {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return if (viewType == R.layout.item_home_restaurant_banner) NearbyRestaurantViewHolder(view)
                as BaseViewHolder<Any> else AdvertisementViewHolder(view)
    }

    override fun getItemCount(): Int = data.count()

    override fun getItemViewType(position: Int): Int = if (position == 2)
        R.layout.item_advertisement_banner else R.layout.item_home_restaurant_banner

    override fun onBindViewHolder(holder: BaseViewHolder<Any>, position: Int) {
        holder.bindData(data[position])
    }

    inner class NearbyRestaurantViewHolder(itemView: View) : BaseViewHolder<NearbyRestaurantModel>(itemView) {


        var imageView:ImageView=itemView.findViewById(R.id.iv_home_restaurant_banner_main)

        var image_title:TextView=itemView.findViewById(R.id.tv_home_restaurant_banner_image_title)

        var ratings:TextView=itemView.findViewById(R.id.tv_home_restaurant_banner_ratings)
        var resturantName:TextView=itemView.findViewById(R.id.tv_home_restaurant_banner_restaurant_name)

        init {


            imageView.scaleType = ImageView.ScaleType.FIT_XY
        }

        override fun bindData(data: NearbyRestaurantModel) {

            image_title.setText(data.imageTitle)
            ratings.setText(data.ratings)
            resturantName.setText(data.restaurantName)
            Glide.with(context).asBitmap()
                    .load(
                    data.url).into(imageView)


        }
    }

    inner class AdvertisementViewHolder(itemView: View) : BaseViewHolder<Any>(itemView) {
        var vpBanner: ViewPager=itemView.findViewById(R.id.vp_home_banner)
       internal val mPagerAdapter: BannerPagerAdapter=BannerPagerAdapter(context);
        val indicatorView: PageIndicatorView? = itemView.findViewById(R.id.indicator_home_banner)
        init {


            vpBanner.adapter = mPagerAdapter
            if (indicatorView != null) {
                indicatorView.setViewPager(vpBanner)

                indicatorView.setAnimationType(AnimationType.FILL)
                indicatorView.setClickListener { position -> vpBanner.currentItem = position }
            } else {

            }
            vpBanner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {}

                override fun onPageScrollStateChanged(state: Int) {
                    enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE)
                }
            })
        }

        override fun bindData(data: Any) {





        }
        fun enableDisableSwipeRefresh(enable: Boolean) {  
        }

    }
    internal inner class BannerPagerAdapter(var mContext: Context) : PagerAdapter() {
        var mResources = intArrayOf(R.drawable.first_banner, R.drawable.second_banner, R.drawable.third_banner)
        var mLayoutInflater: LayoutInflater

        init {
            mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

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
