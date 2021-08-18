package com.checkin.app.checkin.manager.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.checkin.app.checkin.R
import com.checkin.app.checkin.databinding.FragmentCatalogAddBinding
import com.checkin.app.checkin.manager.activities.CatalogActivity
import com.checkin.app.checkin.manager.activities.ManagerWorkActivity
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

class CatalogAddFragment : BaseFragment() {

    override val rootLayout: Int = R.layout.fragment_catalog_add
    private var _binding: FragmentCatalogAddBinding? = null
    val binding get() = _binding!!
    private val mViewModel: ManagerWorkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCatalogAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBegin.setOnClickListener {
            val intent = Intent(requireActivity(), CatalogActivity::class.java).apply { putExtra(CatalogActivity.KEY_RESTAURANT_PK, mViewModel.shopPk) }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): CatalogAddFragment = CatalogAddFragment()
    }
}