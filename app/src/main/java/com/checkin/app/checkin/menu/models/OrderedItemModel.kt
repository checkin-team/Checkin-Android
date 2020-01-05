package com.checkin.app.checkin.menu.models

import com.checkin.app.checkin.Menu.Model.ItemCustomizationFieldModel
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.Utility.Constants.DEFAULT_ORDER_CANCEL_DURATION
import com.fasterxml.jackson.annotation.*
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class OrderedItemModel(
        val pk: Long,
        @JsonSetter("item") val item: MenuItemModel,
        val cost: Double,
        val quantity: Int,
        @JsonProperty("type_index") val typeIndex: Int,
        @JsonProperty("customizations") val selectedFields: List<ItemCustomizationFieldModel> = emptyList(),
        val remarks: String? = null,
        val ordered: Date? = null
) {
    constructor(item: MenuItemModel, quantity: Int, typeIndex: Int) : this(0, item, item.typeCosts[typeIndex] * quantity, quantity, typeIndex)

    constructor(item: MenuItemModel, quantity: Int) : this(item, quantity, 0)

    @JsonGetter
    fun customizations(): List<Long> = selectedFields.map { it.pk }

    @JsonIgnore
    fun typeName(): String? = item.typeNames.getOrNull(typeIndex)

    @JsonGetter("item")
    fun itemPk(): Long = item.pk

    @JsonIgnore
    fun getRemainingCancelTime(): Long = DEFAULT_ORDER_CANCEL_DURATION - (Date().time - ordered!!.time)

    @JsonIgnore
    val itemModel = item

    @JsonIgnore
    fun canCancel() = getRemainingCancelTime() >= 0

    @JsonIgnore
    fun canOrder(): Boolean {
        if (quantity <= 0 || typeIndex >= itemModel.typeNames.size) return false
        itemModel.customizations.forEach {
            val count = if (selectedFields.isNotEmpty()) {
                it.customizationFields.count { selectedFields.contains(it) }
            } else 0
            if (count < it.minSelection || count > it.maxSelection) return false
        }
        return true
    }

    @JsonIgnore
    fun isCustomized() = item.isComplexItem || selectedFields.isNotEmpty()

    fun addCustomizationField(field: ItemCustomizationFieldModel): OrderedItemModel = copy(
            selectedFields = selectedFields.toMutableList().apply { add(field) },
            cost = cost + (field.cost * quantity)
    )

    fun removeCustomizationField(field: ItemCustomizationFieldModel): OrderedItemModel = copy(
            selectedFields = selectedFields.toMutableList().apply { remove(field) },
            cost = cost - (field.cost * quantity)
    )

    fun selectType(typeIndex: Int): OrderedItemModel = copy(
            typeIndex = typeIndex
    )

    fun updatePk(pk: Long): OrderedItemModel = copy(pk = pk)

    fun updateQuantity(quantity: Int): OrderedItemModel = copy(quantity = quantity, cost = item.typeCosts[typeIndex] * quantity)

    fun equalsWithoutPk(other: OrderedItemModel): Boolean {
        return itemPk() == other.itemPk() && selectedFields == other.selectedFields && typeIndex == other.typeIndex
    }

    fun equalsWithPk(other: OrderedItemModel): Boolean {
        return pk != 0L && other.pk != 0L && pk == other.pk
    }

    companion object {
        val TAG = OrderedItemModel::class.simpleName
    }
}
