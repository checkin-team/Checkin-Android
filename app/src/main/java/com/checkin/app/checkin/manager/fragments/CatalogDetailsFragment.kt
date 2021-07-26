package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.checkin.app.checkin.R
import com.checkin.app.checkin.databinding.FragmentCatalogDetailsBinding
import com.checkin.app.checkin.manager.activities.CatalogActivity
import com.checkin.app.checkin.misc.fragments.BaseFragment

class CatalogDetailsFragment : BaseFragment() {

    override val rootLayout: Int = R.layout.fragment_catalog_details
    private var _binding: FragmentCatalogDetailsBinding? = null
    val binding get() = _binding!!

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
        binding.etCatalogGSTNumber.doAfterTextChanged {
            if (it.isNullOrEmpty()) {
                binding.financeGroup.visibility = View.GONE
            } else {
                binding.financeGroup.visibility = View.VISIBLE
            }
        }
        binding.button2.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_catalog, CatalogAddGroupFragment.newInstance())
                .addToBackStack(null)
                .commit()
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