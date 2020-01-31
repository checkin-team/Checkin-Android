package com.checkin.app.checkin.misc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.utility.GlideApp

class CoverPagerAdapter(@DrawableRes val defaultImage: Int) : RecyclerView.Adapter<CoverPagerAdapter.ViewHolder>() {
    private var mData: List<String?> = emptyList()

    fun setData(vararg data: String?) = setData(listOf(*data))

    fun setData(data: List<String?>) {
        mData = data
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_cover_page

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = LayoutInflater.from(parent.context).inflate(viewType, parent, false).let {
        ViewHolder(it)
    }

    override fun getItemCount(): Int = mData.filterNotNull().takeIf { it.isNotEmpty() }?.size ?: 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(mData.getOrNull(position))

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.im_cover_page)
        internal lateinit var imCover: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind(uri: String?) {
            if (uri != null) GlideApp.with(imCover).load(uri).into(imCover)
            else imCover.setImageResource(defaultImage)
        }
    }
}