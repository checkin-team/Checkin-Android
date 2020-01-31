package com.checkin.app.checkin.misc.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.utility.inTransaction

open class SectionFragmentHolderView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
        @LayoutRes private val layoutRes: Int = R.layout.view_collapsible_section_collapsed,
        protected val fragmentContainerId: Int = ViewCompat.generateViewId()
) : ConstraintLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.tv_collapsible_section_text)
    internal lateinit var tvSectionText: TextView
    @BindView(R.id.frg_container_collapsible_section)
    internal lateinit var frgContainer: ViewGroup
    @BindView(R.id.im_collapsible_section_dropdown)
    internal lateinit var imDropDown: ImageView

    init {
        View.inflate(context, layoutRes, this)
        ButterKnife.bind(this)
        initUi(context, attrs)

        setBackgroundResource(R.drawable.bordered_rectangle_brownish_grey)
    }

    private fun initUi(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.SectionFragmentHolderView)
            tvSectionText.text = typedArray.getString(R.styleable.SectionFragmentHolderView_sectionText)
                    ?: "Heading"
            typedArray.recycle()
        } ?: run {
            tvSectionText.text = "Heading"
        }
        // To generate unique ID always so that fragment can be added
        frgContainer.id = fragmentContainerId
    }

    fun attachFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        fragmentManager.inTransaction {
            add(frgContainer.id, fragment)
        }
    }
}
