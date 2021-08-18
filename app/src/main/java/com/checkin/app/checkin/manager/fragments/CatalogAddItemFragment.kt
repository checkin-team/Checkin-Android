package com.checkin.app.checkin.manager.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.checkin.app.checkin.R
import com.checkin.app.checkin.databinding.FragmentCatalogAddItemBinding
import com.checkin.app.checkin.manager.activities.CatalogActivity
import com.checkin.app.checkin.manager.models.GroupMenuItemModel
import com.checkin.app.checkin.manager.viewmodels.CatalogViewModel
import com.checkin.app.checkin.misc.activities.SelectCropImageActivity
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.Utils
import java.io.File

class CatalogAddItemFragment : BaseFragment() {

    override val rootLayout: Int = R.layout.fragment_catalog_add_item
    private val mViewModel: CatalogViewModel by activityViewModels()
    private lateinit var itemImage: File
    private var _binding: FragmentCatalogAddItemBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCatalogAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchGroupsList()
        binding.etGroup.setOnClickListener {
            showGroupChooser()
        }
        binding.containerImage.setOnClickListener {
            addPic()
        }
        binding.btnSaveItem.setOnClickListener {
            if (mViewModel.groupPk == null) {
                Utils.toast(requireActivity(), "Add a group")
                return@setOnClickListener
            }
            if (binding.etItemName.text.isNullOrEmpty()) {
                Utils.toast(requireActivity(), "Add Item Name")
                return@setOnClickListener
            }
            if (binding.etItemName.text.toString().length > 80) {
                Utils.toast(requireActivity(), "Item name should be less than 80 chars")
                return@setOnClickListener
            }
            val listOfCheckedChipsIDs: List<Int> = binding.cgAvailableMeal.checkedChipIds
            val availableMeals = arrayListOf<String>()
            listOfCheckedChipsIDs.forEach {
                when(it) {
                    R.id.chip_breakfast -> availableMeals.add("brkfst")
                    R.id.chip_lunch -> availableMeals.add("lunch")
                    R.id.chip_dinner -> availableMeals.add("dinner")
                }
            }
            val costs = mViewModel.listOfPrice
//            mViewModel.createItem(GroupMenuItemModel(availableMeals, costs, ))
        }
    }

    private fun fetchGroupsList() {
        mViewModel.getGroupsList()
    }
    private fun showGroupChooser() {
        mViewModel.getGroupNames().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                CatalogGroupChooserDialogFragment(it).show(requireActivity().supportFragmentManager, "group chooser dialog fragment")
            }
        })
    }

    fun addPic() {
        val intent = Intent(requireContext(), SelectCropImageActivity::class.java)
        intent.putExtra(SelectCropImageActivity.KEY_FILE_SIZE_IN_MB, 4L)
        startActivityForResult(intent, SelectCropImageActivity.RC_CROP_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SelectCropImageActivity.RC_CROP_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.extras != null) {
                itemImage = data.extras!!.get(SelectCropImageActivity.KEY_IMAGE) as File
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.btnSaveItem.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_catalog, CatalogAddVariantFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        (requireActivity() as CatalogActivity).binding.tvActionBarTitle.text = "Add Item"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CatalogAddItemFragment()
    }
}