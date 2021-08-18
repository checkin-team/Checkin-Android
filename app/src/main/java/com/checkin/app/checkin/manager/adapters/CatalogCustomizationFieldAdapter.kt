package com.checkin.app.checkin.manager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.checkin.app.checkin.databinding.ItemCustomizationFieldBinding
import com.checkin.app.checkin.manager.models.CustomizationFieldModel

class CatalogCustomizationFieldAdapter : RecyclerView.Adapter<CatalogCustomizationFieldAdapter.CatalogCustomizationFieldViewHolder>() {
    private lateinit var list: List<CustomizationFieldModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogCustomizationFieldViewHolder {
        return CatalogCustomizationFieldViewHolder(ItemCustomizationFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CatalogCustomizationFieldViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setData(list: List<CustomizationFieldModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    class CatalogCustomizationFieldViewHolder(val binding: ItemCustomizationFieldBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomizationFieldModel) {
            binding.fieldModel = item
        }
    }
}