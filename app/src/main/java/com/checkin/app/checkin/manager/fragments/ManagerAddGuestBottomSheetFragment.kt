package com.checkin.app.checkin.manager.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.holders.guestInfoModelWithHolder
import com.checkin.app.checkin.manager.listeners.GuestContactChangeListener
import com.checkin.app.checkin.manager.models.GuestContactModel
import com.checkin.app.checkin.manager.viewmodels.ManagerASLiveTablesViewModel
import com.checkin.app.checkin.manager.viewmodels.ManagerGuestListViewModel
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.utility.parentViewModels
import com.checkin.app.checkin.utility.toast

class ManagerAddGuestBottomSheetFragment : BaseBottomSheetFragment(), GuestContactChangeListener {
    override val rootLayout: Int = R.layout.fragment_manager_add_guest

    @BindView(R.id.tv_add_guest_room_no)
    internal lateinit var tvAddTableNo: TextView

    @BindView(R.id.epoxy_rv_guest_list)
    internal lateinit var epoxyGuestList: EpoxyRecyclerView

    private val guestListViewModel: ManagerGuestListViewModel by viewModels()
    private val viewModel: ManagerASLiveTablesViewModel by parentViewModels()

    private val guestList: ArrayList<GuestContactModel> by lazy { guestListViewModel.mvGuestContacts }
    private val tableNumber by lazy { arguments?.getString(KEY_TABLE_NUMBER) ?: "" }
    private val qrpk by lazy { arguments?.getLong(KEY_QR_PK) ?: -1 }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyGuestList.withModels {
            guestList.forEachIndexed { index, model ->
                guestInfoModelWithHolder {
                    id(index)
                    index(index)
                    model(model)
                    listener(this@ManagerAddGuestBottomSheetFragment)
                }
            }
        }
        tvAddTableNo.text = "Room #$tableNumber"

        viewModel.sessionInitiated.observe(viewLifecycleOwner, Observer {
            it?.let { qrResultModelResource ->
                if (qrResultModelResource.status === Resource.Status.SUCCESS && qrResultModelResource.data != null) {
                    val sessionPk = qrResultModelResource.data.sessionPk
                    // HACK: A flag is being used to check if the guest list was already added
                    if (guestListViewModel.isGuestAdded) {
                        return@Observer
                    }
                    guestListViewModel.postGuestList(sessionPk)
                } else if (it.status != Resource.Status.LOADING) {
                    toast(qrResultModelResource.message)
                    dismiss()
                }
            }
        })

        guestListViewModel.guestLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.status == Resource.Status.SUCCESS) {
                    guestListViewModel.setGuestAdded()
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    dismiss()
                } else if (it.status != Resource.Status.LOADING) {
                    toast(it.message)
                }
            }
        })
    }

    @OnClick(R.id.btn_guest_list_add_guest)
    fun addGuest() {
        guestListViewModel.addGuest(GuestContactModel("", ""))
        epoxyGuestList.requestModelBuild()
    }

    @OnClick(R.id.btn_guest_list_checkin)
    fun guestListCheckin() {
        guestList.forEach {
            if (!"^\\+?1?\\d{9,15}\$".toRegex().matches(it.phone)) {
                toast("The given mobile number is invalid, try again")
                return
            }
        }
        viewModel.processQrPk(qrpk)
    }

    @OnClick(R.id.btn_guest_list_cancel)
    fun guestListCancel() {
        dismiss()
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, null)
    }

    override fun updateName(index: Int, name: String) {
        guestList[index] = guestList[index].copy(name = name)
    }

    override fun updateContact(index: Int, number: String) {
        guestList[index] = guestList[index].copy(phone = number)
    }

    companion object {
        private const val KEY_TABLE_NUMBER = "key.table.number"
        private const val KEY_QR_PK = "key.qr.pk"

        fun newInstance(tableNumber: String, qrpk: Long) = ManagerAddGuestBottomSheetFragment().apply {
            arguments = bundleOf(KEY_TABLE_NUMBER to tableNumber, KEY_QR_PK to qrpk)
        }
    }
}