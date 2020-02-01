package com.checkin.app.checkin.Waiter.Fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Waiter.Model.SessionContactModel
import com.checkin.app.checkin.Waiter.WaiterTableViewModel
import com.checkin.app.checkin.Waiter.WaiterWorkViewModel
import com.checkin.app.checkin.data.notifications.MessageObjectModel
import com.checkin.app.checkin.data.notifications.MessageUtils
import com.checkin.app.checkin.data.resource.Resource.Status
import com.checkin.app.checkin.menu.activities.ShopMenuActivity
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.models.SessionBriefModel
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.parentActivityDelegate
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class WaiterTableFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_waiter_table

    @BindView(R.id.container_waiter_table_actions)
    internal lateinit var containerActions: ViewGroup
    @BindView(R.id.tv_waiter_table_members_count)
    internal lateinit var tvMembersCount: TextView
    @BindView(R.id.tv_waiter_session_bill)
    internal lateinit var tvSessionBill: TextView
    @BindView(R.id.container_waiter_no_member)
    internal lateinit var containerWaiterAddContact: ViewGroup
    @BindView(R.id.container_waiter_members_count)
    internal lateinit var containerMembersCount: ViewGroup

    private val mListener: WaiterTableInteraction by parentActivityDelegate()
    val viewModel: WaiterTableViewModel by viewModels()

    private val shopPk: Long by lazy { getSharedViewModel<WaiterWorkViewModel>().shopPk }
    private var mContactAddDialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null)
            return

        buildContactAddDialog()
        mContactAddDialog?.setOnDismissListener { Utils.setKeyboardVisibility(tvSessionBill, false) }

        viewModel.fetchSessionDetail(arguments!!.getLong(KEY_WAITER_TABLE_ID, 0))

        viewModel.sessionDetail.observe(this, Observer {
            it?.let { resource ->
                if (resource.status === Status.SUCCESS && resource.data != null) {
                    setupTableData(resource.data)
                } else if (resource.status === Status.ERROR_NOT_FOUND) {
                    endSession()
                }
            }
        })
        viewModel.checkoutData.observe(this, Observer {
            it?.let { resource ->
                if (resource.status === Status.SUCCESS && resource.data != null) {
                    Utils.toast(requireContext(), resource.data.message)
                    if (resource.data.isCheckout) endSession()
                } else if (resource.status !== Status.LOADING && resource.message != null) {
                    Utils.toast(requireContext(), resource.message)
                }
            }
        })
        viewModel.observableData.observe(this, Observer {
            it?.let { input ->
                if (input.status === Status.SUCCESS && input.data != null)
                    Utils.toast(requireContext(), "User contact details added successfully.")
                else if (input.status !== Status.LOADING && input.message != null)
                    Utils.toast(requireContext(), input.message)
            }
        })

        viewModel.fetchSessionContacts()

        viewModel.sessionContactListData.observe(this, Observer {
            it?.let { input ->
                if (input.status === Status.SUCCESS && input.data != null) {
                    if (input.data.size > 0) {
                        setupContactData(input.data.get(input.data.size - 1))
                    }
                } else if (input.status !== Status.LOADING && input.message != null)
                    Utils.toast(requireContext(), input.message)
            }
        })
    }

    private fun buildContactAddDialog() {
        mContactAddDialog = Dialog(requireContext())
        mContactAddDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mContactAddDialog!!.setContentView(R.layout.view_dialog_waiter_table_bill)
        mContactAddDialog!!.setCanceledOnTouchOutside(true)
        mContactAddDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mContactAddDialog!!.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        val etPhone = mContactAddDialog!!.findViewById<EditText>(R.id.et_contact_phone)
        val etEmail = mContactAddDialog!!.findViewById<EditText>(R.id.et_contact_email)
        val btnDone = mContactAddDialog!!.findViewById<Button>(R.id.btn_contact_done)

        mContactAddDialog!!.setOnShowListener { Utils.setKeyboardVisibility(etPhone, true) }
        mContactAddDialog!!.setOnCancelListener { Utils.setKeyboardVisibility(etPhone, false) }
        mContactAddDialog!!.setOnKeyListener { dialog, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.cancel()
                true
            }
            false
        }

        btnDone.setOnClickListener {
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()

            if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(email)) {
                Utils.toast(requireContext(), "Please enter at least phone number or email.")
                return@setOnClickListener
            }
            if (!TextUtils.isEmpty(phone) && !Patterns.PHONE.matcher(phone).matches()) {
                Utils.toast(requireContext(), "Please enter valid phone number.")
                return@setOnClickListener
            }
            if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Utils.toast(requireContext(), "Please enter valid email.")
                return@setOnClickListener
            }
            viewModel.postSessionContact(email, phone)
            mContactAddDialog!!.dismiss()
        }
    }

    private fun setupContactData(sessionContactModel: SessionContactModel) {
        val etPhone = mContactAddDialog!!.findViewById<EditText>(R.id.et_contact_phone)
        val etEmail = mContactAddDialog!!.findViewById<EditText>(R.id.et_contact_email)

        val email = sessionContactModel.email
        val phone = sessionContactModel.phone

        if (email != null)
            etEmail.setText(email)
        if (phone != null)
            etPhone.setText(phone)
    }

    // Most often causes NPE since fragment is over
    private fun endSession() {
        runCatching<Unit> { mListener.endSession(viewModel.sessionPk) }
    }

    private fun setupTableData(data: SessionBriefModel) {
        if (data.customerCount > 0) {
            containerWaiterAddContact.visibility = View.GONE
            containerMembersCount.visibility = View.VISIBLE
            tvMembersCount.text = data.formatCustomerCount()
        } else {
            containerWaiterAddContact.visibility = View.VISIBLE
            containerMembersCount.visibility = View.GONE
        }

        tvSessionBill.text = Utils.formatCurrencyAmount(requireContext(), data.bill)
        if (data.isRequestedCheckout) {
            containerActions.visibility = View.GONE
            showCollectBill()
        } else {
            containerActions.visibility = View.VISIBLE
            showEventList()
        }
    }

    private fun showEventList() {
        childFragmentManager.beginTransaction()
                .replace(R.id.container_waiter_table_fragment, WaiterTableEventFragment.newInstance())
                .commit()
    }

    private fun showCollectBill() {
        childFragmentManager.beginTransaction()
                .replace(R.id.container_waiter_table_fragment, WaiterTableCollectCashFragment.newInstance())
                .commit()
    }

    override fun updateScreen() {
        viewModel.updateResults()
    }

    @OnClick(R.id.btn_waiter_table_checkout)
    fun onClickCheckout() {
        viewModel.requestSessionCheckout()
    }

    @OnClick(R.id.btn_waiter_table_menu)
    fun onClickMenu() {
        ShopMenuActivity.openMenu(requireContext(), shopPk, viewModel.sessionPk)
    }

    @OnClick(R.id.container_waiter_no_member)
    fun onClickAddContact() {
        showAddContactDialog()
    }

    private fun showAddContactDialog() {
        mContactAddDialog!!.show()
    }

    override fun onResume() {
        super.onResume()
        MessageUtils.dismissNotification(requireContext(), MessageObjectModel.MESSAGE_OBJECT_TYPE.SESSION, viewModel.sessionPk)
        updateScreen()
    }

    interface WaiterTableInteraction {
        fun endSession(sessionPk: Long)
    }

    companion object {
        private const val KEY_WAITER_TABLE_ID = "waiter.table"

        fun newInstance(tableNumber: Long): WaiterTableFragment {
            val fragment = WaiterTableFragment()
            val bundle = Bundle()
            bundle.putLong(KEY_WAITER_TABLE_ID, tableNumber)
            fragment.arguments = bundle
            return fragment
        }
    }
}
