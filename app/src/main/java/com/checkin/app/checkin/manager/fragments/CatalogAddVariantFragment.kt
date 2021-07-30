package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.checkin.app.checkin.R
import com.checkin.app.checkin.databinding.FragmentCatalogAddVariantBinding
import com.checkin.app.checkin.manager.activities.CatalogActivity
import com.checkin.app.checkin.manager.adapters.CatalogVariantAdapter
import com.checkin.app.checkin.manager.models.ItemVariantModel
import com.checkin.app.checkin.manager.viewmodels.CatalogViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

class CatalogAddVariantFragment : BaseFragment() {

    override val rootLayout: Int = R.layout.fragment_catalog_add_variant
    private val mViewModel: CatalogViewModel by activityViewModels()
    private lateinit var adapter: CatalogVariantAdapter
    private var _binding: FragmentCatalogAddVariantBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCatalogAddVariantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        adapter = CatalogVariantAdapter()
        binding.rvVariants.adapter = adapter
        binding.tvAddMoreVariant.setOnClickListener {
            mViewModel.mVariantList.value?.add(ItemVariantModel())
            mViewModel.mVariantList.value = mViewModel.mVariantList.value
        }
        binding.btnSaveVariants.setOnClickListener {
            mViewModel.listOfTypes = mViewModel.mVariantList.value?.map {
                it.type
            } as ArrayList<String>
            mViewModel.listOfPrice = mViewModel.mVariantList.value?.map {
                it.price
            } as ArrayList<String>
        }
    }

    private fun setUpObservers() {
        mViewModel.mVariantList.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })
    }

    override fun onStart() {
        super.onStart()
        binding.btnSaveVariants.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container_catalog,
                    CatalogAddCustomizationFragment.newInstance()
                )
                .addToBackStack(null)
                .commit()
        }
        (requireActivity() as CatalogActivity).binding.tvActionBarTitle.text = "Variants"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CatalogAddVariantFragment()
    }
}