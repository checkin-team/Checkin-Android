package com.checkin.app.checkin.manager.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.adapters.ManagerInactiveTableAdapter
import com.checkin.app.checkin.manager.adapters.ManagerInactiveTableAdapter.ManagerTableInitiate
import com.checkin.app.checkin.manager.viewmodels.ManagerASLiveTablesViewModel
import com.checkin.app.checkin.manager.viewmodels.ManagerSessionViewModel
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.restaurant.models.RestaurantServiceType
import com.checkin.app.checkin.session.models.RestaurantTableModel
import com.checkin.app.checkin.utility.parentViewModels
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class ManagerInactiveTableBottomSheetFragment : BaseBottomSheetFragment(), ManagerTableInitiate {
    override val rootLayout: Int = R.layout.fragment_manager_inactive_tables

    @BindView(R.id.rv_mw_table)
    internal lateinit var rvTable: RecyclerView

    private val viewModel: ManagerASLiveTablesViewModel by parentViewModels()
    private val workViewModel: ManagerWorkViewModel by activityViewModels()

    private val mInactiveAdapter: ManagerInactiveTableAdapter = ManagerInactiveTableAdapter(this)
    private var behaviorOnClickTable: Int = BEHAVIOUR_NEW_TABLE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvTable.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rvTable.adapter = mInactiveAdapter

        viewModel.inactiveTables.observe(viewLifecycleOwner, Observer {
            if (it?.status == Resource.Status.SUCCESS && it.data != null) mInactiveAdapter.setData(it.data)
        })
        behaviorOnClickTable = arguments?.getInt(KEY_BEHAVIOUR_CLICK_TABLE, BEHAVIOUR_NEW_TABLE)
                ?: BEHAVIOUR_NEW_TABLE
    }

    override fun onClickInactiveTable(tableModel: RestaurantTableModel) {
        if (behaviorOnClickTable == BEHAVIOUR_NEW_TABLE && workViewModel.restaurantService.value?.data?.serviceType == RestaurantServiceType.HOTEL) {
            val split: List<String> = tableModel.table?.split(" ".toRegex())!!
            ManagerAddGuestBottomSheetFragment.newInstance((if (split.size > 1) split[1] else split[0]), tableModel.qrPk)
                    .apply {
                        setTargetFragment(this@ManagerInactiveTableBottomSheetFragment, RC_GUEST_CONTACT)
                    }.show(parentFragmentManager, null)
        } else {
            val builder = AlertDialog.Builder(requireContext()).setTitle(tableModel.table)
                    .setMessage("Do you want to initiate the session?")
                    .setPositiveButton("Done") { _, _ ->
                        when (behaviorOnClickTable) {
                            BEHAVIOUR_NEW_TABLE -> {
                                viewModel.processQrPk(tableModel.qrPk)
                            }
                            BEHAVIOUR_SWITCH_TABLE -> getSharedViewModel<ManagerSessionViewModel>().switchTable(tableModel.qrPk)
                        }
                        dismiss()
                    }
                    .setNegativeButton("Cancel") { _, _ -> dialog?.cancel() }
            builder.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GUEST_CONTACT && resultCode == Activity.RESULT_OK) dismiss()
    }

    companion object {
        private const val KEY_BEHAVIOUR_CLICK_TABLE = "tables.click_behaviour"
        private const val BEHAVIOUR_SWITCH_TABLE = 1
        private const val BEHAVIOUR_NEW_TABLE = 0

        private const val RC_GUEST_CONTACT = 123

        const val FRAGMENT_SHOW_TAG = "tables_fragment"

        fun forSwitchTable() = ManagerInactiveTableBottomSheetFragment().apply {
            arguments = bundleOf(KEY_BEHAVIOUR_CLICK_TABLE to BEHAVIOUR_SWITCH_TABLE)
        }

        fun forNewTable() = ManagerInactiveTableBottomSheetFragment()
    }
}