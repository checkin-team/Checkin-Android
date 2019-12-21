package com.checkin.app.checkin.session.scheduled

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.activesession.ActiveSessionPromoAdapter
import com.checkin.app.checkin.session.activesession.ActiveSessionPromoAdapter.onPromoCodeItemListener
import com.checkin.app.checkin.session.models.PromoDetailModel


class ScheduledSessionPromoFragment : BaseFragment(), onPromoCodeItemListener {
    override val rootLayout: Int = R.layout.activity_active_session_promo

    @BindView(R.id.rv_available_promos)
    internal lateinit var rvPromos: RecyclerView
    @BindView(R.id.ed_promo_code)
    internal lateinit var edPromoCode: EditText

    private val mViewModel: ScheduledSessionViewModel by activityViewModels()
    private lateinit var mAdapter: ActiveSessionPromoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.setOnTouchListener { _, _ -> true }
        rvPromos.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        mAdapter = ActiveSessionPromoAdapter(null, this)
        rvPromos.adapter = mAdapter
        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.promoCodes.observe(this, Observer {
            it?.let { listResource ->
                if (listResource.status === Resource.Status.SUCCESS && listResource.data != null) mAdapter.setData(listResource.data)
                else if (listResource.status == Resource.Status.ERROR_FORBIDDEN) parentFragmentManager.popBackStack()
                else if (listResource.status !== Resource.Status.LOADING) Utils.toast(requireContext(), listResource.message)
            }

        })
        mViewModel.observableData.observe(this, Observer {
            it?.let { objectNodeResource ->
                var msg = objectNodeResource.message
                if (objectNodeResource.status === Resource.Status.SUCCESS) {
                    mViewModel.resetObservableData()
                    parentFragmentManager.inTransaction {
                        remove(this@ScheduledSessionPromoFragment)
                    }
                    if (objectNodeResource.data != null && objectNodeResource.data.has("code")) {
                        msg = "Successfully applied " + objectNodeResource.data["code"]
                    }
                } else if (objectNodeResource.status === Resource.Status.ERROR_NOT_FOUND) {
                    msg = "Invalid Promo code."
                }
                Utils.toast(requireContext(), msg)
            }
        })

        mViewModel.fetchPromoCodes()
    }

    @OnClick(R.id.tv_apply_promo)
    fun onPromoCodeApplyClick() {
        val promoCode = edPromoCode.text.toString()
        if (!TextUtils.isEmpty(promoCode)) {
            mViewModel.availPromoCode(promoCode)
        }
    }

    override fun onPromoApply(promoModel: PromoDetailModel) {
        mViewModel.availPromoCode(promoModel.code)
    }

    companion object {
        const val FRAGMENT_TAG = "scheduled.promo"

        fun newInstance(): ScheduledSessionPromoFragment {
            return ScheduledSessionPromoFragment()
        }
    }
}
