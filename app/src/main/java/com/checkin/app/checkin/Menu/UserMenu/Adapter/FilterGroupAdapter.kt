package com.checkin.app.checkin.Menu.UserMenu.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R

class FilterGroupAdapter(private var mCategories: List<String>?, private val mListener: CategoryInteraction) : RecyclerView.Adapter<FilterGroupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mCategories!![position])
    }

    override fun getItemCount(): Int {
        return if (mCategories == null) 0 else mCategories!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_as_menu_filter_group_names
    }

    fun setCategories(categories: List<String>) {
        this.mCategories = categories
        notifyDataSetChanged()
    }

    fun getCategory(position: Int): String {
        return mCategories!![position]
    }

    interface CategoryInteraction {
        fun onClick(category: String?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.btn_filter_category)
        internal lateinit var btnFilterCategory: TextView

        private var title: String? = null

        init {
            ButterKnife.bind(this, itemView)
            btnFilterCategory.setOnClickListener { mListener.onClick(title) }
        }

        fun bind(title: String) {
            this.title = title
            btnFilterCategory.text = title
        }
    }
}
