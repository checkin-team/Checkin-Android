package com.checkin.app.checkin.manager.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
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
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.session.models.RestaurantTableModel
import com.checkin.app.checkin.utility.parentViewModels
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class ManagerInactiveTableBottomSheetFragment : BaseBottomSheetFragment(), ManagerTableInitiate {
    override val rootLayout: Int = R.layout.fragment_manager_inactive_tables

    @BindView(R.id.rv_mw_table)
    internal lateinit var rvTable: RecyclerView

    private val viewModel: ManagerASLiveTablesViewModel by parentViewModels()
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
        val builder = AlertDialog.Builder(requireContext()).setTitle(tableModel.table)
                .setMessage("Do you want to initiate the session?")
                .setPositiveButton("Done") { _, _ ->
                    when (behaviorOnClickTable) {
                        BEHAVIOUR_NEW_TABLE -> viewModel.processQrPk(tableModel.qrPk)
                        BEHAVIOUR_SWITCH_TABLE -> getSharedViewModel<ManagerSessionViewModel>().switchTable(tableModel.qrPk)
                    }
                    dismiss()
                }
                .setNegativeButton("Cancel") { _, _ -> dialog?.cancel() }
        builder.show()
    }

    companion object {
        private const val KEY_BEHAVIOUR_CLICK_TABLE = "tables.click_behaviour"
        private const val BEHAVIOUR_SWITCH_TABLE = 1
        private const val BEHAVIOUR_NEW_TABLE = 0

        fun forSwitchTable() = ManagerInactiveTableBottomSheetFragment().apply {
            arguments = bundleOf(KEY_BEHAVIOUR_CLICK_TABLE to BEHAVIOUR_SWITCH_TABLE)
        }

        fun forNewTable() = ManagerInactiveTableBottomSheetFragment()
    }
}