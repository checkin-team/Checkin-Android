package com.checkin.app.checkin.misc.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R


sealed class HeaderView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, @LayoutRes val layoutRes: Int) : LinearLayout(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, layoutRes, this)
    }

    abstract fun setTextSize(size: Float)
}

class HeaderWithShareView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : HeaderView(context, attrs, defStyleAttr, R.layout.incl_header_with_share) {
    @BindView(R.id.tv_header_name)
    internal lateinit var tvName: TextView

    init {
        ButterKnife.bind(this)
    }

    fun bind(name: String) {
        tvName.text = name
    }

    override fun setTextSize(size: Float) {
        tvName.textSize = size
    }
}
