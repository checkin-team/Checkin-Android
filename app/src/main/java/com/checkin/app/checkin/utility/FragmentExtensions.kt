package com.checkin.app.checkin.utility

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) = beginTransaction().func().commit()

inline fun <reified VM : ViewModel> Fragment.parentViewModels() = viewModels<VM>({ if (parentFragment != null) requireParentFragment() else requireActivity() })
