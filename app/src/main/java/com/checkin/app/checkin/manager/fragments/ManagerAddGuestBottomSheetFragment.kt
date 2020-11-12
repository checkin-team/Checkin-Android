package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.util.Log
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
import com.checkin.app.checkin.manager.epoxy.guestInfoModelWithHolder
import com.checkin.app.checkin.manager.listeners.GuestListListener
import com.checkin.app.checkin.manager.models.GuestDetailsModel
import com.checkin.app.checkin.manager.viewmodels.ManagerASLiveTablesViewModel
import com.checkin.app.checkin.manager.viewmodels.ManagerGuestListViewModel
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.parentViewModels
import kotlin.random.Random

class ManagerAddGuestBottomSheetFragment : BaseBottomSheetFragment(), GuestListListener {
    override val rootLayout: Int = R.layout.fragment_manager_add_guest

    private val guestListViewModel: ManagerGuestListViewModel by viewModels()
    private val viewModel: ManagerASLiveTablesViewModel by parentViewModels()

    private val guestList by lazy {
        guestListViewModel.guestList
    }

    private val tableNumber by lazy {
        guestListViewModel.tableNumber
    }

    private val qrpk by lazy {
        guestListViewModel.qrpk
    }

    private val isGuestAdded by lazy {
        guestListViewModel.isGuestAdded
    }

    @BindView(R.id.tv_add_guest_room_no)
    internal lateinit var tvAddTableNo: TextView

    @BindView(R.id.epoxy_rv_guest_list)
    internal lateinit var epoxyGuestList: EpoxyRecyclerView

    @OnClick(R.id.btn_guest_list_add_guest)
    fun addGuest() {
        guestList.add(GuestDetailsModel("+91", ""))
        epoxyGuestList.requestModelBuild()
    }

    @OnClick(R.id.btn_guest_list_checkin)
    fun guestListCheckin() {
        viewModel.processQrPk(qrpk)
    }

    @OnClick(R.id.btn_guest_list_cancel)
    fun guestListCancel() {
        dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyGuestList.withModels {
            guestList.forEachIndexed { index, model ->
                guestInfoModelWithHolder {
                    id(index.toString() + model.name + Random.nextInt(0, 100))
                    index(index)
                    model(model)
                    listener(this@ManagerAddGuestBottomSheetFragment)
                }
            }
        }
        guestListViewModel.tableNumber = arguments?.getString(KEY_TABLE_NUMBER) ?: ""
        guestListViewModel.qrpk = arguments?.getLong(KEY_QR_PK) ?: -1
        tvAddTableNo.text = "Room #$tableNumber"

        viewModel.sessionInitiated.observe(viewLifecycleOwner, Observer {
            it?.let { qrResultModelResource ->
                if (qrResultModelResource.status === Resource.Status.SUCCESS && qrResultModelResource.data != null) {
                    val sessionPk = qrResultModelResource.data.sessionPk
                    // HACK: A flag is being used to check if the guest list was already added
                    if (isGuestAdded) {
                        return@Observer
                    }
                    guestListViewModel.addGuestList(sessionPk)

                } else {
                    Utils.toast(requireContext(), qrResultModelResource.message)
                }
            }
        })

        guestListViewModel.guestLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.status == Resource.Status.SUCCESS) {
                    guestListViewModel.setGuestAdded()
                    try {
                        val parentFragment = parentFragment as ManagerInactiveTableBottomSheetFragment
                        parentFragment.dismiss()
                    } catch (e: TypeCastException) {
                        Log.e(TAG, e.toString(), e)
                    }
                    dismiss()
                } else {
                    Utils.toast(requireContext(), it.message)
                }
            }
        })
    }

    override fun updateName(index: Int, name: String) {
        guestList[index] = guestList[index].copy(name = name)
    }

    override fun updateContact(index: Int, number: String) {
        guestList[index] = guestList[index].copy(contact = number)
    }

    companion object {
        private const val KEY_TABLE_NUMBER = "key.table.number"
        private const val KEY_QR_PK = "key.qr.pk"
        private const val TAG = "ManagerAddGuest"
        fun newInstance(tableNumber: String, qrpk: Long) = ManagerAddGuestBottomSheetFragment().apply {
            arguments = bundleOf(KEY_TABLE_NUMBER to tableNumber, KEY_QR_PK to qrpk)
        }
    }

}