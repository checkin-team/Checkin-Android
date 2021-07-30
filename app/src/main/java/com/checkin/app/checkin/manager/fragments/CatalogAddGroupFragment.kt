package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.databinding.FragmentCatalogAddGroupBinding
import com.checkin.app.checkin.manager.activities.CatalogActivity
import com.checkin.app.checkin.manager.models.MenuGroupModel
import com.checkin.app.checkin.manager.viewmodels.CatalogViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.Utils
import com.google.android.material.chip.Chip

class CatalogAddGroupFragment : BaseFragment() {

    override val rootLayout: Int = R.layout.fragment_catalog_add_group
    private var _binding: FragmentCatalogAddGroupBinding? = null
    val binding get() = _binding!!
    private val mViewModel: CatalogViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCatalogAddGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = listOf("appetizer", "barbeque", "beer", "bread", "chinese",
            "cocktail", "coffee", "continental", "dessert", "dumpling", "frappe",
            "main", "mexican", "mocktail", "non-veg", "noodles", "pancake", "pasta",
            "pizza", "rice", "rum", "salad", "sandwich", "shake", "shots", "snack",
            "soda", "soup", "starter", "sweets", "tea", "vodka", "whisky", "wrap",
            "chef_spl")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_catalog_group_type, items)
        binding.menuGroupType.setAdapter(adapter)
        binding.button3.setOnClickListener {
           createGroup()
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as CatalogActivity).binding.tvActionBarTitle.text = "Add Group"

    }

    fun createGroup() {
        val text = binding.etGroupName.text.toString()
        when {
            text.isNullOrEmpty() -> {
                Utils.toast(requireActivity(), "Group name cannot be empty")
                return
            }
            text.length > 50 -> {
                Utils.toast(requireActivity(), "Group name cannot be longer than 50 characters")
                return
            }
        }
        if (binding.menuGroupType.text.isNullOrEmpty()) {
            Utils.toast(requireActivity(), "Icon Type cannot be empty")
            return
        }
        val category = binding.cgCategory.findViewById<Chip>(binding.cgCategory.checkedChipId).text.toString()
        val type = binding.menuGroupType.text.toString()
        mViewModel.createMenuGroup(MenuGroupModel(name = text, category = category, type = type))
        mViewModel.getMenuGroup().observe(viewLifecycleOwner, Observer { input ->
            if (input != null && input.status == Resource.Status.SUCCESS && input.data != null) {
                mViewModel.groupPk = input.data.pk.toLong()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_catalog, CatalogAddItemFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CatalogAddGroupFragment()
    }
}