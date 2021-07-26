package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.checkin.app.checkin.R
import com.checkin.app.checkin.databinding.FragmentCatalogAddItemBinding
import com.checkin.app.checkin.databinding.FragmentCatalogAddItemsBinding
import com.checkin.app.checkin.databinding.FragmentCatalogDetailsBinding
import com.checkin.app.checkin.manager.activities.CatalogActivity
import com.checkin.app.checkin.misc.fragments.BaseFragment

class CatalogAddItemsFragment : BaseFragment() {

    override val rootLayout: Int = R.layout.fragment_catalog_add_items
    private var _binding: FragmentCatalogAddItemsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCatalogAddItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as CatalogActivity).binding.tvActionBarTitle.text = "Menu Name"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CatalogAddItemsFragment()
    }
}