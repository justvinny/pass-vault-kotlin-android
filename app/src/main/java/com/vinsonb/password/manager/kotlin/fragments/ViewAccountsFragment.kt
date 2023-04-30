package com.vinsonb.password.manager.kotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vinsonb.password.manager.kotlin.ui.features.viewaccount.ViewAccountScreen
import com.vinsonb.password.manager.kotlin.ui.features.viewaccount.ViewAccountViewModel
import com.vinsonb.password.manager.kotlin.utilities.withComposeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewAccountsFragment : Fragment() {
    private val viewModel: ViewAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = withComposeView(requireContext()) {
        ViewAccountScreen(viewModel = viewModel)
    }
}