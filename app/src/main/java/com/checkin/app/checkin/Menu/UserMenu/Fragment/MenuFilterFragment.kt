package com.checkin.app.checkin.Menu.UserMenu.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.renderscript.RenderScript
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.checkin.app.checkin.Menu.Model.MenuItemModel.AVAILABLE_MEAL
import com.checkin.app.checkin.Menu.UserMenu.Adapter.FilterGroupAdapter
import com.checkin.app.checkin.Menu.UserMenu.MenuViewModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.*

class MenuFilterFragment : Fragment() {
    private lateinit var unbinder: Unbinder

    @BindView(R.id.crf_as_menu_filter)
    internal lateinit var clipMenuFilter: ClipRevealFrame
    @BindView(R.id.dark_back_as_menu_filter)
    internal lateinit var vDarkBack: ImageView
    @BindView(R.id.container_as_menu_filter)
    internal lateinit var containerFilter: ViewGroup
    @BindView(R.id.rv_as_menu_groups)
    internal lateinit var rvFilterCategories: RecyclerView
    @BindView(R.id.container_as_menu_filter_clear)
    internal lateinit var containerFilterClear: ViewGroup
    @BindView(R.id.im_as_menu_filter_breakfast)
    internal lateinit var imBreakfast: ImageView
    @BindView(R.id.im_as_menu_filter_lunch)
    internal lateinit var imLunch: ImageView
    @BindView(R.id.im_as_menu_filter_dinner)
    internal lateinit var imDinner: ImageView
    @BindView(R.id.tv_as_menu_filter_breakfast)
    internal lateinit var tvBreakfast: TextView
    @BindView(R.id.tv_as_menu_filter_lunch)
    internal lateinit var tvLunch: TextView
    @BindView(R.id.tv_as_menu_filter_dinner)
    internal lateinit var tvDinner: TextView
    @BindView(R.id.rb_as_menu_filter_low_high)
    internal lateinit var rbLowHigh: RadioButton
    @BindView(R.id.rb_as_menu_filter_high_low)
    internal lateinit var rbHighLow: RadioButton

    private lateinit var mViewModel: MenuViewModel
    private lateinit var mAdapter: FilterGroupAdapter
    private var isFilterShown = false
    private var mListener: MenuFilterInteraction? = null

    private val blurBackRunnable = {
        val bgView = requireActivity().findViewById<ViewGroup>(android.R.id.content)
        val renderScript = RenderScript.create(requireContext())
        vDarkBack.setImageBitmap(RSBlurProcessor(renderScript).blur(Utils.getBitmapFromView(bgView), 25f, 1))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_as_menu_filter, container, false)
        unbinder = ButterKnife.bind(this, view)

