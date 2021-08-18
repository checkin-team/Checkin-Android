package com.checkin.app.checkin.manager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.checkin.app.checkin.databinding.ItemCatalogAddVariantBinding
import com.checkin.app.checkin.manager.models.ItemVariantModel

class CatalogVariantAdapter : RecyclerView.Adapter<CatalogVariantAdapter.CatalogVariantViewHolder>() {
    private lateinit var list: List<ItemVariantModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogVariantViewHolder {
        return CatalogVariantViewHolder(ItemCatalogAddVariantBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CatalogVariantViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setData(list: List<ItemVariantModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    class CatalogVariantViewHolder(val binding: ItemCatalogAddVariantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemVariantModel) {
            binding.variant = item
        }
    }
}