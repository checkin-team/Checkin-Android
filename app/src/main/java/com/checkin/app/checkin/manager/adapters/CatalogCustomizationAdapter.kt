package com.checkin.app.checkin.manager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.checkin.app.checkin.databinding.ItemCatalogAddCustomizationBinding
import com.checkin.app.checkin.manager.models.ItemCustomizationModel

class CatalogCustomizationAdapter(val listener: CustomizationClickListener) : RecyclerView.Adapter<CatalogCustomizationAdapter.CatalogCustomizationViewHolder>() {
    private lateinit var list: List<ItemCustomizationModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogCustomizationViewHolder {
        return CatalogCustomizationViewHolder(ItemCatalogAddCustomizationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CatalogCustomizationViewHolder, position: Int) {
        holder.bind(list[position])
        holder.binding.tvAddMoreFields.setOnClickListener {
            listener.onItemClicked(position)
        }
    }

    override fun getItemCount(): Int = list.size

    fun setData(list: List<ItemCustomizationModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class CatalogCustomizationViewHolder(val binding: ItemCatalogAddCustomizationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemCustomizationModel) {
            binding.customizationModel = item
            val adapter = CatalogCustomizationFieldAdapter()
            adapter.setData(item.listOfFields)
            binding.rvFields.adapter = adapter
        }
    }
}

interface CustomizationClickListener {
    fun onItemClicked(position: Int)
}