        RevealCircleAnimatorHelper
                .create(this, container)
                .start(view)

//        vDarkBack.post(blurBackRunnable)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vDarkBack.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideFilter(event.x, event.y)
                true
            } else false
        }

        mAdapter = FilterGroupAdapter(null, object : FilterGroupAdapter.CategoryInteraction {
            override fun onClick(category: String?) = category?.let {
                mListener?.filterByCategory(it)
                hideFilter()
            } ?: Unit
        })
        rvFilterCategories.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rvFilterCategories.adapter = mAdapter

        mViewModel = ViewModelProviders.of(requireActivity()).get(MenuViewModel::class.java)
        mViewModel.groupName.observe(this, Observer(mAdapter::setCategories))
        mViewModel.filteredString.observe(this, Observer {
            if (it != null) {
                containerFilterClear.visibility = View.VISIBLE
                when {
                    it.equals("Breakfast", ignoreCase = true) -> {
                        tvBreakfast.setTextColor(resources.getColor(R.color.primary_red))
                        imBreakfast.setImageDrawable(resources.getDrawable(R.drawable.ic_menu_filter_breakfast_rouge))
                    }
                    it.equals("Lunch", ignoreCase = true) -> {
                        tvLunch.setTextColor(resources.getColor(R.color.primary_red))
                        imLunch.setImageDrawable(resources.getDrawable(R.drawable.ic_menu_filter_lunch_rouge))
                    }
                    it.equals("Dinner", ignoreCase = true) -> {
                        tvDinner.setTextColor(resources.getColor(R.color.primary_red))
                        imDinner.setImageDrawable(resources.getDrawable(R.drawable.ic_menu_filter_dinner_rouge))
                    }
                    it.equals("Low-High", ignoreCase = true) -> {
                        rbLowHigh.isChecked = true
                        rbHighLow.isChecked = false
                    }
                    it.equals("High-Low", ignoreCase = true) -> {
                        rbLowHigh.isChecked = false
                        rbHighLow.isChecked = true
                    }
                }
            } else {
                containerFilterClear.visibility = View.GONE
            }
        })
        showFilter()
    }

    @OnClick(R.id.container_as_menu_filter_breakfast, R.id.container_as_menu_filter_lunch, R.id.container_as_menu_filter_dinner)
    fun filterByAvailableMeal(v: View) {
        when (v.id) {
            R.id.container_as_menu_filter_breakfast -> mViewModel.filterMenuGroups(AVAILABLE_MEAL.BREAKFAST)
            R.id.container_as_menu_filter_lunch -> mViewModel.filterMenuGroups(AVAILABLE_MEAL.LUNCH)
            R.id.container_as_menu_filter_dinner -> mViewModel.filterMenuGroups(AVAILABLE_MEAL.DINNER)
        }
        mListener!!.filterByAvailableMeals()
        hideFilter()
    }

    @OnClick(R.id.container_as_menu_filter_clear)
    fun resetFilter() {
        mViewModel.clearFilters()
        mListener!!.resetFilters()
        hideFilter()
    }

    @OnClick(R.id.rb_as_menu_filter_high_low, R.id.rb_as_menu_filter_low_high)
    fun sortMenuItems(v: View) {
        when (v.id) {
            R.id.rb_as_menu_filter_high_low -> mViewModel.sortMenuItems(false)
            R.id.rb_as_menu_filter_low_high -> mViewModel.sortMenuItems(true)
        }
        mListener?.sortItems()
        hideFilter()
    }

    fun onBackPressed(): Boolean = if (isFilterShown) {
        hideFilter()
        true
    } else false

    private fun showFilter() {
        isFilterShown = true
        mListener?.onShowFilter()
    }

    private fun hideFilter() {
        val cx = view?.right ?: 0
        val cy = view?.bottom ?: 0
        hideFilter(cx.toFloat(), cy.toFloat())
    }

    private fun hideFilter(cx: Float, cy: Float) {
        isFilterShown = false
        mListener?.onHideFilter()

        val radius = getEnclosingCircleRadius(view!!, cx, cy)
        val unrevealAnim = AnimUtils.createCircularRevealAnimator(clipMenuFilter, cx.toInt(), cy.toInt(), radius, 0f)
        unrevealAnim.interpolator = AccelerateInterpolator(2f)

        unrevealAnim.addListener(onEnd = {
            requireFragmentManager().beginTransaction()
                    .remove(this)
                    .commit()
            requireFragmentManager().executePendingTransactions()
        })
        unrevealAnim.start()
    }

    private fun getEnclosingCircleRadius(v: View, cx: Float, cy: Float): Float = Math.hypot(Math.max(cx, v.width - cx).toDouble(), Math.max(cy, v.height - cy).toDouble()).toFloat()

    override fun onDestroy() {
        super.onDestroy()
        vDarkBack.removeCallbacks(blurBackRunnable)
        unbinder.unbind()
    }

    interface MenuFilterInteraction {
        fun onShowFilter()

        fun onHideFilter()

        fun filterByCategory(category: String)

        fun sortItems()

        fun resetFilters()

        fun filterByAvailableMeals()
    }

    companion object {
        private val TAG = MenuFilterFragment::class.java.simpleName

        fun newInstance(listener: MenuFilterInteraction, sourceView: View): MenuFilterFragment = MenuFilterFragment().apply {
            mListener = listener
            arguments = Bundle()
            RevealCircleAnimatorHelper.addBundleValues(arguments!!, sourceView)
        }
    }
}
