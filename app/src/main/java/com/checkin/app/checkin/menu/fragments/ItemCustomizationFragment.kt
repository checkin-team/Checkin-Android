package com.checkin.app.checkin.menu.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.renderscript.RenderScript
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.Menu.Model.ItemCustomizationFieldModel
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.Menu.UserMenu.ItemCustomizationGroupHolder
import com.checkin.app.checkin.Menu.UserMenu.MenuViewModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.RSBlurProcessor
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.Utility.parentFragmentDelegate
import com.checkin.app.checkin.misc.fragments.BaseFragment


class ItemCustomizationFragment : BaseFragment(), ItemCustomizationGroupHolder.CustomizationGroupInteraction {
    override val rootLayout = R.layout.fragment_as_menu_item_customization

    @BindView(R.id.container_menu_customization)
    internal lateinit var vMenuCustomizations: ConstraintLayout
    @BindView(R.id.dark_back_menu_customization)
    internal lateinit var vDarkBack: ImageView
    @BindView(R.id.btn_menu_customization_done)
    internal lateinit var btnDone: Button
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

    val viewModel: MenuViewModel by activityViewModels()
    val interactionListener: ItemCustomizationInteraction by parentFragmentDelegate()

    private var item: MenuItemModel? = null

    private val blurBackRunnable = {
        val bgView = requireActivity().findViewById<View>(android.R.id.content)
        val renderScript = RenderScript.create(requireContext())
        vDarkBack.setImageBitmap(RSBlurProcessor(renderScript).blur(Utils.getBitmapFromView(bgView), 25f, 1))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = super.onCreateView(inflater, container, savedInstanceState).also {
        it?.post(blurBackRunnable)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        item?.let { setupUi(it) }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUi(item: MenuItemModel) {
        vDarkBack.visibility = View.GONE
        vDarkBack.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onBackPressed()
                true
            }
            false
        }

        tvItemName.text = item.name
        viewModel.itemCost.observe(this, Observer {
            it?.let { value ->
                tvBill.text = String.format("Item Total   %s", Utils.formatIntegralCurrencyAmount(requireContext(), value))
            }
        })
        tvRadioLabel1.text = item.typeNames[0]
        groupRadio.visibility = View.VISIBLE
        when (item.typeNames.size) {
            3 -> {
                tvRadioLabel2.text = item.typeNames[1]
                tvRadioLabel3.text = item.typeNames[2]
                btnRadio1.performClick()
            }
            2 -> {
                hideViews(btnRadio3, tvRadioLabel3, vLineHorizontalTwo)
                setViewInCenter()
                tvRadioLabel2.text = item.typeNames[1]
                btnRadio1.performClick()
            }
            else -> groupRadio.visibility = View.GONE
        }
        if (item.hasCustomizations()) {
            svContainerCustomization.visibility = View.VISIBLE
            for (group in item.customizations) {
                listCustomizations.addView(ItemCustomizationGroupHolder(group, context, this).view)
            }
        } else {
            svContainerCustomization.visibility = View.GONE
        }

        vMenuCustomizations.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up))
        showDarkBack()
    }

    private fun setViewInCenter() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(containerRadioButtons)
        constraintSet.connect(R.id.rb_menu_customization_type_1, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(R.id.rb_menu_customization_type_1, ConstraintSet.END, R.id.line_horizontal, ConstraintSet.START)
        constraintSet.setHorizontalChainStyle(R.id.rb_menu_customization_type_1, ConstraintSet.CHAIN_SPREAD)

        constraintSet.connect(R.id.rb_menu_customization_type_2, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(R.id.rb_menu_customization_type_2, ConstraintSet.START, R.id.line_horizontal, ConstraintSet.END)

        constraintSet.connect(R.id.tv_menu_customization_radio_1, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(R.id.tv_menu_customization_radio_1, ConstraintSet.TOP, R.id.rb_menu_customization_type_1, ConstraintSet.BOTTOM)
        //        constraintSet.connect(R.id.tv_menu_customization_radio_1, ConstraintSet.END ,R.id.line_horizontal, ConstraintSet.START);
        constraintSet.setHorizontalChainStyle(R.id.tv_menu_customization_radio_1, ConstraintSet.HORIZONTAL_GUIDELINE)

        constraintSet.connect(R.id.tv_menu_customization_radio_2, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(R.id.tv_menu_customization_radio_2, ConstraintSet.TOP, R.id.rb_menu_customization_type_2, ConstraintSet.BOTTOM)
        constraintSet.connect(R.id.tv_menu_customization_radio_2, ConstraintSet.START, R.id.line_horizontal, ConstraintSet.END, 30)

        constraintSet.applyTo(containerRadioButtons)
    }

    private fun exitFragment() {
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                parentFragmentManager.inTransaction {
                    remove(this@ItemCustomizationFragment)
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        vMenuCustomizations.startAnimation(anim)
        hideDarkBack()
    }

    private fun hideViews(vararg views: View) {
        for (v in views) v.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.removeCallbacks(blurBackRunnable)
    }

    override fun onFieldClick(field: ItemCustomizationFieldModel, isSelected: Boolean) {
        if (isSelected) {
            viewModel.addItemCustomization(field)
        } else {
            viewModel.removeItemCustomization(field)
        }
    }

    @OnClick(R.id.btn_menu_customization_done)
    fun onDoneClick() {
        if (!viewModel.canOrder() && view != null) {
            Utils.warningSnack(view, "Not all required customizations are selected.")
            return
        }
        interactionListener.onCustomizationDone()
        exitFragment()
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
                if (item!!.typeCosts.size == 3)
                    selectType(2)
                else if (item!!.typeCosts.size == 2) selectType(1)
                groupRadio.check(R.id.rb_menu_customization_type_3)
            }
            else -> selectType(0)
        }
    }

    private fun selectType(position: Int) = viewModel.setSelectedType(position)

    override fun onBackPressed(): Boolean {
        interactionListener.onCustomizationCancel()
        exitFragment()
        return true
    }

    private fun showDarkBack() {
        vDarkBack.animate()
                .alpha(1.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        vDarkBack.visibility = View.VISIBLE
                    }
                })
                .setDuration(100L)
                .start()
    }

    private fun hideDarkBack() {
        vDarkBack.animate()
                .alpha(0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (vDarkBack != null)
                            vDarkBack.visibility = View.GONE
                    }
                })
                .start()
    }

    interface ItemCustomizationInteraction {
        fun onCustomizationDone()

        fun onCustomizationCancel()
    }

    companion object {
        private val TAG = ItemCustomizationFragment::class.java.simpleName

        fun newInstance(item: MenuItemModel) = ItemCustomizationFragment().apply {
            this.item = item
        }
    }
}
