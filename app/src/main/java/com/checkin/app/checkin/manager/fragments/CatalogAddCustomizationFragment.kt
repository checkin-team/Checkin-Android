package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.checkin.app.checkin.R
import com.checkin.app.checkin.databinding.FragmentCatalogAddCustomizationBinding
import com.checkin.app.checkin.databinding.FragmentCatalogAddGroupBinding
import com.checkin.app.checkin.manager.activities.CatalogActivity
import com.checkin.app.checkin.manager.adapters.CatalogCustomizationAdapter
import com.checkin.app.checkin.manager.adapters.CatalogVariantAdapter
import com.checkin.app.checkin.manager.adapters.CustomizationClickListener
import com.checkin.app.checkin.manager.models.CustomizationFieldModel
import com.checkin.app.checkin.manager.models.ItemCustomizationModel
import com.checkin.app.checkin.manager.models.ItemVariantModel
import com.checkin.app.checkin.manager.viewmodels.CatalogViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

 class CatalogAddCustomizationFragment : BaseFragment(), CustomizationClickListener {

    override val rootLayout: Int = R.layout.fragment_catalog_add_customization
    private val mViewModel: CatalogViewModel by activityViewModels()
    private lateinit var adapter: CatalogCustomizationAdapter
    private var _binding: FragmentCatalogAddCustomizationBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCatalogAddCustomizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CatalogCustomizationAdapter(this)
        binding.rvCustomization.adapter = adapter
        setUpObservers()
        binding.tvAddGroup.setOnClickListener {
            mViewModel.mCustomizationList.value?.add(ItemCustomizationModel())
            mViewModel.mCustomizationList.value = mViewModel.mCustomizationList.value
        }
        binding.btnSaveCustomization.setOnClickListener {
            postCustomizations()
        }
    }

     private fun postCustomizations() {
     }

     private fun setUpObservers() {
        mViewModel.mCustomizationList.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })
    }

    override fun onStart() {
        super.onStart()
        binding.btnSaveCustomization.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_catalog, CatalogAddItemsFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        (requireActivity() as CatalogActivity).binding.tvActionBarTitle.text = "Add Ons"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CatalogAddCustomizationFragment()
    }

     override fun onItemClicked(position: Int) {
         mViewModel.mCustomizationList.value?.get(position)?.listOfFields?.add(
             CustomizationFieldModel()
         )
         mViewModel.mCustomizationList.value = mViewModel.mCustomizationList.value
     }
 }