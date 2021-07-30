package com.checkin.app.checkin.manager.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.checkin.app.checkin.R
import com.checkin.app.checkin.manager.viewmodels.CatalogViewModel

class CatalogGroupChooserDialogFragment(val items: List<String>): DialogFragment() {
    private val mViewModel: CatalogViewModel by activityViewModels()
    private lateinit var mBuilder: AlertDialog.Builder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder= AlertDialog.Builder(requireActivity())
        mBuilder.setItems(items.toTypedArray(), DialogInterface.OnClickListener { _, position ->
            mViewModel.setGroupPk(position)
        })
        mBuilder.setTitle("Choose Group")
        mBuilder.setPositiveButton("Add Group", DialogInterface.OnClickListener { _, _ ->
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_catalog, CatalogAddGroupFragment.newInstance())
                .addToBackStack(null)
                .commit()
        })
        return mBuilder.create()
    }

}