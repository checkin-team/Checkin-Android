package com.checkin.app.checkin.misc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private val viewList: MutableList<View> = arrayListOf()

    abstract override fun getItemViewType(position: Int): Int

    fun getViewAtPosition(position: Int) = viewList.getOrNull(position)

    protected fun onCreateView(parent: ViewGroup, viewType: Int) = LayoutInflater.from(parent.context).inflate(viewType, parent, false).also {
        viewList.add(it)
    }
}