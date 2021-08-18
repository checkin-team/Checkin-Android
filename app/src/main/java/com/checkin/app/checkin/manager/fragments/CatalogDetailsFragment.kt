package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.databinding.FragmentCatalogDetailsBinding
import com.checkin.app.checkin.manager.activities.CatalogActivity
import com.checkin.app.checkin.manager.models.CatalogMenuModel
import com.checkin.app.checkin.manager.viewmodels.CatalogViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.Utils

class CatalogDetailsFragment : BaseFragment() {

    override val rootLayout: Int = R.layout.fragment_catalog_details
    private var _binding: FragmentCatalogDetailsBinding? = null
    val binding get() = _binding!!
    private val mViewModel: CatalogViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCatalogDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etGSTNumber.doAfterTextChanged {
            if (it.isNullOrEmpty()) {
                binding.financeGroup.visibility = View.GONE
            } else {
                binding.financeGroup.visibility = View.VISIBLE
            }
        }
        binding.btnCreateMenu.setOnClickListener {
            createMenu()
        }
    }

    fun createMenu() {
        val text = binding.etMenuName.text.toString()
        when {
            text.isNullOrEmpty() -> {
                Utils.toast(requireActivity(), "Menu name cannot be empty")
            }
            text.length > 50 -> {
                Utils.toast(requireActivity(), "Menu name cannot be longer than 50 characters")
            }
            else -> {
                mViewModel.createMenu(CatalogMenuModel(name = text, isAvailable = true))
                mViewModel.getMenu().observe(viewLifecycleOwner, Observer { input ->
                    if (input != null && input.status == Resource.Status.SUCCESS && input.data != null) {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_catalog, CatalogAddGroupFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                    }
                })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as CatalogActivity).binding.tvActionBarTitle.text = "Catalog Details"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CatalogDetailsFragment()
    }
}