package com.checkin.app.checkin.menu.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.menu.models.ItemCustomizationFieldModel
import com.checkin.app.checkin.menu.models.MenuItemModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.parentFragmentDelegate
import com.checkin.app.checkin.utility.parentViewModels

class ItemCustomizationBottomSheetFragment : BaseBottomSheetFragment(), ItemCustomizationGroupHolder.CustomizationGroupInteraction {
    override val rootLayout = R.layout.fragment_as_menu_item_customization

    @BindView(R.id.container_menu_customization)
    internal lateinit var vMenuCustomizations: ConstraintLayout
    @BindView(R.id.tv_menu_customization_bill)
    internal lateinit var tvBill: TextView
    @BindView(R.id.tv_menu_customization_item_name)
    internal lateinit var tvItemName: TextView
    @BindView(R.id.rg_menu_customization_type)
    internal lateinit var groupRadio: RadioGroup
    @BindView(R.id.line_horizontal)
    internal lateinit var vLineHorizontal: View
    @BindView(R.id.line_horizontal_2)
    internal lateinit var vLineHorizontalTwo: View
    @BindView(R.id.rb_menu_customization_type_1)
    internal lateinit var btnRadio1: RadioButton
    @BindView(R.id.rb_menu_customization_type_2)
    internal lateinit var btnRadio2: RadioButton
    @BindView(R.id.rb_menu_customization_type_3)
    internal lateinit var btnRadio3: RadioButton
    @BindView(R.id.tv_menu_customization_radio_1)
    internal lateinit var tvRadioLabel1: TextView
    @BindView(R.id.tv_menu_customization_radio_2)
    internal lateinit var tvRadioLabel2: TextView
    @BindView(R.id.tv_menu_customization_radio_3)
    internal lateinit var tvRadioLabel3: TextView
    @BindView(R.id.list_menu_customizations)
    internal lateinit var listCustomizations: LinearLayout
    @BindView(R.id.sv_menu_customization)
    internal lateinit var svContainerCustomization: ViewGroup
    @BindView(R.id.container_as_radio_buttons)
    internal lateinit var containerRadioButtons: ConstraintLayout

    val viewModel: UserMenuViewModel by parentViewModels()
    lateinit var itemModel: MenuItemModel
    val listener: ItemCustomizationInteraction by parentFragmentDelegate()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvItemName.text = itemModel.name
        viewModel.itemCost.observe(this, Observer { value ->
            tvBill.text = String.format("Item Total   %s", Utils.formatIntegralCurrencyAmount(requireContext(), value))
        })
        tvRadioLabel1.text = itemModel.typeNames.get(0)
        groupRadio.visibility = View.VISIBLE
        when (itemModel.typeNames.size) {
            3 -> {
                tvRadioLabel2.text = itemModel.typeNames[1]
                tvRadioLabel3.text = itemModel.typeNames[2]
                btnRadio1.performClick()
            }
            2 -> {
                hideViews(btnRadio3, tvRadioLabel3, vLineHorizontalTwo)
                tvRadioLabel2.text = itemModel.typeNames[1]
                btnRadio1.performClick()
            }
            else -> groupRadio.visibility = View.GONE
        }
        if (itemModel.hasCustomizations()) {
            svContainerCustomization.visibility = View.VISIBLE
            for (group in itemModel.customizations) {
                listCustomizations.addView(ItemCustomizationGroupHolder(group!!, context, this).view)
            }
        } else {
            svContainerCustomization.visibility = View.GONE
        }
    }

    override fun onFieldClick(field: ItemCustomizationFieldModel, isSelected: Boolean) {
        if (isSelected) viewModel.addItemCustomization(field)
        else viewModel.removeItemCustomization(field)
    }

    @OnClick(R.id.footer_menu_customization)
    fun onDoneClick() {
        if (!viewModel.canOrder() && view != null) {
            Utils.warningSnack(view, "Not all required customizations are selected.")
            return
        }
        listener.onCustomizationDone()
        exitFragment()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onCustomizationCancel()
    }

    private fun exitFragment() = dismiss()

    private fun hideViews(vararg views: View) {
        for (v in views) v.visibility = View.GONE
    }

    @OnClick(R.id.rb_menu_customization_type_1, R.id.rb_menu_customization_type_2, R.id.rb_menu_customization_type_3)
    fun onChangeType(v: View) {
        btnRadio1.isSelected = false
        btnRadio2.isSelected = false
        btnRadio3.isSelected = false
        when (v.id) {
            R.id.rb_menu_customization_type_1 -> {
                selectType(0)
                groupRadio.check(R.id.rb_menu_customization_type_1)
            }
            R.id.rb_menu_customization_type_2 -> {
                selectType(1)
                groupRadio.check(R.id.rb_menu_customization_type_2)
            }
            R.id.rb_menu_customization_type_3 -> {
                if (itemModel.typeCosts.size == 3) selectType(2) else if (itemModel.typeCosts.size == 2) selectType(1)
                groupRadio.check(R.id.rb_menu_customization_type_3)
            }
            else -> selectType(0)
        }
    }

    private fun selectType(position: Int) = viewModel.setSelectedType(position)

    companion object {
        fun newInstance(item: MenuItemModel) = ItemCustomizationBottomSheetFragment().apply { itemModel = item }
    }

    interface ItemCustomizationInteraction {
        fun onCustomizationDone()
        fun onCustomizationCancel()
    }
}