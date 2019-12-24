package com.checkin.app.checkin.manager.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.parentViewModels
import com.checkin.app.checkin.manager.adapters.ManagerInactiveTableAdapter
import com.checkin.app.checkin.manager.adapters.ManagerInactiveTableAdapter.ManagerTableInitiate
import com.checkin.app.checkin.manager.viewmodels.ManagerASLiveTablesViewModel
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.session.models.RestaurantTableModel

class ManagerInactiveTableBottomSheetFragment : BaseBottomSheetFragment(), ManagerTableInitiate {
    override val rootLayout: Int = R.layout.fragment_manager_inactive_tables

    @BindView(R.id.rv_mw_table)
    internal lateinit var rvTable: RecyclerView

    private val viewModel: ManagerASLiveTablesViewModel by parentViewModels()
    private val mInactiveAdapter: ManagerInactiveTableAdapter = ManagerInactiveTableAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvTable.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rvTable.adapter = mInactiveAdapter

        viewModel.inactiveTables.observe(this, Observer { if (it?.status == Resource.Status.SUCCESS && it.data != null) mInactiveAdapter.setData(it.data) })
    }

    override fun onClickInactiveTable(tableModel: RestaurantTableModel) {
        val builder = AlertDialog.Builder(requireContext()).setTitle(tableModel.table)
                .setMessage("Do you want to initiate the session?")
                .setPositiveButton("Done") { dialog: DialogInterface?, which: Int -> viewModel.processQrPk(tableModel.qrPk) }
                .setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.cancel() }
        builder.show()
    }
}